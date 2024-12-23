package com.nuvoled.ndi;

import java.awt.*;

public class YUV2RGB {
    public static int[] toRGB(int Y0, int U0, int V0) {

        int Y1 = Y0 & 0xff;
        int U1 = U0 & 0xff;
        int V1 = V0 & 0xff;

        double R;
        double G;
        double B;

        R = (double) Y1 + (1.4075 * ((double) V1 - 128));
        G = (double) Y1 - 0.3455 * ((double) U1 - 128) - (0.7169 * ((double) V1 - 128));
        B = (double) Y1 + 1.7790 * ((double) U1 - 128);

        int roundR = roundTo255((int)R);
        int roundG = roundTo255((int)G);
        int roundB = roundTo255((int)B);

        //System.out.println(roundR + "," + roundG + "," + roundB);

        int[] outputRGB = new int[3];
        outputRGB[0] = roundR;
        outputRGB[1] = roundB;
        outputRGB[2] = roundG;
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
