package com.nuvoled.sender;

import com.nuvoled.Main;


import javax.imageio.ImageIO;
import javax.imageio.metadata.IIOMetadata;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;

public class PictureSender {

    public static byte[] rgb = new byte[Main.getPanelSizeX() * Main.getPanelSizeY() * 3];// 128*128*3
    public static byte[] rgbOld = new byte[Main.getPanelSizeX() * Main.getPanelSizeY() * 3];

    private static final boolean debug = false;
    private static final boolean DEBUG_RGB = false;

    private static boolean image_identical = false;


    public static void send(BufferedImage image) {

        try {

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", os);
            if (debug) {
                System.out.println(os.size());
            }
            //File f = new File("c:/data/myimage.jpg");//set appropriate path
            //ImageIO.write(image, "jpg", f)

            //send_jpg(os);

            if (DEBUG_RGB) {
                System.out.println(":");
                System.out.println("Image Buffer RGBdata:");
                printRgbFromPicture(image);
                System.out.println(":");
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        send_rgb(image);
    }

    private static void send_rgb(BufferedImage image){
        checkPicture(image); // checks if the picture ist big enough
        getRgbFromPicture(image); //gets rgb data from pictures

        if (Arrays.equals(rgb, rgbOld)) {
            if (image_identical) {
                System.out.print(".");
                return;
            } else {
                System.out.println("-");
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
            message[4] = 10; //RGB -> 10 JPG -> 20
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
                } else {
                    message[9 + i] = rgb[pixel];
                    pixel++;
                    message[9 + 1 + i] = rgb[pixel];
                    pixel++;
                    message[9 + 2 + i] = rgb[pixel];
                }
                pixel++;
            }
            SendSync.send_data(message);
        }
        SendSync.send_end_frame();
        System.arraycopy(rgb, 0, rgbOld, 0, rgb.length);
    }

    private static void send_jpg( ByteArrayOutputStream image ) {

        int pixel = 0;
        int MaxPackets = image.toByteArray().length/1440 +1;
        System.out.println(("Packages " + MaxPackets ));
        for (int counter = 0; counter < MaxPackets; counter++) { //35 = (128 * 128 * 3)/1440
            byte[] message = new byte[1450];
            message[0] = 36;
            message[1] = 36;
            message[2] = 20;
            message[3] = Main.getCourantFrame();
            message[4] = 20; //RGB -> 10 JPG -> 20
            message[5] = (byte) (counter >> 8);
            message[6] = (byte) (counter & 255);
            message[7] = (byte) (MaxPackets >> 8);
            message[8] = (byte) (MaxPackets & 255);
            message[9] = 45;

            for (int i = 1; i < 1440; i++) {
                if (pixel >= image.toByteArray().length) {
                    //setzt die letzten bytes des Psackest auf 0
                    message[9 + i] = 0;
                } else {
                    message[9 + i] = image.toByteArray()[pixel];
                }
                pixel++;
            }
            SendSync.send_data(message);
        }
        SendSync.send_end_frame();
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
