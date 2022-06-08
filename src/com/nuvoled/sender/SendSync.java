package com.nuvoled.sender;

import com.nuvoled.Main;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class SendSync {
    public static void send() {
        try {
            int port = 2000;
            byte[] FrameFinish = new byte[4];
            FrameFinish[0] = 36;
            FrameFinish[1] = 36;
            FrameFinish[2] = 100;
            FrameFinish[3] = Main.getCurantFrame(); //curframe

            for (int i = 0; i < FrameFinish.length; i++) {
                System.out.print((FrameFinish[i] & 0xff) + " ");
            }

            InetAddress address = InetAddress.getByName(Main.getAddr());

            DatagramSocket dsocket = new DatagramSocket();
            DatagramPacket packet = new DatagramPacket(FrameFinish, FrameFinish.length, address, port);
            dsocket.send(packet);
            dsocket.close();
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}