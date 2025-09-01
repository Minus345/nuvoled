package com.nuvoled.sender;

import com.nuvoled.Main;

import java.io.IOException;
import java.net.*;
import java.util.Enumeration;

public class ManageNetworkConnection {
    private static DatagramSocket datagramSocket;

    /**
     * finds the network interface with address: "169.254.255.255"
     * @return
     */
    private static InetAddress findNetworkInterface() {
        try {
            System.out.println("Full list of Network Interfaces:");
            for (Enumeration<NetworkInterface> networkInterfaceEnumeration = NetworkInterface.getNetworkInterfaces(); networkInterfaceEnumeration.hasMoreElements(); ) {
                NetworkInterface networkInterface = networkInterfaceEnumeration.nextElement();
                System.out.println("    " + networkInterface.getName() + " " + networkInterface.getDisplayName());

                for (Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses(); inetAddresses.hasMoreElements(); ) {//inetAddresses -> Ip4/Ip6
                    InetAddress currInetAddress = inetAddresses.nextElement();
                    String currInetAddressString = currInetAddress.toString();
                    System.out.println("        " + currInetAddressString);
                    if (currInetAddressString.startsWith("/169.254")) {
                        System.out.println("==>> Binding to this adapter..." + currInetAddressString + "\n");
                        return currInetAddress;
                    }
                }
            }
        } catch (SocketException e) {
            System.out.println("Error retrieving network interface list");
        }
        System.out.println("No suitable Network card found");
        System.exit(-1);
        return null;
    }

    /**
     * creates DatagrammSocket for broadcasting out messages
     */
    public static void setDatagramSocket() {
        try {
            InetAddress address = findNetworkInterface();
            datagramSocket = new DatagramSocket(Main.getPort(), address);
            datagramSocket.setBroadcast(true);
        } catch (SocketException e) {
            System.out.println("Could not bind to Network Card");
            e.printStackTrace();
        }
    }

    public static void send_data(byte[] message) {
        try {
            DatagramPacket packet = new DatagramPacket(message, message.length, InetAddress.getByName(Main.getBroadcastIpAddress()), Main.getPort());
            datagramSocket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * sends the Syncro message after all pixels of the frame were send out
     */
    public static void sendSyncro() {
        try {
            int port = 2000;
            byte[] FrameFinish = new byte[4];
            FrameFinish[0] = 36;
            FrameFinish[1] = 36;
            FrameFinish[2] = 100;
            FrameFinish[3] = (byte) (Main.getCourantFrame() - 1); //currant frame

            DatagramPacket packet = new DatagramPacket(FrameFinish, FrameFinish.length, InetAddress.getByName(Main.getBroadcastIpAddress()), port);
            datagramSocket.send(packet);

            Main.setCourantFrame((byte) (Main.getCourantFrame() + 1));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (Main.getCourantFrame() == (byte) 255) {
            //resets frame counter
            Main.setCourantFrame((byte) 0);
        }
    }

    /**
     * deprecated
     *
     * @param Frame
     * @param numberPacketH
     * @param numberPacketL
     */
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

            DatagramSocket dsocket = new DatagramSocket();
            dsocket.setSendBufferSize(1048576);
            DatagramPacket packet = new DatagramPacket(FrameFinish, FrameFinish.length, InetAddress.getByName(Main.getBroadcastIpAddress()), port);
            dsocket.send(packet);
            dsocket.close();

            Main.setCourantFrame((byte) (Main.getCourantFrame() + 1));
        } catch (Exception e) {
            System.err.println(e);
        }
        if (Main.getCourantFrame() == (byte) 255) {
            Main.setCourantFrame((byte) 0);
        }
    }
}