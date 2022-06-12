package com.nuvoled.sender;

import com.nuvoled.Main;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class SendColor {
    public static void send(byte red, byte green, byte blue) {
        try {
            //counter =  (128*128*3)/1440 == 34,133
            for (int counter = 0; counter <= 35 ; counter++) {
                byte[] message = new byte[1450];
                message[0] = 36;
                message[1] = 36;
                message[2] = 20;
                message[3] = Main.getCourantFrame();
                message[4] = 10; //RGB
                message[5] = 0;
                message[6] = (byte) counter; //counter
                message[7] = 0;
                message[8] = 35;
                message[9] = 45;

                for (int i = 1; i < 1440; i = i + 3) {
                    message[9 + i] = blue;
                    message[9 + 1 + i] = green;
                    message[9 + 2 + i] = red;
                }

                for (byte b : message) {
                    System.out.print((b & 0xff) + " ");
                }
                System.out.println("Counter " + counter);

                InetAddress address = InetAddress.getByName(Main.getAddr());
                DatagramSocket dsocket = new DatagramSocket();
                DatagramPacket packet = new DatagramPacket(message, message.length, address, Main.getPort());
                dsocket.send(packet);
                dsocket.close();
            }

        } catch (Exception e) {
            System.err.println(e);
        }
    }
}
