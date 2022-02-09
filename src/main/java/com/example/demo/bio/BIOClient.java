package com.example.demo.bio;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.Socket;

@Slf4j
public class BIOClient {

    public static void main(String[] args) throws IOException {
        Socket s = new Socket("0.0.0.0", 10002);

        InputStream input = s.getInputStream();
        OutputStream output = s.getOutputStream();

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(output));
        bw.write("Client sends message test to server\n");  //Send a message to the server side
        bw.flush();

        BufferedReader br = new BufferedReader(new InputStreamReader(input));  //Read messages returned by the server
        String mess = br.readLine();
        log.info("The server:" + mess);
    }
}