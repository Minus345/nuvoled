package com.nuvoled.Util;

import com.nuvoled.ndi.Point;

public class Rotation {
    public static byte[] rotate90(byte[] rgb, int ndiPixelX, int ndiPixelY) {
        Point[][] notRotated = decodePointTo2DArray(decodeToPoints(rgb), ndiPixelX, ndiPixelY);
        Point[][] rotated = new Point[ndiPixelX][ndiPixelY];
        for (int i = 0; i < ndiPixelY; i++) {
            for (int j = 0; j < ndiPixelX; j++) {
                rotated[j][ndiPixelY - i - 1] = notRotated[i][j];
            }
        }
        return encodePointToByteArray(encodPointTo1DArray(rotated, rgb.length), rgb.length);
    }

    public static byte[] rotate180(byte[] rgb, int ndiPixelX, int ndiPixelY) {
        System.out.println("not implemented");
        return rgb;
    }

    public static byte[] rotate270(byte[] rgb, int ndiPixelX, int ndiPixelY) {
        Point[][] notRotated = decodePointTo2DArray(decodeToPoints(rgb), ndiPixelX, ndiPixelY);
        Point[][] rotated = new Point[ndiPixelX][ndiPixelY];
        for (int i = 0; i < ndiPixelY; i++) {
            for (int j = 0; j < ndiPixelX; j++) {
                rotated[ndiPixelX - j - 1][i] = notRotated[i][j];
            }
        }
        return encodePointToByteArray(encodPointTo1DArray(rotated, rgb.length), rgb.length);
    }

    private static Point[] decodeToPoints(byte[] rgb) {
        Point[] rgbPoint = new Point[rgb.length / 3];
        int a = 0;
        for (int i = 0; i < rgb.length / 3; i++) {
            rgbPoint[i] = new Point(rgb[a], rgb[a + 1], rgb[a + 2]);
            a = a + 3;
        }
        return rgbPoint;
    }

    private static Point[][] decodePointTo2DArray(Point[] rgbPoint, int ndiPixelX, int ndiPixelY) {
        Point[][] notRotated = new Point[ndiPixelY][ndiPixelX];
        int rgbArrayPositionCounter = 0;
        for (int i = 0; i < notRotated.length; i++) {
            for (int j = 0; j < notRotated[i].length; j++) {
                notRotated[i][j] = rgbPoint[rgbArrayPositionCounter];
                rgbArrayPositionCounter++;
            }
        }
        return notRotated;
    }

    private static Point[] encodPointTo1DArray(Point[][] rotated, int inputByteArrayLength) {
        int rgbArrayPositionCounter = 0;
        Point[] rgbPoint = new Point[inputByteArrayLength / 3];
        for (int i = 0; i < rotated.length; i++) {
            for (int j = 0; j < rotated[i].length; j++) {
                rgbPoint[rgbArrayPositionCounter] = rotated[i][j];
                rgbArrayPositionCounter++;
            }
        }
        return rgbPoint;
    }

    private static byte[] encodePointToByteArray(Point[] rgbPoint, int inputByteArrayLength) {
        int a = 0;
        byte[] rgb = new byte[inputByteArrayLength];
        for (int i = 0; i < rgb.length; i = i + 3) {
            rgb[i] = rgbPoint[a].getR();
            rgb[i + 1] = rgbPoint[a].getG();
            rgb[i + 2] = rgbPoint[a].getB();
            a++;
        }
        return rgb;
    }
}
