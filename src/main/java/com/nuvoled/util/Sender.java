package com.nuvoled.debug;

import com.nuvoled.Main;
import com.nuvoled.sender.ManageNetworkConnection;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;

public class Sender {
    public static void main(String[] args) throws IOException, InterruptedException {
        InetAddress address = InetAddress.getByName("255.255.255.255");
        System.out.println(address);
        DatagramSocket datagramSocket = new DatagramSocket();
        datagramSocket.setBroadcast(true);
        for (int i = 0; i < Integer.parseInt(args[0]); i++) {
            DatagramPacket packet = new DatagramPacket(getMessage(i), getMessage(i).length, address, Main.getPort());
            datagramSocket.send(packet);
            Thread.sleep(100);
        }
    }

    private static byte[] getMessage(int i){
        byte[] mac = new byte[4];
        mac[0] = 0;
        mac[1] = 0;
        mac[2] = 0;
        mac[3] = Byte.parseByte(String.valueOf(i));
        System.out.println(Arrays.toString(mac));
        byte[] message = new byte[11];
        message[0] = 36;
        message[1] = 36;
        message[2] = 15;
        message[3] = mac[3];
        message[4] = mac[2];
        message[5] = mac[1];
        message[6] = mac[0];
        message[7] = 'T';
        message[8] = 'E';
        message[9] = 'S';
        message[10] = 'T';

        return message;
    }
}