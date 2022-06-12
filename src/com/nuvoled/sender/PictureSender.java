package com.nuvoled.sender;

import com.nuvoled.Main;

import java.awt.image.BufferedImage;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class PictureSender {
    public static void send(BufferedImage image) {
        if (image.getHeight() < Main.getPannelGroessey() || image.getWidth() < Main.getPannelGroessex()) {
            System.out.println("Falsches Format");
            System.out.println("Bitte Format von mindestens 128 * 128 Pixeln verwenden");
            System.exit(0);
        }
        byte[] rgb = new byte[Main.getPannelGroessex() * Main.getPannelGroessey() * 3];// 128*128*3
        int rgbCounterNumber = 0;
        for (int y = 0; y <= (Main.getPannelGroessey() - 1); y++) {
            for (int x = 0; x <= (Main.getPannelGroessex() - 1); x++) {
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
                //System.out.println("x: " + x + " " + "y: " + y + " | " + "red: " + red + ", green: " + green + ", blue: " + blue + " | " + rgbCounterNumber + " | " + rgbCounterNumber / 3);
            }
        }

        try {
            int pixel = 0;
            for (int counter = 0; counter <= ((Main.getPannelGroessex() * Main.getPannelGroessey() * 3)/ 1440) + 1; counter++) { //35 = (128 * 128 * 3)/1440
                byte[] message = new byte[1450];
                message[0] = 36;
                message[1] = 36;
                message[2] = 20;
                message[3] = Main.getCourantFrame();
                message[4] = 10; //RGB
                message[5] = 0;
                message[6] = (byte) counter; //counter
                message[7] = 0;
                message[8] = (byte) ((byte) ((Main.getPannelGroessex() * Main.getPannelGroessey() * 3)/ 1440) + 1);
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

                InetAddress address = InetAddress.getByName(Main.getAddr());
                DatagramSocket datagramSocket = new DatagramSocket();
                DatagramPacket packet = new DatagramPacket(message, message.length, address, Main.getPort());
                datagramSocket.send(packet);
                //datagramSocket.close();
                //Thread.sleep(10);
            }
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}
