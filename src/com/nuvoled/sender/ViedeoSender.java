package com.nuvoled.sender;

import com.nuvoled.Main;

import java.awt.image.BufferedImage;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ViedeoSender {
    public static byte[] rgb = new byte[Main.getPannelGroessex() * Main.getPannelGroessey() * 3];// 128*128*3

    public static void send(BufferedImage image) {
        int rgbCounterNumber = 0;
        if (!Main.isRotation()) {
            for (int y = 1; y <= Main.getPannelGroessey(); y++) {
                for (int x = 1; x <= Main.getPannelGroessex(); x++) {
                    int pixel = image.getRGB(x, y);
                    int red = (pixel >> 16) & 0xff;
                    int green = (pixel >> 8) & 0xff;
                    int blue = (pixel) & 0xff;
                    rgb[rgbCounterNumber] = (byte) blue;
                    rgbCounterNumber++;
                    rgb[rgbCounterNumber] = (byte) green;
                    rgbCounterNumber++;
                    rgb[rgbCounterNumber] = (byte) red;
                    rgbCounterNumber++;
                }
            }
        } else {
            for (int y = Main.getPannelGroessey(); y >= 1; y--) {
                for (int x = Main.getPannelGroessex(); x >= 1; x--) {
                    int pixel = image.getRGB(x, y);
                    int red = (pixel >> 16) & 0xff;
                    int green = (pixel >> 8) & 0xff;
                    int blue = (pixel) & 0xff;
                    rgb[rgbCounterNumber] = (byte) blue;
                    rgbCounterNumber++;
                    rgb[rgbCounterNumber] = (byte) green;
                    rgbCounterNumber++;
                    rgb[rgbCounterNumber] = (byte) red;
                    rgbCounterNumber++;
                }
            }
        }

        try {
            InetAddress address = InetAddress.getByName(Main.getAddr());
            DatagramSocket datagramSocket = new DatagramSocket();
            datagramSocket.setSendBufferSize(1048576);
            int pixel = 0;
            for (int counter = 0; counter <= ((Main.getPannelGroessex() * Main.getPannelGroessey() * 3) / 1440) + 1; counter++) { //35 = (128 * 128 * 3)/1440
                byte[] message = new byte[1450];
                message[0] = 36;
                message[1] = 36;
                message[2] = 20;
                message[3] = Main.getCourantFrame();
                message[4] = 10; //RGB
                message[5] = 0;
                message[6] = (byte) counter; //counter
                message[7] = 0;
                message[8] = (byte) ((byte) ((Main.getPannelGroessex() * Main.getPannelGroessey() * 3) / 1440) + 1);
                message[9] = 45;
                for (int i = 1; i < 1440; i = i + 3) {
                    if (pixel >= rgb.length) {
                        message[9 + i] = 0;
                        pixel++;
                        message[9 + 1 + i] = 0;
                        pixel++;
                        message[9 + 2 + i] = 0;
                        pixel++;
                    } else {
                        message[9 + i] = rgb[pixel];
                        pixel++;
                        message[9 + 1 + i] = rgb[pixel];
                        pixel++;
                        message[9 + 2 + i] = rgb[pixel];
                        pixel++;
                    }
                }
                DatagramPacket packet = new DatagramPacket(message, message.length, address, Main.getPort());
                datagramSocket.send(packet);
            }
            SendSync.send((byte) (Main.getCourantFrame() - 1));
            //Thread.sleep(20);
            //System.out.println("Sending Frame: " + Main.getCourantFrame());
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}