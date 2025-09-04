package com.nuvoled.sender;

import com.nuvoled.Main;
import com.nuvoled.util.Rgb565;
import com.nuvoled.util.Rotation;

import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

public class PictureSender {

    public static byte[] rgb = new byte[Main.getGlobalPixelInX() * Main.getGlobalPixelInY() * 3];
    public static byte[] rgbOld = new byte[Main.getGlobalPixelInX() * Main.getGlobalPixelInY() * 3];

    public static void send(BufferedImage image) {
        getFormat(applyFilter(image)); //updates rgb array
        artNetCheck();

        int pixel = 0;
        int MaxPackets;

        if (Main.getColorMode() == 30) {
            MaxPackets = ((Main.getGlobalPixelInX() * Main.getGlobalPixelInY() * 2) / 1440) + 1; //rgb -> 3 rgb565 -> 2
        } else {
            MaxPackets = ((Main.getGlobalPixelInX() * Main.getGlobalPixelInY() * 3) / 1440) + 1; //rgb -> 3 rgb565 -> 2
        }

        //prep message
        for (int counter = 0; counter <= MaxPackets; counter++) {
            byte[] message = new byte[1450];
            message[0] = 36;
            message[1] = 36;
            message[2] = 20;
            message[3] = Main.getCourantFrame();
            message[4] = (byte) (Main.getColorMode()); //RGB -> 10 JPG -> 20 RGB565 -> 30
            message[5] = (byte) (counter >> 8);
            message[6] = (byte) (counter & 255);
            message[7] = (byte) (MaxPackets >> 8);
            message[8] = (byte) (MaxPackets & 255);
            message[9] = 45;

            for (int i = 1; i < 1440; i = i + 3) {
                if (pixel >= rgb.length) {
                    //setzt die letzten bytes des Packets auf 0
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
            ManageNetworkConnection.send_data(message);
        }
        try {
            Thread.sleep(Main.getSleep());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        ManageNetworkConnection.sendSyncro();

        //gehört zu onlySendChangedPicture
        System.arraycopy(rgb, 0, rgbOld, 0, rgb.length);
    }

    private static void getFormat(BufferedImage image) {

        switch (Main.getColorMode()) {
            case 10:
                getLedRgbDataNewRotFunction(image);
                break;
            case 30:
                getLedRgbDataNewRotFunction(image);
                rgb = Rgb565.getLedRgb565Data(rgb);
                break;
        }
    }

    /**
     * Sets the brightness of the picture
     *
     * @param image
     * @return
     */
    public static BufferedImage applyFilter(BufferedImage image) {
        RescaleOp rescaleOp = new RescaleOp(Main.getBrightness(), Main.getOffSet(), null);
        rescaleOp.filter(image, image);  // Source and destination are the same.
        return image;
    }

    private static void getLedRgbDataNewRotFunction(BufferedImage image) {
        int rgbCounterNumber = 0;
        for (int y = 0; y < Main.getGlobalPixelInY(); y++) {
            for (int x = 0; x < Main.getGlobalPixelInX(); x++) {
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

        switch (Main.getRotation()) {
            case 90 -> rgb = Rotation.rotate90(rgb, Main.getGlobalPixelInX(), Main.getGlobalPixelInY());
            case 180 -> //noinspection DataFlowIssue -> not imlemented
                    rgb = Rotation.rotate180(rgb, Main.getGlobalPixelInX(), Main.getGlobalPixelInY());
            case 270 -> rgb = Rotation.rotate270(rgb, Main.getGlobalPixelInX(), Main.getGlobalPixelInY());
        }
    }

    private static void artNetCheck() {
        //Art net implementation
        if (Main.isArtnetEnabled()) {
            byte[] dmx = Main.getArtnet().readDmxData(Main.getSubnet(), Main.getUniversum());
            int channel = Byte.toUnsignedInt(dmx[Main.getChannel()]);
            float value = channel / (float) 100;
            Main.setBrightness(value);

            if (Main.isArtnetDebug()) {
                System.out.println("A: " + channel);
                System.out.println("B: " + value);
            }
        }
    }
}
