package com.nuvoled;

import com.nuvoled.ndi.Point;

public class Rotation {
    public static void rotate90(byte rgb[], int ndiPixelX, int ndiPixelY) {
        //conversion into Points
        Point[] rgbPoint = new Point[rgb.length / 3];
        int a = 0;
        for (int i = 0; i < rgb.length / 3; i++) {
            rgbPoint[i] = new Point(rgb[a], rgb[a + 1], rgb[a + 2]);
            a = a + 3;
        }

        //conversion into 2D Array
        Point[][] notRotated = new Point[ndiPixelY][ndiPixelX];
        int rgbArrayPositionCounter = 0;
        for (int i = 0; i < notRotated.length; i++) {
            for (int j = 0; j < notRotated[i].length; j++) {
                notRotated[i][j] = rgbPoint[rgbArrayPositionCounter];
                rgbArrayPositionCounter++;
            }
        }

        //rotation
        //input int[y][x] -> int[4][3]

        int resY = ndiPixelY;
        int resX = ndiPixelX;

        // int[x][y]
        Point[][] Rotated = new Point[resX][resY];

        for (int i = 0; i < resY; i++) {
            for (int j = 0; j < resX; j++) {
                //System.out.println(i + ", " + j + " -> " + j + ", " + (resY - i - 1));
                Rotated[j][resY - i - 1] = notRotated[i][j];
            }
        }

        //conversion to 1D Array
        rgbArrayPositionCounter = 0;
        for (int i = 0; i < Rotated.length; i++) {
            for (int j = 0; j < Rotated[i].length; j++) {
                rgbPoint[rgbArrayPositionCounter] = Rotated[i][j];
                rgbArrayPositionCounter++;
            }
        }


        //conversion into standard values
        a = 0;
        for (int i = 0; i < rgb.length; i = i + 3) {
            rgb[i] = rgbPoint[a].getR();
            rgb[i + 1] = rgbPoint[a].getG();
            rgb[i + 2] = rgbPoint[a].getB();
            a++;
        }
    }
    public static void rotate180(byte rgb[], int ndiPixelX, int ndiPixelY){
    }
    public static void rotate270(byte rgb[], int ndiPixelX, int ndiPixelY){
    }
}
