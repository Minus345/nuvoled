package com.nuvoled.sender;

import com.nuvoled.Main;

import java.io.IOException;
import java.net.*;

public class SendSync {

    private static long date = System.currentTimeMillis();

    private static DatagramSocket datagramSocket;
    private static InetAddress address ;

    public static boolean setDatagramSocket() {

        InetAddress address = null;

        try {
            datagramSocket = new DatagramSocket();
            address = InetAddress.getByName(Main.getBroadcastIpAddress());
            System.out.println("Connect Datagram " + Main.getPort() + " Adr " + Main.getBroadcastIpAddress());
            datagramSocket.connect(address, Main.getPort());
            datagramSocket.setSendBufferSize(2048576);
            if (datagramSocket.isConnected()) {
                System.out.println("Connected");
                return true;
            }
            return false;
        } catch (UnknownHostException | SocketException e) {
            //throw new RuntimeException(e);
            System.out.println(e);
            return false;
        }
    }

    public static void send_data(byte[] message) {

        if (datagramSocket.isClosed() || !datagramSocket.isBound()) {
            System.out.println("Reconnect");
            datagramSocket.close();
            try {
                address = InetAddress.getByName(Main.getBroadcastIpAddress());
                datagramSocket.connect(address, Main.getPort());
                datagramSocket.setSendBufferSize(2048576);
            } catch (UnknownHostException | SocketException e)  {
                throw new RuntimeException(e);
            }
        }

        try {
            DatagramPacket packet = new DatagramPacket(message, message.length, address, Main.getPort());
            datagramSocket.send(packet);
            //SendSync.sendFrameFinish(Main.getCourantFrame(), (byte) (MaxPackets >> 8), (byte) (MaxPackets & 255));
        } catch (IOException e) {
            //throw new RuntimeException(e);
            System.out.println(e);
        }
    }

    public static void send_end_frame() {
        SendSync.sendSyncro((byte) (Main.getCourantFrame() - 1));
        System.out.println("Sending Frame: " + Main.getCourantFrame());
    }
    public static void sendSyncro(byte Frame) {
        try {
            int port = 2000;
            byte[] FrameFinish = new byte[4];
            FrameFinish[0] = 36;
            FrameFinish[1] = 36;
            FrameFinish[2] = 100;
            FrameFinish[3] = Frame; //curframe

            DatagramPacket packet = new DatagramPacket(FrameFinish, FrameFinish.length, address, port);
            datagramSocket.send(packet);

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