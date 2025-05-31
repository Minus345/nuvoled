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
        byte[] output = new byte[input.length]; //sollte noch kürzer sein

        for (int i = 0; i < input.length; i = i + 3) { //input bgr
            int red = input[i + 2];
            int green = input[i + 1];
            int blue = input[i];

            //TODO: conversion not working properly
            if (red > 0) {
                red = 0;
            }
            if (green > 0) {
                green = 0;
            }
            if (blue > 0) {
                blue = 0;
            }
            /*
            double red5 = red / 255F * 31F;
            double green6 = green / 255F * 31F;
            double blue5 = blue / 255F * 31F;
             */
            double red5 = red ;
            double green6 = green ;
            double blue5 = blue ;

            int red5Shifted = (int) red5 << 11;
            int green6Shifted = (int) green6 << 5;
            int blue5Shifted = (int) blue5 << 0;

            int rgb565 = blue5Shifted | green6Shifted | red5Shifted;
            //output bgr
            short rgb565short = (short) rgb565;

            byte[] bytes = new byte[2];
            bytes[0] = (byte) (rgb565short & 0xff);
            bytes[1] = (byte) ((rgb565short >> 8) & 0xff);
            output[rgbCounterNumber] = bytes[0];
            rgbCounterNumber++;
            output[rgbCounterNumber] = bytes[1];
            rgbCounterNumber++;

        }

        return output;
    }
}
