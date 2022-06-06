package com.nuvoled;

import java.io.IOException;
import java.net.*;

public class Receiver {

    private static byte[] mac;
    private static boolean runing;

    public void run(int port) {
        try {
            DatagramSocket serverSocket = new DatagramSocket(port);
            byte[] receiveData = new byte[15];
            mac = new byte[4];

            System.out.printf("Listening on udp: %s:%d%n", InetAddress.getLocalHost().getHostAddress(), port);
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            runing = true;

            while (runing) {
                System.out.println("Searching...");
                try {
                    serverSocket.receive(receivePacket);
                }catch (Exception ex){
                    System.out.println(ex);
                }
                //String sentence = new String(receivePacket.getData(), 0, receivePacket.getLength());
                //System.out.println("RECEIVED: " + sentence);
                System.out.print("RECEIVED: ");

                for (int i = 0; i < receiveData.length; i++) {
                    System.out.print(receiveData[i] + " ");
                }
                System.out.println("ende");

                if (receiveData[2] == 15) {
                    runing = false;
                    mac[0] = receiveData[3];
                    mac[1] = receiveData[4];
                    mac[2] = receiveData[5];
                    mac[3] = receiveData[6];
                    System.out.println("Pannel Gefunden | Mac: " + mac[0] + mac[1] + mac[2] + mac[3]);

                    byte[] message = new byte[16];
                    message[0] = 36;
                    message[1] = 36;
                    message[2] = (byte) 120;
                    message[3] = 2;
                    message[4] = 32;
                    message[5] = 8;
                    message[6] = 8;
                    message[7] = 1;
                    message[8] = mac[2];
                    message[9] = mac[3];
                    message[10] = 74;
                    message[11] = 8;
                    message[12] = 8;
                    message[13] = 0;
                    message[14] = 0;
                    message[15] = 0;

                    InetAddress address = InetAddress.getByName(Main.getAddr());

                    //DatagramSocket dsocket = new DatagramSocket();
                    DatagramPacket senderPacket = new DatagramPacket(message, message.length, address, port);
                    serverSocket.send(senderPacket);
                    serverSocket.close();

                }
            }
        } catch (IOException e) {
            System.out.println(e);
        }

    }
}