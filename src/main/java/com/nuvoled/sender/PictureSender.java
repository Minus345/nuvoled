package com.nuvoled.sender;

import com.nuvoled.Main;

import java.awt.image.BufferedImage;
import java.util.Arrays;

public class PictureSender {

    public static byte[] rgb = new byte[Main.getPanelSizeX() * Main.getPanelSizeY() * 3];
    public static byte[] rgbOld = new byte[Main.getPanelSizeX() * Main.getPanelSizeY() * 3];
    private static boolean only_changed_pictures = false;
    private static final int color_mode = Main.getColorMode();
    private static final boolean debugForOnlySendChangedPicture = false;
    private static boolean image_identical = false;

    public static void send(BufferedImage image) {
        getFormat(image, color_mode); //updates rgb array
        artNetCheck();
        onlySendChangedPicture();

        int pixel = 0;
        int MaxPackets;

        if (color_mode == 30) {
            MaxPackets = ((Main.getPanelSizeX() * Main.getPanelSizeY() * 2) / 1440) + 1; //rgb -> 3 rgb565 -> 2
        } else {
            MaxPackets = ((Main.getPanelSizeX() * Main.getPanelSizeY() * 3) / 1440) + 1; //rgb -> 3 rgb565 -> 2
        }

        for (int counter = 0; counter <= MaxPackets; counter++) {
            byte[] message = new byte[1450];
            message[0] = 36;
            message[1] = 36;
            message[2] = 20;
            message[3] = Main.getCourantFrame();
            message[4] = (byte) (color_mode); //RGB -> 10 JPG -> 20 RGB565 -> 30
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
        try {
            Thread.sleep(Main.getSleep());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        SendSync.send_end_frame();

        //gehört zu onlySendChangedPicture
        System.arraycopy(rgb, 0, rgbOld, 0, rgb.length);
    }

    private static void getFormat(BufferedImage image, int colormode) {

        switch (colormode) {
            case 10:
                getLedRgbData(image);
                break;
            case 30:
                getLedRgb565Data(image);
                break;
        }
    }

    private static void getLedRgb565Data(BufferedImage image) {

        int rgbCounterNumber = 0;

        for (int y = 0; y < Main.getPanelSizeY(); y++) {
            for (int x = 0; x < Main.getPanelSizeX(); x++) {
                int pixel = image.getRGB(x, y);
                int red = (pixel >> 16) & 0xff;
                int green = (pixel >> 8) & 0xff;
                int blue = (pixel) & 0xff;

                double red5 = red / 255F * 31F;
                double green6 = green / 255F * 31F;
                double blue5 = blue / 255F * 31F;

                int red5Shifted = (int) red5 << 11;
                int green6Shifted = (int) green6 << 5;
                int blue5Shifted = (int) blue5 << 0;

                int rgb565 = red5Shifted | green6Shifted | blue5Shifted;
                short rgb565short = (short) rgb565;

                byte[] bytes = new byte[2];
                bytes[0] = (byte) (rgb565short & 0xff);
                bytes[1] = (byte) ((rgb565short >> 8) & 0xff);
                rgb[rgbCounterNumber] = bytes[0];
                rgbCounterNumber++;
                rgb[rgbCounterNumber] = bytes[1];
                rgbCounterNumber++;

            }
        }

        //System.out.println(Arrays.toString(rgb));
    }

    private static void getLedRgbData(BufferedImage image) {

        int rgbCounterNumber = 0;

        if (Main.rotationDegree() == 180) {
            //System.out.println( "x: " + Main.getPanelSizeX() + " Y: " +  Main.getPanelSizeY());
            for (int y = Main.getPanelSizeY() - 1; y >= 0; y--) {
                for (int x = Main.getPanelSizeX() - 1; x >= 0; x--) {
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

        } else if (Main.rotationDegree() == 90) {
            //System.out.println( "x: " + Main.getPanelSizeX() + " Y: " +  Main.getPanelSizeY());
            for (int x = Main.getPanelSizeX() - 1; x >= 0; x--) {
                for (int y = 0; y < Main.getPanelSizeY(); y++) {
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
        } else if (Main.rotationDegree() == 270) {
            //System.out.println( "x: " + Main.getPanelSizeX() + " Y: " +  Main.getPanelSizeY());
            for (int x = 0; x < Main.getPanelSizeX(); x++) {
                for (int y = Main.getPanelSizeY() - 1; y >= 0; y--) {
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
            for (int y = 0; y < Main.getPanelSizeY(); y++) {
                for (int x = 0; x < Main.getPanelSizeX(); x++) {
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

    private static void artNetCheck() {
        //Art net implementation
        if (Main.isArtnetEnabled()) {
            byte[] dmx = Main.getArtnet().readDmxData(Main.getSubnet(), Main.getUniversum());
            int channel = Byte.toUnsignedInt(dmx[Main.getChannel()]);
            float value = channel / (float) 100;
            Main.setScaleFactor(value);

            if (Main.isArtnetDebug()) {
                System.out.println("A: " + channel);
                System.out.println("B: " + value);
            }
        }
    }

    private static void onlySendChangedPicture() {
        if (only_changed_pictures) {
            if (Arrays.equals(rgb, rgbOld)) {
                if (image_identical) {
                    if (debugForOnlySendChangedPicture) {
                        System.out.print(".");
                    }
                    return;
                } else {
                    if (debugForOnlySendChangedPicture) {
                        System.out.println("-");
                    }
                    image_identical = true;
                }
            } else {
                image_identical = false;
            }
        }
    }

}
