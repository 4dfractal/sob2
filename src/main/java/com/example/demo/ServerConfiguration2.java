package com.example.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.ip.tcp.TcpReceivingChannelAdapter;
import org.springframework.integration.ip.tcp.TcpSendingMessageHandler;
import org.springframework.integration.ip.tcp.connection.TcpNioClientConnectionFactory;
import org.springframework.messaging.PollableChannel;

import java.util.concurrent.TimeUnit;

@Configuration()
@EnableIntegration
@IntegrationComponentScan
public class ServerConfiguration2 {

    public static final String REQUEST_CHANNEL = "rec_chanel";
    public static final String RESPONSE_CHANNEL = "res_chanel";
    public static final long timeout = 50000l;

    @Bean(name = REQUEST_CHANNEL)
    public DirectChannel sender() {
        return new DirectChannel();
    }

    @Bean(name = RESPONSE_CHANNEL)
    public PollableChannel receiver() {
        return new QueueChannel();
    }

    @Bean
    @ServiceActivator(inputChannel = REQUEST_CHANNEL)
    public TcpSendingMessageHandler outboundClient(TcpNioClientConnectionFactory connectionFactory) {
        TcpSendingMessageHandler outbound = new TcpSendingMessageHandler();
        outbound.setConnectionFactory(connectionFactory);
        outbound.setRetryInterval(TimeUnit.SECONDS.toMillis(timeout));
        outbound.setClientMode(true);
        return outbound;
    }

    @Bean
    public TcpReceivingChannelAdapter inboundClient(TcpNioClientConnectionFactory connectionFactory) {
        TcpReceivingChannelAdapter inbound = new TcpReceivingChannelAdapter();
        inbound.setConnectionFactory(connectionFactory);
        inbound.setRetryInterval(TimeUnit.SECONDS.toMillis(timeout));
        inbound.setOutputChannel(receiver());
        inbound.setClientMode(true);
        return inbound;
    }
}