package com.example.demo.nio;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

@Slf4j
public class NIOServer {
    private InetAddress addr;
    private int port;
    private Selector selector;

    private static int BUFF_SIZE = 1024;

    public NIOServer(InetAddress addr, int port) throws IOException {
        this.addr = addr;
        this.port = port;
        startServer();
    }

    private void startServer() throws IOException {
        // Get selector and channel (socketChannel)
        this.selector = Selector.open();
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);

        // Binding Address and Port
        InetSocketAddress listenAddr = new InetSocketAddress(this.addr, this.port);
        serverChannel.socket().bind(listenAddr);
        serverChannel.register(this.selector, SelectionKey.OP_ACCEPT);

        log.info("NIOServer Running...Press Ctrl-C Out of Service");

        while (true) {
            log.info("The server waits for new connections and selector Choose...");
            this.selector.select();

            // Select key job
            Iterator keys = this.selector.selectedKeys().iterator();
            while (keys.hasNext()) {
                SelectionKey key = (SelectionKey) keys.next();

                // Prevent duplicate key s and remove them when finished
                keys.remove();

                //Invalid direct skip
                if (!key.isValid()) {
                    continue;
                }
                if (key.isAcceptable()) {
                    this.accept(key);
                } else if (key.isReadable()) {
                    this.read(key);
                } else if (key.isWritable()) {
                    this.write(key);
                } else if (key.isConnectable()) {
                    this.connect(key);
                }
            }
        }
    }

    private void connect(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        if (channel.finishConnect()) {
            // Success
            log.info("Successfully connected");
        } else {
            // fail
            log.info("Failed Connection");
        }
    }

    private void accept(SelectionKey key) throws IOException {
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        SocketChannel channel = serverChannel.accept();
        channel.configureBlocking(false);
        channel.register(this.selector, SelectionKey.OP_READ);

        Socket socket = channel.socket();
        SocketAddress remoteAddr = socket.getRemoteSocketAddress();
        log.info("connection to: " + remoteAddr);
    }

    private void read(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();

        ByteBuffer buffer = ByteBuffer.allocate(BUFF_SIZE);
        int numRead = channel.read(buffer);
        if (numRead == -1) {
            log.info("Close Client Connection: " + channel.socket().getRemoteSocketAddress());
            channel.close();
            return;
        }
        String msg = new String(buffer.array()).trim();
        log.info("Got: " + msg);

        // Reply Client
        String reMsg = msg + " Hello, this is BIOServer Reply message to you:" + System.currentTimeMillis();
        channel.write(ByteBuffer.wrap(reMsg.getBytes()));
    }

    private void write(SelectionKey key) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(BUFF_SIZE);
        byteBuffer.flip();
        SocketChannel clientChannel = (SocketChannel) key.channel();
        while (byteBuffer.hasRemaining()) {
            clientChannel.write(byteBuffer);
        }
        byteBuffer.compact();
    }

    public static void main(String[] args) throws IOException {
        new NIOServer(null, 10002);
    }
}