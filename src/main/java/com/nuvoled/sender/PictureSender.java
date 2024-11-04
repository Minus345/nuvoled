package com.nuvoled.sender;

import com.nuvoled.Main;

import java.awt.image.BufferedImage;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class PictureSender {

    public static byte[] rgb = new byte[Main.getPanelSizeX() * Main.getPanelSizeY() * 3];// 128*128*3
    public static byte[] rgbOld = new byte[Main.getPanelSizeX() * Main.getPanelSizeY() * 3];
    private static boolean only_changed_pictures = false;
    private static int color_mode = 10;
    private static final boolean use_filter = false;
    private static final boolean debug = false;
    private static final boolean DEBUG_RGB = false;
    private static boolean image_identical = false;
    private static int channelOld = 0;
    private static int channel;

    public static void setScreenMode(boolean screenMode_b, int colormode) {
        only_changed_pictures = screenMode_b;
        color_mode = colormode;
    }

    public static void send(BufferedImage image) {
        send_rgb(image);
    }

    private static void send_rgb(BufferedImage image) {
        //checkPicture(image); // checks if the picture ist big enough
        //getRgbFromPicture(image, color_mode); //updates rgb array
        getLedRgb565Data(image);

         /*
        if (only_changed_pictures) {
            if (Arrays.equals(rgb, rgbOld)) {
                if (image_identical) {
                    if (debug) {
                        System.out.print(".");
                    }
                    return;
                } else {
                    if (debug) {
                        System.out.println("-");
                    }
                    image_identical = true;
                }
            } else {
                image_identical = false;
            }
        }

          */

        //Art net implementation
        if (Main.isArtnetEnabled()) {
            byte[] dmx = Main.getArtnet().readDmxData(Main.getSubnet(), Main.getUniversum());
            channel = Byte.toUnsignedInt(dmx[Main.getChannel()]);
            float value = channel / (float) 100;
            Main.setScaleFactor(value);

            if (Main.isArtnetDebug()) {
                System.out.println("A: " + channel);
                System.out.println("B: " + value);
            }
        }

        int pixel = 0;
        int MaxPackets = ((Main.getPanelSizeX() * Main.getPanelSizeY() * 2) / 1440) + 1; //rgb -> 3 rgb565 -> 2
        //System.out.println(Main.getPanelSizeX() + " : " + Main.getPanelSizeY() );

        for (int counter = 0; counter <= MaxPackets; counter++) {
            byte[] message = new byte[1450];
            message[0] = 36;
            message[1] = 36;
            message[2] = 20;
            message[3] = Main.getCourantFrame();
            message[4] = (byte) (30); //RGB -> 10 JPG -> 20 RGB565 -> 30
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

        // System.arraycopy(rgb, 0, rgbOld, 0, rgb.length);
    }

    private static void printRgbFromPicture(BufferedImage image) {
        int rgbCounterNumber = 0;
        for (int y = 1; y <= 1; y++) {
            for (int x = 1; x <= Main.getPanelSizeX(); x++) {
                int pixel = image.getRGB(x, y);
                int red = (pixel >> 16) & 0xff;
                int green = (pixel >> 8) & 0xff;
                int blue = (pixel) & 0xff;
                if (debug) {
                    System.out.print((byte) blue);
                    System.out.print(" ");
                    System.out.print((byte) green);
                    System.out.print(" ");
                    System.out.print((byte) red);
                    System.out.print(" ");
                }
                rgb[rgbCounterNumber] = (byte) blue;
                rgbCounterNumber++;
                rgb[rgbCounterNumber] = (byte) green;
                rgbCounterNumber++;
                rgb[rgbCounterNumber] = (byte) red;
                rgbCounterNumber++;
            }
        }

    }

    private static void getRgbFromPicture(BufferedImage image, int colormode) {

        switch (colormode) {
            case 10:
                getLedRgbData(image);
                break;
            case 20:
                getLedJpgData(image);
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

                double red5 = blue / 255F * 31F;
                double green6 = red / 255F * 31F;
                double blue5 = green / 255F * 31F;

                int red5Shifted = (int) red5 << 11;
                int green6Shifted = (int) green6 << 5;
                int blue5Shifted = (int) blue5 << 0;

                int rgb565 = red5Shifted | green6Shifted | blue5Shifted;

                short rgb565short = (short) rgb565;

                byte[] bytes = BigInteger.valueOf(rgb565short).toByteArray();

                if (bytes.length != 2) {
                    rgb[rgbCounterNumber] = 0;
                    rgbCounterNumber++;
                    rgb[rgbCounterNumber] = (byte) rgb565short;
                    rgbCounterNumber++;
                } else {
                    rgb[rgbCounterNumber] = bytes[0];
                    rgbCounterNumber++;
                    rgb[rgbCounterNumber] = bytes[1];
                    rgbCounterNumber++;
                }


                //System.out.println(rgb565);
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

    private static void getLedJpgData(BufferedImage image) {

        int rgbCounternumber = 0;
        int panelSize = 128;
        //System.out.println( "x: " + Main.getPanelSizeX() + " Y: " +  Main.getPanelSizeY());
        int rowsX = Main.getPanelSizeX() / panelSize;
        int rowsY = Main.getPanelSizeY() / panelSize;
        for (int y = 0; y < rowsY; y++) {
            for (int x = 0; x < rowsX; x++) {
                rgbCounternumber = getPixelPerPanel(image, rgbCounternumber, x, y, panelSize);
            }
        }
    }

    private static int getPixelPerPanel(BufferedImage image, int rgbCounternumber, int rowX, int colY, int panelSize) {
        int startX = rowX * panelSize;
        int startY = colY * panelSize;
        for (int y = 0; y < panelSize; y++) {
            for (int x = 0; x < panelSize; x++) {
                int pixel = image.getRGB(startX + x, startY + y);
                int red = (pixel >> 16) & 0xff;
                int green = (pixel >> 8) & 0xff;
                //int blue = (pixel) & 0xff;
                rgb[rgbCounternumber] = (byte) ((pixel) & 0xff); //blue
                rgbCounternumber++;
                rgb[rgbCounternumber] = (byte) green;
                rgbCounternumber++;
                rgb[rgbCounternumber] = (byte) red;
                rgbCounternumber++;
            }
        }
        return rgbCounternumber;
    }

    private static void checkPicture(BufferedImage image) {
        if (image.getHeight() < Main.getPanelSizeY() || image.getWidth() < Main.getPanelSizeX()) {
            System.out.println("Falsches Format");
            System.out.println("Bitte Format von mindestens " + Main.getPanelSizeX() + " * " + Main.getPanelSizeY() + " Pixeln verwenden");
            System.exit(0);
        }
    }


}
