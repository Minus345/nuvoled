package com.nuvoled.Tests;

import java.net.*;

public class TestReciver {
    public static void main(String[] args) {
        try {
            DatagramSocket serverSocket = new DatagramSocket(2000);
            byte[] receiveData = new byte[15];

            System.out.printf("Listening on udp: %s:%d%n", InetAddress.getLocalHost().getHostAddress(), 2000);
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

            while(true) {
                try {
                    serverSocket.receive(receivePacket);
                } catch (Exception ex) {
                    System.out.println(ex);
                }

                System.out.print("RECEIVED: ");

                for (byte receiveDatum : receiveData) {
                    System.out.print(receiveDatum + " ");
                }
                System.out.println("ende");
                System.out.println("Adress: " + receivePacket.getAddress());
            }

        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        }
    }
}