package com.nuvoled.old;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Sender {
    public static void main(String args[]){
        try {
            String host = "192.168.178.255";
            int port = 2000;

            //byte[] message = "Java Source and Support".getBytes();
            byte[] message = new byte[16];
            message[0] = 36;
            message[1] = 36;
            message[2] = (byte) 160;
            message[3] = 23;
            message[4] = 49;
            message[5] = 74;
            message[6] = 0;
            message[7] = 0;
            message[8] = 0;
            message[9] = 0;
            message[10] = 0;
            message[11] = 0;
            message[12] = 0;
            message[13] = 0;
            message[14] = 0;
            message[15] = 0;

            byte[] message2 = new byte[15];
            message2[0] = 36;
            message2[1] = 36;
            message2[2] = (byte) 120;
            message2[3] = 2;
            message2[4] = 32;
            message2[5] = 8;
            message2[6] = 8;
            message2[7] = 1;
            message2[8] = 23;
            message2[9] = 49;
            message2[10] = 74;
            message2[11] = 8;
            message2[12] = 8;
            message2[13] = 0;
            message2[14] = 0;

            byte[] message3 = new byte[16];
            message3[0] = 36;
            message3[1] = 36;
            message3[2] = (byte) 155;
            message3[3] = 0;

            // Get the internet address of the specified host
            InetAddress address = InetAddress.getByName(host);

            DatagramSocket dsocket = new DatagramSocket();
            for (int i = 0; i <= 5; i++){
                DatagramPacket packet = new DatagramPacket(message2, message2.length, address, port);
                dsocket.send(packet);
            }
            dsocket.close();
        } catch (Exception e) {
            System.err.println(e);
        }

    }
}
