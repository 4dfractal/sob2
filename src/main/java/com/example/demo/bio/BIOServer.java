package com.example.demo.bio;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

@Slf4j
public class BIOServer {
    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(10002);
        while (true) {
            Socket client = server.accept(); //Wait for client connection, if no connection is obtained, wait at this step
            new Thread(new ServerThread(client)).start(); //Open a thread for each client connection
        }
        //server.close();
    }
}

@Slf4j
class ServerThread extends Thread {

    private Socket client;

    public ServerThread(Socket client) {
        this.client = client;
    }

    @SneakyThrows
    @Override
    public void run() {
        log.info("Client:" + client.getInetAddress().getLocalHost() + "Connected to server");
        BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
        //Read messages from clients
        String mess = br.readLine();
        log.info("Client:" + mess);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
        bw.write(mess + "\n");
        bw.flush();
    }
}