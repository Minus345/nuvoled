package com.nuvoled.Util;

import java.util.GregorianCalendar;

public class Rgb565 {
    /**
     * returns the rgb565 data;
     *
     * @param input input array of rgb values
     * @return output translated to rgb565 values
     */
    public static byte[] getLedRgb565Data(byte[] input) {
        int rgbCounterNumber = 0;
        byte[] output = new byte[input.length]; //TODO: sollte noch kürzer sein

        for (int i = 0; i < input.length; i = i + 3) { //input bgr
            byte blue = input[i];
            byte green = input[i + 1];
            byte red = input[i + 2];

            // Convert byte to int to be within [0 , 255]
            int red5 = red & 0xff;
            int green6 = green & 0xff;
            int blue5 = blue & 0xff;

            //Source: https://barth-dev.de/about-rgb565-and-how-to-convert-into-it/
            int red5Shifted = (red5 & 0b11111000) << 8;
            int green6Shifted = (green6 & 0b11111100) << 3;
            int blue5Shifted = blue5 >> 3;

            int rgb565 = red5Shifted | green6Shifted | blue5Shifted; //output r g b

            short rgb565short = (short) rgb565;

            byte[] bytes = new byte[2];
            bytes[0] = (byte) (rgb565short);  //second 8 Bits
            bytes[1] = (byte) ((rgb565short >> 8)); //first 8 Bits

            //little fix for the "terminal Black" -> sets the green last green bit to 0; if its the only thing active
            if (bytes[0] == 32 && bytes[1] == 0) {
                bytes[0] = 0;
            }

            output[rgbCounterNumber] = bytes[0];
            rgbCounterNumber++;
            output[rgbCounterNumber] = bytes[1];
            rgbCounterNumber++;

        }

        return output;
    }
}
