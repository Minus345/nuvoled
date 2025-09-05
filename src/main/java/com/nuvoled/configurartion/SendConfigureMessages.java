package com.nuvoled.configurartion;

import com.nuvoled.Main;
import com.nuvoled.sender.ManageNetworkConnection;

import java.awt.*;
import java.net.DatagramPacket;
import java.util.HexFormat;

public class SendConfigureMessages {

    public static void reset() {
        send160();
        sendTrash();
        sendRequestForData130();
        sendRequestForData130();
        sendDummyConfig120();
        send160();
        sendTrash();
        sendRequestForData130();
        sendRequestForData130();
    }

    /**
     * In Software angeklickt
     */
    public static void sendRedCross(byte[] mac) {
        byte[] message = new byte[6];
        message[0] = 36;
        message[1] = 36;
        message[2] = (byte) 160;
        message[3] = mac[2]; //mac[2] //126
        message[4] = mac[3];  //mac[3] //12
        message[5] = mac[1];  //mac[1] //93
        //                  mac[0]
        ManageNetworkConnection.send_data(message);
        sendTrash();
    }

    private static void send160() {
        byte[] message = new byte[6];
        message[0] = 36;
        message[1] = 36;
        message[2] = (byte) 160;
        message[3] = 0;
        message[4] = 0;
        message[5] = 0;

        ManageNetworkConnection.send_data(message);
    }

    private static void sendTrash() {
        byte[] message = new byte[1450];
        String data = "24 24 7f ff 00 00 00 00 00 00 00 00 00 00 00 00 " +
                "00 01 01 01 01 01 01 02 02 02 02 02 03 03 03 03 " +
                "04 04 04 05 05 05 06 06 06 07 07 07 08 08 09 09 " +
                "09 0a 0a 0b 0b 0c 0c 0c 0d 0d 0e 0e 0f 0f 10 10 " +
                "11 12 12 13 13 14 14 15 16 16 17 17 18 19 19 1a " +
                "1b 1b 1c 1d 1d 1e 1f 1f 20 21 22 22 23 24 25 25 " +
                "26 27 28 28 29 2a 2b 2c 2d 2d 2e 2f 30 31 32 33 " +
                "33 34 35 36 37 38 39 3a 3b 3c 3d 3e 3f 40 41 42 " +
                "43 44 45 46 47 48 49 4a 4b 4c 4d 4e 4f 50 51 52 " +
                "54 55 56 57 58 59 5a 5b 5d 5e 5f 60 61 63 64 65 " +
                "66 67 69 6a 6b 6c 6e 6f 70 71 73 74 75 77 78 79 " +
                "7a 7c 7d 7e 80 81 83 84 85 87 88 89 8b 8c 8e 8f " +
                "91 92 93 95 96 98 99 9b 9c 9e 9f a1 a2 a4 a5 a7 " +
                "a8 aa ab ad ae b0 b1 b3 b5 b6 b8 b9 bb bd be c0 " +
                "c2 c3 c5 c6 c8 ca cb cd cf d0 d2 d4 d6 d7 d9 db " +
                "dc de e0 e2 e3 e5 e7 e9 eb ec ee f0 f2 f4 f5 f7 " +
                "f9 fb fd ff";
        int x = 0;
        for (int i = 0; i < 260; i++) {
            String substring = data.substring(x, x + 2);
            byte[] bytes = HexFormat.of().parseHex(substring);
            message[i] = bytes[0];
            x = x + 3;
        }
        ManageNetworkConnection.send_data(message);
    }

    private static void sendRequestForData130() {
        byte[] message = new byte[4];
        message[0] = 36;
        message[1] = 36;
        message[2] = (byte) 130;
        message[3] = 0;
        // message[4] = 0;
        ManageNetworkConnection.send_data(message);
    }

    private static void sendDummyConfig120() {
        byte[] message = new byte[8];
        message[0] = 36;
        message[1] = 36;
        message[2] = (byte) 120;
        message[3] = 2;
        message[4] = 0;
        message[5] = 8;
        message[6] = 8;
        message[7] = 0;
        ManageNetworkConnection.send_data(message);
    }

    public static void makeConfigAndSendGreenCross(byte[] mac, int offsetX, int offsetY) {
        setConfig(mac, offsetX, offsetY);
        send160();
        sendTrash();
        setConfig(mac, offsetX, offsetY);
    }

    private static void setConfig(byte[] mac, int offsetX, int offsetY) {
        byte[] message = new byte[15];
        message[0] = 36;
        message[1] = 36;
        message[2] = (byte) 120; //send config
        message[3] = (byte) 2; //fix
        message[4] = 0; //fix
        message[5] = (byte) (Main.getGlobalPixelInX() / 16); //total screen width /16
        message[6] = (byte) (Main.getGlobalPixelInY() / 16); //total screen height /16
        message[7] = 1;//(byte) (Main.getxPanelCount() * Main.getyPanelCount()); //total numbers of modules connected
        message[8] = mac[2]; //mac[2]
        message[9] = mac[3]; //mac[3]
        message[10] = mac[1]; //mac[1]
        message[11] = (byte) (Main.getOnePanelSizeX() / 16); //modul width /16
        message[12] = (byte) (Main.getOnePanelSizeY() / 16); //modul height /16
        message[13] = (byte) (offsetX / 16); //offset x /16
        message[14] = (byte) (offsetY / 16); //offset y /16
        ManageNetworkConnection.send_data(message);
    }

    /**
     * searches for panels in the connected network
     *
     * @param time im ms
     */
    public static void getPanelConnected(int time) {
        byte[] receiveData = new byte[15];

        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        long timeStart = System.currentTimeMillis();

        while (System.currentTimeMillis() <= timeStart + time) {
            System.out.println("Searching...");
            try {
                ManageNetworkConnection.getDatagramSocket().receive(receivePacket);
            } catch (Exception e) {
                e.printStackTrace();
            }
/*
            System.out.print("RECEIVED: ");

            for (byte receiveDatum : receiveData) {
                System.out.print(receiveDatum + " ");
            }
            System.out.println("ende");
            System.out.println("Adress: " + receivePacket.getAddress().getHostAddress());
 */
            if (receiveData[0] == 36 && receiveData[1] == 36 && receiveData[2] == 15) {
                byte[] mac = new byte[4];
                mac[0] = receiveData[3];
                mac[1] = receiveData[4];
                mac[2] = receiveData[5];
                mac[3] = receiveData[6];

                Panel panel = new P5(mac);
                if (!ConfigManager.lookIfAlreadyInList(panel)) {
                    ConfigManager.waitingListAdd(panel);
                    System.out.println("Panel Gefunden | Mac: " + mac[0] + mac[1] + mac[2] + mac[3] + " Version: " + (char) receiveData[7] + (char) receiveData[8] + (char) receiveData[9] + (char) receiveData[10]);
                }
            }
        }
    }
}