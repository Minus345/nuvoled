package com.nuvoled.util;

public class Rgb565 {
    /**
     * returns the rgb565 data;
     *
     * @param input input array of bgr values / little-endian
     * @return output translated to bgr565 values / little-endian
     */
    public static byte[] convertBGR888ToBGR565(byte[] input) {
        int rgbCounterNumber = 0;
        byte[] output = new byte[input.length * 2 / 3]; // pixelX * pixelY * 2 -> only 2 byte instead of 3

        for (int i = 0; i < input.length; i = i + 3) {
            /*
            https://docs.oracle.com/javase/specs/jls/se10/html/jls-5.html#jls-5.6.2
            all byte operations are performed as integer operations

            convert to int: [0-255] unsigned read
            because if not:
            -> input [(byte) 255,0,0]
            blue = (byte 255) = -1 | byte is singed in java
            blue >> 3 = -1
             */
            int blue = input[i] & 0xff;
            int green = input[i + 1] & 0xff;
            int red = input[i + 2] & 0xff;

            short RGB565 = (short) (((red & 0xf8) << 8) + ((green & 0xfc) << 3) + (blue >> 3));

            // Little-endian:
            output[rgbCounterNumber] = (byte) (RGB565 & 0xff);
            rgbCounterNumber++;
            output[rgbCounterNumber] = (byte) ((RGB565 >> 8) & 0xff);
            rgbCounterNumber++;
        }
        return output;
    }
}
