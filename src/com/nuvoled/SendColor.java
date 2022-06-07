package com.nuvoled;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public abstract class SendColor {
    public static void send(byte red, byte green, byte blue) {
        try {
            int port = 2000;
            byte[] message = new byte[1450];
            message[0] = 36;
            message[1] = 36;
            message[2] = 20;
            message[3] = 2;
            message[4] = 10; //RGB
            message[5] = 0;
            message[6] = 0; //counter
            message[7] = 0;
            message[8] = 35;
            message[9] = 45;

            for(int i = 1; i < 1440; i = i+3){
             message[9+i] = red;
             message[9+1+i] = green;
             message[9+2+i] = blue;
            }

            for (int i = 0; i < message.length; i++){
                System.out.print((message[i] & 0xff) + " ");
            }

            InetAddress address = InetAddress.getByName(Main.getAddr());

            DatagramSocket dsocket = new DatagramSocket();
            DatagramPacket packet = new DatagramPacket(message, message.length, address, port);
            dsocket.send(packet);
            dsocket.close();
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}
