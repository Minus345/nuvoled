package com.nuvoled.sender;

import com.nuvoled.Main;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.*;
import java.net.*;
import java.util.Arrays;

public class PictureSender {

    public static byte[] rgb = new byte[Main.getPanelSizeX() * Main.getPanelSizeY() * 3];// 128*128*3
    public static byte[] rgbOld = new byte[Main.getPanelSizeX() * Main.getPanelSizeY() * 3];

    private static boolean image_identical = false;

    public static void send(BufferedImage image, DatagramSocket datagramSocket) {

        System.out.println("process image");
        try {
            //File f1 = new File("//Users/MFU/Pictures/myimage.tiff");
            //ImageIO.write(image, "tiff", f1);
            //File f2 = new File("//Users/MFU/Pictures/myimage.jpg");
            //ImageIO.write(image, "jpg", f2);
            //System.out.println("image on disk");

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(image, "jpeg", os);
            System.out.println(os.size());
            byte[] byteArray = os.toByteArray();
            int length = 0;
            if (byteArray.length > 100) {
                length = 100;
            }
            for (int i = 0; i < length; i++) {
                System.out.print(byteArray[i]);
                System.out.print(" ");
            }

            System.out.println("");
            System.out.println("Meta Data:");
            java.util.Iterator<javax.imageio.ImageReader> readers = ImageIO.getImageReaders(os);

            if (readers.hasNext()) {
                System.out.println("---> ");
                // pick the first available ImageReader
                javax.imageio.ImageReader reader = readers.next();
                // attach source to the reader
                reader.setInput(os, true);
                // read metadata of first image
                IIOMetadata metadata = reader.getImageMetadata(0);

                String[] names = metadata.getMetadataFormatNames();
                int length2 = names.length;
                for (int i = 0; i < length2; i++) {
                    System.out.println("Format name: " + names[i]);
                }
            }

            System.out.println("");
            System.out.println("Image Buffer RGBdata:");
            printRgbFromPicture(image);
            System.out.println("");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ;

        send_rgb(image, datagramSocket);

        System.exit(0);

    }

    private static void send_rgb(BufferedImage image, DatagramSocket datagramSocket) {
        checkPicture(image); // checks if the picture ist big enough
        getRgbFromPicture(image); //gets rgb data from pictures

        if (Arrays.equals(rgb, rgbOld)) {
            if (image_identical) {
                return;
            } else {
                image_identical = true;
            }
        } else {
            image_identical = false;
        }

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
            send_data(message, datagramSocket);
        }

        System.arraycopy(rgb, 0, rgbOld, 0, rgb.length);
    }

    private static void send_data(byte[] message, DatagramSocket datagramSocket) {

        InetAddress address = null;
        try {
            address = InetAddress.getByName(Main.getBroadcastIpAddress());
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }

        if (datagramSocket.isClosed()) {
            System.out.println("Reconnect");
            datagramSocket.close();
            datagramSocket.connect(address, Main.getPort());
        }

        try {
            datagramSocket.setSendBufferSize(2048576);
            DatagramPacket packet = new DatagramPacket(message, message.length, address, Main.getPort());
            datagramSocket.send(packet);
            //SendSync.sendFrameFinish(Main.getCourantFrame(), (byte) (MaxPackets >> 8), (byte) (MaxPackets & 255));
            SendSync.sendSyncro((byte) (Main.getCourantFrame() - 1));
            System.out.println("Sending Frame: " + Main.getCourantFrame());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    private static void printRgbFromPicture(BufferedImage image) {
        int rgbCounterNumber = 0;
        for (int y = 1; y <= 1; y++) {
            for (int x = 1; x <= Main.getPanelSizeX(); x++) {
                int pixel = image.getRGB(x, y);
                int red = (pixel >> 16) & 0xff;
                int green = (pixel >> 8) & 0xff;
                int blue = (pixel) & 0xff;
                System.out.print((byte) blue);
                System.out.print(" ");
                System.out.print((byte) green);
                System.out.print(" ");
                System.out.print((byte) red);
                System.out.print(" ");
                rgb[rgbCounterNumber] = (byte) blue;
                rgbCounterNumber++;
                rgb[rgbCounterNumber] = (byte) green;
                rgbCounterNumber++;
                rgb[rgbCounterNumber] = (byte) red;
                rgbCounterNumber++;
            }
        }

    }

    private static void getRgbFromPicture(BufferedImage image) {
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

    private static void checkPicture(BufferedImage image) {
        if (image.getHeight() < Main.getPanelSizeY() || image.getWidth() < Main.getPanelSizeX()) {
            System.out.println("Falsches Format");
            System.out.println("Bitte Format von mindestens " + Main.getPanelSizeX() + " * " + Main.getPanelSizeY() + " Pixeln verwenden");
            System.exit(0);
        }
    }


}
