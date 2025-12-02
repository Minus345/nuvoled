package com.nuvoled.ndi;

public class YUV2RGB {
    public static int[] toRGB(int Y0, int U0, int V0) {

        double Y1 = (Y0 & 0xff) - 16D;
        double U1 = (U0 & 0xff) - 128;
        double V1 = (V0 & 0xff) - 128;

        // Algorithmus von https://de.wikipedia.org/wiki/YUV-Farbmodell
        double R = Y1 + 1 / 0.877 * V1;
        double G = Y1 - 0.144 / (0.587 * 0.493) * U1 - 0.299 / (0.587 * 0.877) * V1;
        double B = Y1 + 1 / 0.493 * U1;

        /*
        //Algorithmus von Copilot
        double R = Y1 + 1.13983 * V1 ;
        double G = Y1 - 0.39465 * U1 - 0.58060 * V1;
        double B = Y1 + 2.03211 * U1;
         */

        int roundR = roundTo255((int) R);
        int roundG = roundTo255((int) G);
        int roundB = roundTo255((int) B);

        // schwarz korrektur (wegen dem Compression Algorithmus von NDI)
        //TODO: Make it toogable
        final int minimalValue = 10;

        if (roundR < minimalValue) roundR = 0;
        if (roundG < minimalValue) roundG = 0;
        if (roundB < minimalValue) roundB = 0;

        int[] outputRGB = new int[3];
        outputRGB[0] = roundR;
        outputRGB[1] = roundG;
        outputRGB[2] = roundB;

        return outputRGB;
    }

    private static int roundTo255(int input) {
        if (input > 255) {
            return 255;
        }
        if (input < 0) {
            return 0;
        }
        return input;
    }
}
