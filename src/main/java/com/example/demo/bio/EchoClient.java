package com.example.demo.bio;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.lang.*;

public class EchoClient {
    public static void main(String[] args) throws IOException {

        Socket echoSocket = null;
        PrintWriter out = null;
        BufferedReader in = null;

        try {
            echoSocket = new Socket("0.0.0.0", 10002);
            out = new PrintWriter(echoSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: taranis.");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for "+ "the connection to: taranis.");
            System.exit(1);
        }
        InputStream ina = echoSocket.getInputStream();
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        String userInput;int bytesRead;
        int STX=2, ETX=3, CR=13, LF=10;
        String crx = new Character((char)CR).toString();
        String lfx = new Character((char)LF).toString();
        String stx = new Character((char)STX).toString();
        String etx = new Character((char)ETX).toString();
        String cumposary = "1,3,0192744210,";
        byte[] buffer = new byte[1024];
        System.out.println("Enter string to communicate ->");
        //while ((userInput = stdIn.readLine()) != null) {
        userInput = stdIn.readLine();
        out.println(stx+cumposary+userInput+etx);
        System.out.println("Sending to server -> " + stx+cumposary+userInput+etx);
        bytesRead = ina.read(buffer);
        System.out.println(bytesRead);
        for (int timer=0; timer < 10000; timer++);{};
        for (int b=0; b < 1024; b++){
            String bufferprinter = new Character((char)buffer[b]).toString();
            //if(buffer[b] == 13){System.out.println("<lfx>");}
            // if(buffer[b] == 10){System.out.println("<crx>");}
            System.out.print(bufferprinter);
            //      }
        }

        out.close();
        in.close();
        stdIn.close();
        echoSocket.close();
    }
}