package com.nuvoled.sender;

import com.nuvoled.Main;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Reset {
    public static void send(int port) {
        try {
            byte[] message = new byte[6];
            message[0] = 36;
            message[1] = 36;
            message[2] = (byte) 160;
            message[3] = 0;
            message[4] = 0;
            message[5] = 0;

            byte[] message2 = new byte[4];
            message2[0] = 36;
            message2[1] = 36;
            message2[2] = (byte) 130;
            message2[3] = 0;

            InetAddress address = InetAddress.getByName(Main.getAddr());
            DatagramSocket dsocket = new DatagramSocket();
            DatagramPacket packet = new DatagramPacket(message, message.length, address, port);
            dsocket.send(packet);
            Thread.sleep(1000);
            DatagramPacket packet2 = new DatagramPacket(message2, message2.length, address, port);
            dsocket.send(packet2);
            dsocket.close();

        } catch (Exception e) {
            System.err.println(e);
        }

    }
}
