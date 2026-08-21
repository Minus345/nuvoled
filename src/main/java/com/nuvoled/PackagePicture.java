package com.nuvoled;

import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

public class PackagePicture {

    private static final int PICTURE_DATA_LENGTH = 1440;
    private static final int SINGLE_PACKET_LENGTH = PICTURE_DATA_LENGTH + 10; //10 bytes header + 1440 bytes pixel data

    public static void packageAndSendPixels(byte[] pixelData, ManageNetworkConnection manageNetworkConnection, int colorMode) {
        int pixel = 0;
        int packetsCount = Math.ceilDiv(pixelData.length, PICTURE_DATA_LENGTH);
        //splits up the array int SINGLE_PACKET_LENGTH byte long messages
        for (int j = 0; j <= packetsCount; j++) {
            //message header
            byte[] message = new byte[SINGLE_PACKET_LENGTH];
            message[0] = 36;
            message[1] = 36;
            message[2] = 20;
            message[3] = Main.getCourantFrame(); //cur frame
            message[4] = (byte) (colorMode); //RGB -> 10 JPG -> 20 RGB565 -> 30
            message[5] = (byte) (j >> 8); //cur packH
            message[6] = (byte) (j & 0xff); //cur PackL
            message[7] = (byte) (packetsCount >> 8); // #PackH
            message[8] = (byte) (packetsCount & 0xff); // #PackL
            message[9] = 45; //cur package size or fix?

            for (int i = 0; i < PICTURE_DATA_LENGTH; i++) {
                if (pixel >= pixelData.length) {
                    //sets the last bytes of the last package to 0
                    message[10 + i] = 0;
                } else {
                    message[10 + i] = pixelData[pixel];
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
    public static BufferedImage applyFilter(BufferedImage image, double brightness, double offset) {
        RescaleOp rescaleOp = new RescaleOp((float) brightness, (float) offset, null);
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
    public static byte[] getLedBGRDataFormImage(BufferedImage image, int length) {
        byte[] rgb = new byte[length];
        int rgbCounterNumber = 0;
        for (int y = 0; y < Main.getGlobalPixelInY(); y++) {
            for (int x = 0; x < Main.getGlobalPixelInX(); x++) {
                int pixel = image.getRGB(x, y);
                int red = (pixel >> 16) & 0xff;
                int green = (pixel >> 8) & 0xff;
                int blue = (pixel) & 0xff;

                // send rgb data in little-endian to panels
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
