package com.nuvoled.util.rotation;

public class Rotation {

    /**
     * rotates the given picture (rgb Array) by rotation degrees
     *
     * @param rgb      input array
     * @param rotation rotation degree (90, 180, 270)
     * @return rotated picture as rgb array
     */
    public static byte[] rotateRgbData(byte[] rgb, int rotation, int globalPixelInX, int globalPixelInY) {
        switch (rotation) {
            case 90 -> rgb = rotate90(rgb, globalPixelInX, globalPixelInY);
            case 180 -> //noinspection DataFlowIssue -> not imlemented
                    rgb = rotate180(rgb, globalPixelInX, globalPixelInY);
            case 270 -> rgb = rotate270(rgb, globalPixelInX, globalPixelInY);
            default -> {
                System.out.println("Error: " + rotation + " degrees of rotation is not supported");
                System.exit(1);
            }
        }
        return rgb;
    }

    private static byte[] rotate90(byte[] rgb, int ndiPixelX, int ndiPixelY) {
        Point[][] notRotated = decodePointTo2DArray(decodeToPoints(rgb), ndiPixelX, ndiPixelY);
        Point[][] rotated = new Point[ndiPixelX][ndiPixelY];
        for (int i = 0; i < ndiPixelY; i++) {
            for (int j = 0; j < ndiPixelX; j++) {
                rotated[j][ndiPixelY - i - 1] = notRotated[i][j];
            }
        }
        return encodePointToByteArray(encodPointTo1DArray(rotated, rgb.length), rgb.length);
    }

    private static byte[] rotate180(byte[] rgb, int ndiPixelX, int ndiPixelY) {
        System.out.println("not implemented");
        return rgb;
    }

    private static byte[] rotate270(byte[] rgb, int ndiPixelX, int ndiPixelY) {
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
