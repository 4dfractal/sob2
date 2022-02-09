package com.example.demo.nio;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

@Slf4j
public class NIOClient {
    private static int BUFF_SIZE = 1024;

    public static void main(String[] args) throws IOException, InterruptedException {

        InetSocketAddress socketAddress = new InetSocketAddress("0.0.0.0", 10002);
        SocketChannel socketChannel = SocketChannel.open(socketAddress);

        log.info("Connect BIOServer Service, Port: 10002...");

        ArrayList<String> companyDetails = new ArrayList<>();

        // Create message list
        companyDetails.add("tencent");
        companyDetails.add("Alibaba");
        companyDetails.add("JD.COM");
        companyDetails.add("Baidu");
        companyDetails.add("google");

        for (String companyName : companyDetails) {
            socketChannel.write(ByteBuffer.wrap(companyName.getBytes()));
            log.info("Send out: " + companyName);

            ByteBuffer buffer = ByteBuffer.allocate(BUFF_SIZE);
            buffer.clear();
            socketChannel.read(buffer);
            String result = new String(buffer.array()).trim();
            log.info("Received NIOServer Message to reply:" + result);

            // Wait 2 seconds before sending the next message
            Thread.sleep(2000);
        }

        socketChannel.close();
    }
}