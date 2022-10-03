package com.nuvoled.sender;

import com.nuvoled.Main;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class SendSync {

    private static long date =  System.currentTimeMillis();;

    public static void sendSyncro(byte Frame,  DatagramSocket datagramSocket) {
        try {
            int port = 2000;
            byte[] FrameFinish = new byte[4];
            FrameFinish[0] = 36;
            FrameFinish[1] = 36;
            FrameFinish[2] = 100;
            FrameFinish[3] = Frame; //curframe

            //for (byte frameFinish : FrameFinish) {
            //   System.out.print((frameFinish & 0xff) + " ");
            //}

            InetAddress address = InetAddress.getByName(Main.getBroadcastIpAddress());

            //DatagramSocket dsocket = new DatagramSocket();
            datagramSocket.setSendBufferSize(1048576);
            DatagramPacket packet = new DatagramPacket(FrameFinish, FrameFinish.length, address, port);
            datagramSocket.send(packet);
            //datagramSocket.close();
            Main.setCourantFrame((byte) (Main.getCourantFrame() + 1));
        } catch (Exception e) {
            System.err.println(e);
        }
        if (Main.getCourantFrame() == (byte) 255) {
            Main.setCourantFrame((byte) 0);
            float difference = (System.currentTimeMillis() - date);
            difference = difference / 1000;
            float fps = (255 / difference);
            System.out.println("dif: " + difference + " fps: " + fps);
            date = System.currentTimeMillis();
            System.exit(0);
        }
    }

    public static void sendFrameFinish(byte Frame, byte numberPacketH, byte numberPacketL) {
        try {
            int port = 2000;
            byte[] FrameFinish = new byte[4];
            FrameFinish[0] = 36;
            FrameFinish[1] = 36;
            FrameFinish[2] = 30;
            FrameFinish[3] = Frame;
            FrameFinish[4] = numberPacketH;
            FrameFinish[5] = numberPacketL;

            InetAddress address = InetAddress.getByName(Main.getBroadcastIpAddress());

            DatagramSocket dsocket = new DatagramSocket();
            dsocket.setSendBufferSize(1048576);
            DatagramPacket packet = new DatagramPacket(FrameFinish, FrameFinish.length, address, port);
            dsocket.send(packet);
            dsocket.close();

            Main.setCourantFrame((byte) (Main.getCourantFrame() + 1));
        } catch (Exception e) {
            System.err.println(e);
        }
        if (Main.getCourantFrame() == (byte) 255) {
            Main.setCourantFrame((byte) 0);
            float difference = (System.currentTimeMillis() - date);
            difference = difference / 1000;
            float fps = (255 / difference);
            System.out.println("dif: " + difference + " fps: " + fps);
            date = System.currentTimeMillis();
        }
    }
}