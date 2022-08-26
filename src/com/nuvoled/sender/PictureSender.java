package com.nuvoled.sender;

import com.nuvoled.Main;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.*;
import java.util.Arrays;

public class PictureSender {

    public static byte[] rgb = new byte[Main.getPanelSizeX() * Main.getPanelSizeY() * 3];// 128*128*3
    public static byte[] rgbOld = new byte[Main.getPanelSizeX() * Main.getPanelSizeY() * 3];

    public static void send(BufferedImage image, DatagramSocket datagramSocket) {
        checkPicture(image); // checks if the picture ist big enough
        getRgbFromPicture(image); //gets rgb data from pictures

        if (Arrays.equals(rgb, rgbOld)) {
            return;
        }

        try {
            InetAddress address = InetAddress.getByName(Main.getBroadcastIpAddress());
            try {
                int pixel = 0;
                int MaxPackets = ((Main.getPanelSizeX() * Main.getPanelSizeY() * 3) / 1440) + 1;
                for (int counter = 0; counter <= MaxPackets; counter++) { //35 = (128 * 128 * 3)/1440
                    byte[] message = new byte[1450];
                    message[0] = 36;
                    message[1] = 36;
                    message[2] = 20;
                    message[3] = Main.getCourantFrame();
                    message[4] = 10; //RGB
                    message[5] = (byte) (counter >> 8);
                    message[6] = (byte) (counter & 255);
                    message[7] = (byte) (MaxPackets >> 8);
                    message[8] = (byte) (MaxPackets & 255);
                    message[9] = 45;

                    for (int i = 1; i < 1440; i = i + 3) {
                        if (pixel >= rgb.length) {
                            //setzt die letzten bytes des Psackest auf 0
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

                    if (datagramSocket.isClosed()) {
                        System.out.println("Reconnect");
                        datagramSocket.close();
                        datagramSocket.connect(address, Main.getPort());
                    }

                    datagramSocket.setSendBufferSize(2048576);
                    DatagramPacket packet = new DatagramPacket(message, message.length, address, Main.getPort());
                    datagramSocket.send(packet);

                }
                //Thread.sleep(5);
                //SendSync.sendFrameFinish(Main.getCourantFrame(), (byte) (MaxPackets >> 8), (byte) (MaxPackets & 255));
                //Thread.sleep(30);
                SendSync.sendSyncro((byte) (Main.getCourantFrame() - 1));
                //Thread.sleep(30);
                //SendSync.sendSyncro(Main.getCourantFrame());
                System.out.println("Sending Frame: " + Main.getCourantFrame());
                System.arraycopy(rgb, 0, rgbOld, 0, rgb.length);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void getRgbFromPicture(BufferedImage image){
        int rgbCounterNumber = 0;
        if (!Main.isRotation()) {
            for (int y = 1; y <= Main.getPanelSizeY(); y++) {
                for (int x = 1; x <= Main.getPanelSizeX(); x++) {
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
            for (int y = Main.getPanelSizeY(); y >= 1; y--) {
                for (int x = Main.getPanelSizeX(); x >= 1; x--) {
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
    }

    private static void checkPicture(BufferedImage image){
        if (image.getHeight() < Main.getPanelSizeY() || image.getWidth() < Main.getPanelSizeX()) {
            System.out.println("Falsches Format");
            System.out.println("Bitte Format von mindestens " + Main.getPanelSizeX() + " * " + Main.getPanelSizeY() + " Pixeln verwenden");
            System.exit(0);
        }
    }
}