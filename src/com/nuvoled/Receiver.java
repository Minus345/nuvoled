package com.nuvoled;

import java.io.IOException;
import java.net.*;

public class Receiver {

    public void run(int port) {
        try {
            DatagramSocket serverSocket = new DatagramSocket(port);
            byte[] receiveData = new byte[15];
            byte[] mac = new byte[4];

            System.out.printf("Listening on udp: %s:%d%n", InetAddress.getLocalHost().getHostAddress(), port);
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            boolean runing = true;

            while (runing) {
                System.out.println("Searching...");
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

                if (receiveData[2] == 15) {
                    runing = false;
                    mac[0] = receiveData[3];
                    mac[1] = receiveData[4];
                    mac[2] = receiveData[5];
                    mac[3] = receiveData[6];
                    System.out.println("Pannel Gefunden | Mac: " + mac[0] + mac[1] + mac[2] + mac[3]);

                    byte[] aktiviert = new byte[6];
                    aktiviert[0] = 36;
                    aktiviert[1] = 36;
                    aktiviert[2] = (byte) 160;
                    aktiviert[3] = mac[2]; //mac[3] 23
                    aktiviert[4] = mac[3]; //mac[4] 49
                    aktiviert[5] = mac[1]; //mac[2] 74

                    byte[] configMessage = new byte[16];
                    configMessage[0] = 36;
                    configMessage[1] = 36;
                    configMessage[2] = (byte) 120;
                    configMessage[3] = 2;
                    configMessage[4] = 32;
                    configMessage[5] = 8;
                    configMessage[6] = 8;
                    configMessage[7] = 1;
                    configMessage[8] = mac[2]; //mac[3]
                    configMessage[9] = mac[3]; //mac[4]
                    configMessage[10] = mac[1]; //mac[2]
                    configMessage[11] = 8;
                    configMessage[12] = 8;
                    configMessage[13] = 0;
                    configMessage[14] = 0;
                    configMessage[15] = 0;

                    InetAddress address = InetAddress.getByName(Main.getAddr());

                    //DatagramSocket dsocket = new DatagramSocket();
                    DatagramPacket aktiviertPacket = new DatagramPacket(aktiviert, aktiviert.length, address, port);
                    serverSocket.send(aktiviertPacket);
                    System.out.print("SENDING: ");
                    for (byte b : aktiviert) {
                        System.out.print(Byte.toUnsignedInt(b) + " ");
                    }
                    System.out.println(" ende");
                    Thread.sleep(1000);

                    DatagramPacket configPacket = new DatagramPacket(configMessage, configMessage.length, address, port);
                    serverSocket.send(configPacket);
                    serverSocket.close();
                    System.out.print("SENDING: ");
                    for (byte b : configMessage) {
                        System.out.print(Byte.toUnsignedInt(b) + " ");
                    }
                    System.out.println(" ende");
                    Thread.sleep(1000);

                }
            }
        } catch (IOException | InterruptedException e) {
            System.out.println(e);
        }

    }
}