package com.nuvoled;

import java.io.IOException;
import java.net.*;

public class Receiver {

    private static byte[] mac1 = new byte[4];

    public static void run() {
        try {
            DatagramSocket serverSocket = new DatagramSocket(Main.getPort());
            byte[] receiveData = new byte[15];

            System.out.printf("Listening on udp: %s:%d%n", InetAddress.getLocalHost().getHostAddress(), Main.getPort());
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
                    mac1[0] = receiveData[3];
                    mac1[1] = receiveData[4];
                    mac1[2] = receiveData[5];
                    mac1[3] = receiveData[6];
                    System.out.println("Pannel 1 Gefunden | Mac: " + mac1[0] + mac1[1] + mac1[2] + mac1[3]);
                    sendConfig();
                }
            }
        } catch (IOException | InterruptedException e) {
            System.out.println(e);
        }

    }

    private static void sendConfig() throws IOException, InterruptedException {
        DatagramSocket serverSocket = new DatagramSocket(Main.getPort());
        byte[] aktiviert = new byte[6];
        aktiviert[0] = 36;
        aktiviert[1] = 36;
        aktiviert[2] = (byte) 160;
        aktiviert[3] = mac1[2]; //mac1[3]
        aktiviert[4] = mac1[3]; //mac1[4]
        aktiviert[5] = mac1[1]; //mac1[2]

        byte[] configMessage = new byte[15];
        configMessage[0] = 36;
        configMessage[1] = 36;
        configMessage[2] = (byte) 120;
        configMessage[3] = 2;
        configMessage[4] = 32;
        configMessage[5] = 8; //8
        configMessage[6] = 8;
        configMessage[7] = 1; //1
        configMessage[8] = mac1[2]; //mac1[3]
        configMessage[9] = mac1[3]; //mac1[4]
        configMessage[10] = mac1[1]; //mac1[2]
        configMessage[11] = 8;
        configMessage[12] = 8;
        configMessage[13] = 0;
        configMessage[14] = 0; // Offset

        InetAddress address = InetAddress.getByName(Main.getBroadcastIpAddress());

        //DatagramSocket dsocket = new DatagramSocket();
        DatagramPacket aktiviertPacket = new DatagramPacket(aktiviert, aktiviert.length, address, Main.getPort());
        serverSocket.send(aktiviertPacket);
        System.out.print("SENDING: ");
        for (byte b : aktiviert) {
            System.out.print(Byte.toUnsignedInt(b) + " ");
        }
        System.out.println(" ende");
        Thread.sleep(1000);

        DatagramPacket configPacket = new DatagramPacket(configMessage, configMessage.length, address, Main.getPort());
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