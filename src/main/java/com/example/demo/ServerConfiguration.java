package com.example.demo;

import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.ip.tcp.TcpReceivingChannelAdapter;
import org.springframework.integration.ip.tcp.TcpSendingMessageHandler;
import org.springframework.integration.ip.tcp.connection.AbstractServerConnectionFactory;
import org.springframework.integration.ip.tcp.connection.TcpConnectionEvent;
import org.springframework.integration.ip.tcp.connection.TcpNetServerConnectionFactory;
import org.springframework.messaging.MessageChannel;
import org.springframework.util.SocketUtils;

@Configuration()
@EnableIntegration
@IntegrationComponentScan
public class ServerConfiguration implements ApplicationListener<TcpConnectionEvent> {

    private final int port = 50000; //SocketUtils.findAvailableServerSocket(5000);

    @MessagingGateway(defaultRequestChannel="toTcp")
    public interface Gateway {
        String send(String in);
    }

    @Bean
    public AbstractServerConnectionFactory serverFactory() {
        System.out.println("serverFactory");
        AbstractServerConnectionFactory connectionFactory = new TcpNetServerConnectionFactory(port);
        return connectionFactory;
    }

    @Bean
    MessageChannel toTcp() {
        System.out.println("creating toTcp DirectChannel");
        DirectChannel dc = new DirectChannel();
        dc.setBeanName("toTcp");

        return dc;
    }

    @Bean
    public MessageChannel fromTcp() {
        System.out.println("creating fromTcp DirectChannel");
        DirectChannel dc = new DirectChannel();
        dc.setBeanName("fromTcp");

        return dc;
    }

    // Inbound channel adapter. This receives the data from the client
    @Bean
    public TcpReceivingChannelAdapter inboundAdapter(AbstractServerConnectionFactory connectionFactory) {
        System.out.println("Creating inbound adapter");
        TcpReceivingChannelAdapter inbound = new TcpReceivingChannelAdapter();

        inbound.setConnectionFactory(connectionFactory);
        inbound.setOutputChannelName("fromTcp");

        return inbound;
    }

    // Outbound channel adapter. This sends the data to the client
    @Bean
    @ServiceActivator(inputChannel="toTcp")
    public TcpSendingMessageHandler outboundAdapter(AbstractServerConnectionFactory connectionFactory) {
        System.out.println("Creating outbound adapter");
        TcpSendingMessageHandler outbound = new TcpSendingMessageHandler();
        outbound.setConnectionFactory(connectionFactory);
        return outbound;
    }

    // Endpoint example
    @MessageEndpoint
    public static class Echo {

        // Server
        @Transformer(inputChannel="fromTcp", outputChannel="toEcho")
        public String convert(byte[] bytes) {
            System.out.println("convert: " + new String(bytes));
            return new String(bytes);
        }

        // Server
        @ServiceActivator(inputChannel="toEcho", outputChannel="toTcp")
        public String upCase(String in) {
            System.out.println("upCase: " + in.toUpperCase());
            return in.toUpperCase();
        }
    }

    @Override
    public void onApplicationEvent(TcpConnectionEvent event) {
        System.out.println("Got TcpConnectionEvent: source=" + event.getSource() +
                ", id=" + event.getConnectionId());
    }
}