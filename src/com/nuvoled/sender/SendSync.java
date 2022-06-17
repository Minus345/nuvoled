package com.nuvoled.sender;

import com.nuvoled.Main;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.Date;

public class SendSync {

    private static long date =  System.currentTimeMillis();;

    public static void send(byte Frame) {
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

            InetAddress address = InetAddress.getByName(Main.getAddr());

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

    public static void sendSyncMessage(){
        send(Main.getCourantFrame());
        Main.setCourantFrame((byte) (Main.getCourantFrame() + 1));
        System.out.println("Frame " + Byte.toUnsignedInt(Main.getCourantFrame()) + " Finish");
    }
}