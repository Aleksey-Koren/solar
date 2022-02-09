package io.solar;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;

public class Socket {

    private static final String HOST = "localhost";
    private static final Integer PROXY_PORT = 8081;
    private static final Integer LOCAL_PORT = 2000;

    private static java.net.Socket server;


    public static void main(String[] args) {
        try {
            System.out.println("Starting proxy for " + HOST
                    + ":" + PROXY_PORT
                    + " on port " + LOCAL_PORT);

            server = new java.net.Socket(HOST, PROXY_PORT);


            runServer();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void runServer() throws IOException {
        ServerSocket ss = new ServerSocket(LOCAL_PORT);


        while (true) {
            new MyThread(ss.accept()).start();
        }
    }
}

class MyThread extends Thread {

    private final java.net.Socket client;
    private  java.net.Socket server;
    private static DataInputStream serverInputStream;
    private static DataOutputStream serverOutputStream;

    MyThread(java.net.Socket client) {
        this.client = client;
        try {
            this.server = new java.net.Socket("localhost", 8081);
            server.setSoTimeout(0);
            client.setSoTimeout(0);
            serverInputStream = new DataInputStream(server.getInputStream());
            serverOutputStream = new DataOutputStream(server.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {

        DataInputStream streamFromClient = null;
        DataOutputStream streamToClient = null;

        try {
            streamFromClient = new DataInputStream(client.getInputStream());
            streamToClient = new DataOutputStream(client.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }


        int bytesRead;
        try {

            byte[] bytes = new byte[2000];
            int num = 0;
            int count = 0;
            while (true) {
                num = streamFromClient.read(bytes);
                if (num == -1) {
                    break;
                }
                System.out.println(new String(bytes));
                serverOutputStream.write(bytes,0, num);
                count++;
            }
            if (count > 0) {
                serverOutputStream.flush();
            }



//            System.out.println(stringFromClient);
//            serverOutputStream.writeUTF(stringFromClient);
//            serverOutputStream.flush();
//            while ((bytesRead = streamFromClient.read(request)) > 0) {
//
//                serverOutputStream.write(request, 0, bytesRead);
//                System.out.println("REQUEST: " + new String(request));
//            }
//            serverOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }



        try {
            byte[] bytes = new byte[2000];
            int num = 0;
            while(true) {
                num = serverInputStream.read(bytes);
                if (num == -1) {
                    break;
                }
                System.out.println(new String(bytes));
                streamToClient.write(bytes);
            }
            streamToClient.flush();
//            while ((bytesReadResponse = serverInputStream.read(reply)) > 0) {
//                streamToClient.write(reply, 0, bytesReadResponse);
//                System.err.println("RESPONSE: " + new String(reply));
//            }
//            streamToClient.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                streamToClient.close();
                streamFromClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }
}
