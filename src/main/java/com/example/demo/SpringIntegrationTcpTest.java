package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.config.EnableMessageHistory;
import org.springframework.integration.ip.tcp.connection.AbstractServerConnectionFactory;
import org.springframework.integration.ip.util.TestingUtilities;

import java.io.IOException;
import java.util.Scanner;

/**
 * https://stackoverflow.com/questions/41568806/spring-integration-tcp-inbound-outbound-adapter-with-non-spring-client
 */

@SpringBootApplication
@IntegrationComponentScan
@EnableMessageHistory
public class SpringIntegrationTcpTest {

    @Autowired
    private ServerConfiguration.Gateway gateway;

    public String send(String data) {
        return gateway.send(data);
    }


    public static void main(String[] args) throws IOException {

        ConfigurableApplicationContext context = SpringApplication.run(SpringIntegrationTcpTest.class, args);

        SpringIntegrationTcpTest si = context.getBean(SpringIntegrationTcpTest.class);

        final AbstractServerConnectionFactory crLfServer = context.getBean(AbstractServerConnectionFactory.class);

        final Scanner scanner = new Scanner(System.in);
        System.out.print("Waiting for server to accept connections on port " + crLfServer.getPort());
        TestingUtilities.waitListening(crLfServer, 100000L);
        System.out.println("running.\n\n");

        System.out.println("Please enter some text and press <enter>: ");
        System.out.println("\tNote:");
        System.out.println("\t- Entering FAIL will create an exception");
        System.out.println("\t- Entering q will quit the application");
        System.out.print("\n");

        while (true) {

            final String input = scanner.nextLine();

            if("q".equals(input.trim())) {
                break;
            }
            else {
                final String result = si.send(input);
                System.out.println(result);
            }
        }

        scanner.close();
        context.close();
    }
}