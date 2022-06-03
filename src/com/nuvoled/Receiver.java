package com.nuvoled;

import java.io.IOException;
import java.net.*;

public class Receiver {

    public static void main(String[] args) {
        int port = args.length == 0 ? 2000 : Integer.parseInt(args[0]);
        new Receiver().run(port);
    }

    public void run(int port) {
        try {
            DatagramSocket serverSocket = new DatagramSocket(port);
            byte[] receiveData = new byte[15];

            String sendString = "polo";
            byte[] sendData = sendString.getBytes("UTF-8");

            System.out.printf("Listening on udp:%s:%d%n", InetAddress.getLocalHost().getHostAddress(), port);
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

            while (true) {
                serverSocket.receive(receivePacket);
                String sentence = new String(receivePacket.getData(), 0, receivePacket.getLength());
                System.out.println("RECEIVED: " + sentence);

                for (int i = 0; i < receiveData.length; i++) {
                    System.out.print(receiveData[i] + " ");
                }
                System.out.println("ende");
                // now send acknowledgement packet back to sender
                 //DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, receivePacket.getAddress(), receivePacket.getPort());
                 //serverSocket.send(sendPacket);
            }
        } catch (IOException e) {
            System.out.println(e);
        }
        // should close serverSocket in finally block
    }
}