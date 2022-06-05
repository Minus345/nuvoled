package com.nuvoled;

import java.io.IOException;
import java.net.*;

public class Receiver extends Thread {


    public static void main(String[] args) {
        int port = args.length == 0 ? 2000 : Integer.parseInt(args[0]);
        //int port = 2000;
        new Receiver().run(port);
    }



    public static byte[] mac;

    public void run(int port) {
        try {

            DatagramSocket serverSocket = new DatagramSocket(port);
            byte[] receiveData = new byte[15];
            mac = new byte[2];

            System.out.printf("Listening on udp:%s:%d%n", InetAddress.getLocalHost().getHostAddress(), port);
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

            while (true) {
                serverSocket.receive(receivePacket);
                String sentence = new String(receivePacket.getData(), 0, receivePacket.getLength());
                System.out.println("RECEIVED: " + sentence);

                for (int i = 0; i < receiveData.length; i++) {
                    System.out.print(receiveData[i] + " ");
                }

                mac[0] = receiveData[3];
                mac[1] = receiveData[4];
                mac[2] = receiveData[5];
                System.out.println("ende");
            }
        } catch (IOException e) {
            System.out.println(e);
        }

    }
}