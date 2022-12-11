package com.nuvoled.Tests;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class TestSend {
    public static void main(String args[]) {
        try {
            String host = args[0];
            int port = 2000;
            System.out.println("Sending on: " + host + " : " + port);

            //byte[] message = "Java Source and Support".getBytes();
            byte[] message = new byte[16];
            message[0] = 36;
            message[1] = 36;
            message[2] = 15;
            message[3] = 0;
            message[4] = 74;
            message[5] = 23;
            message[6] = 49;
            message[7] = 80;
            message[8] = 52;
            message[9] = 83;
            message[10] = 32;
            message[11] = 8;
            message[12] = 8;
            message[13] = 8;
            message[14] = 8;
            message[15] = 0;
            // Get the internet address of the specified host
            InetAddress address = InetAddress.getByName(host);

            DatagramSocket dsocket = new DatagramSocket();
            while (true) {
                DatagramPacket packet = new DatagramPacket(message, message.length, address, port);
                dsocket.send(packet);
            }
            //dsocket.close();
        } catch (Exception e) {
            System.err.println(e);
        }

    }
}
