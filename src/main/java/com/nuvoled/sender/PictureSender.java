package com.nuvoled.sender;

import com.nuvoled.Main;

import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

public class PictureSender {

    private static final int SINGLE_PACKET_LENGTH = 1450;

    public static void packageAndSendPixels(byte[] rgb, int maxPackets, ManageNetworkConnection manageNetworkConnection) {
        int pixel = 0;
        //splits up the array int SINGLE_PACKET_LENGTH byte long messages
        for (int counter = 0; counter <= maxPackets; counter++) {
            //prep message
            byte[] message = new byte[SINGLE_PACKET_LENGTH];
            message[0] = 36;
            message[1] = 36;
            message[2] = 20;
            message[3] = Main.getCourantFrame();
            message[4] = (byte) (Main.getColorMode()); //RGB -> 10 JPG -> 20 RGB565 -> 30
            message[5] = (byte) (counter >> 8);
            message[6] = (byte) (counter & 255);
            message[7] = (byte) (maxPackets >> 8);
            message[8] = (byte) (maxPackets & 255);
            message[9] = 45;

            for (int i = 1; i < SINGLE_PACKET_LENGTH - 10; i = i + 3) {
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
            manageNetworkConnection.send_data(message);
        }
    }


    /**
     * Sets the brightness of the picture
     *
     * @param image
     * @return
     */
    public static BufferedImage applyFilter(BufferedImage image, float brightness, float offset) {
        RescaleOp rescaleOp = new RescaleOp(brightness, offset, null);
        rescaleOp.filter(image, image);  // Source and destination are the same.
        return image;
    }

    /**
     * gets the rgb information from the picture and puts it into an array
     *
     * @param image  input picture
     * @param length output byte array length -> must be calculated before (x * y * 3)
     * @return byte array with rgb data
     */
    public static byte[] getLedRgbDataFormImage(BufferedImage image, int length) {
        byte[] rgb = new byte[length];
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
        return rgb;
    }
}
