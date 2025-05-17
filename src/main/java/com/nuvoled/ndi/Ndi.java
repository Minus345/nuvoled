package com.nuvoled.ndi;

import com.nuvoled.Main;
import com.nuvoled.sender.SendSync;
import me.walkerknapp.devolay.*;

import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.Scanner;

public class Ndi {

    private static DevolayReceiver receiver;
    private static DevolayFinder finder;
    private static int ndiPixelX;
    private static int ndiPixelY;
    private static byte[] rgb = new byte[Main.getPanelSizeX() * Main.getPanelSizeY() * 3];
    private static DevolayVideoFrame videoFrame;
    private static DevolayFrameSync frameSync;

    public static void ndi() throws InterruptedException {
        System.out.println("----NDI----");
        Devolay.loadLibraries();
        receiver = new DevolayReceiver();
        finder = new DevolayFinder();

        System.out.println("Starting");
        findSources();

        System.out.println("Connect to DatagrammSocket");
        if (!SendSync.setDatagramSocket()) {
            System.out.println("Internal Error: No DatagrammSocket connected");
            System.exit(0);
        }

        System.out.println("Setting up NDI");
        videoFrame = new DevolayVideoFrame();
        frameSync = new DevolayFrameSync(receiver);

        System.out.println("Running");
        while (true) {
            sendNDI();
        }
    }

    private static void findSources() {
        // Run for one minute
        DevolaySource[] sources = null;
        long startTime = System.currentTimeMillis();
        int sourceNumber = 0;

        final boolean useFirstOne = true; //TODO: make it changeable within cli

        while (System.currentTimeMillis() - startTime < 1000 * 60) {
            // Query with a timeout of 5 seconds
            if (!finder.waitForSources(5000)) {
                // If no new sources were found
                System.out.println("No change to the sources list found.");

                if (sources == null) {
                    System.out.println("No Sources Found");
                    System.exit(0);
                }

                //If useFirstOne is enabled; to first source found the programm connects
                if (useFirstOne) {
                    sourceNumber = 1;
                    break;
                }

                //User Input to select the right source
                System.out.println("Enter Source Number:");
                Scanner scanner = new Scanner(System.in);
                sourceNumber = scanner.nextInt();

                //user error catching:
                if (sourceNumber < 0 || sourceNumber > Objects.requireNonNull(sources).length) {
                    System.out.println("Wrong Input. Pleas enter a Number within range");
                    System.exit(102);
                }
                System.out.println("Connecting to: " + sourceNumber + ". | " + sources[sourceNumber - 1].getSourceName());
                break;
            }

            // Query the updated list of sources
            sources = finder.getCurrentSources();
            System.out.println("Network sources (" + sources.length + " found).");
            for (int i = 0; i < sources.length; i++) {
                System.out.println((i + 1) + ". " + sources[i].getSourceName());
            }
        }
        assert sources != null;
        receiver.connect(sources[sourceNumber - 1]);
    }

    private static void getVideoData_4_2_2_subsampling() throws InterruptedException {
        // Run at 30Hz
        final float clockSpeed = 30;

        if (frameSync.captureVideo(videoFrame)) { // Only returns true if a video frame was returned
            DevolayVideoFrame videoFrame1 = videoFrame;
            ByteBuffer byteBuffer = videoFrame1.getData();

            //Get NDI Meta Data
            ndiPixelX = videoFrame1.getXResolution();
            ndiPixelY = videoFrame1.getYResolution();

            //Compare if Incoming NDI Stream is the same resolution as the configured panel Count
            int panelXY = Main.getPanelSizeX() * Main.getPanelSizeY();
            int ndiPixelCount = ndiPixelX * ndiPixelY;

            int ndiPixelBufferLength = ndiPixelCount * 4 / 2; //4: Wegen YUV → Eigentlich ja nur mal 2 weil pro pixel 2 bytes
            int bufferLength = byteBuffer.limit();

            if (ndiPixelCount != panelXY || ndiPixelBufferLength != bufferLength) { //|| Main.getPanelSizeX() != pixelX || Main.getPanelSizeY() != pixelY
                System.out.println("Pixel count of NDI Source and configured panels are not the Same ");
                System.out.println("Configured Pixel: " + "x: " + Main.getPanelSizeX() + " y: " + Main.getPanelSizeY() + " | Form NDI Source: " + "x: " + ndiPixelX + " y: " + ndiPixelY);
                System.exit(0);
            }

            System.out.println("Configured Pixel: " + "x: " + Main.getPanelSizeX() + " y: " + Main.getPanelSizeY() + " | Form NDI Source: " + "x: " + ndiPixelX + " y: " + ndiPixelY);

            //Get the hole frame in one array:
            byte[] ndiFrameBuffer = new byte[ndiPixelBufferLength];
            byteBuffer.get(ndiFrameBuffer, 0, ndiPixelBufferLength);

            //TODO: Rotation
            //TODO: brightness

            int rgbCounterNumber = 0;

            //ndi Data -> RGB
            for (int i = 0; i <= ndiPixelBufferLength - 1; i = i + 4) {
                /*

                Y: Intensity
                U + V: Colour

                Pixel in FrameBuffer:
                The ordering of these pixels is U0, Y0, V0, Y1.

                                Y | U | V

                  Pixel 1:      Y0 | U0 | V0
                  Pixel 2:      Y1 | U0 | V0

                                U0 | Y0 | V0 | Y1
                                0  | 1  | 2 | 3
                 */
                //First Pixel
                int[] pixel1 = YUV2RGB.toRGB(ndiFrameBuffer[i + 1], ndiFrameBuffer[i], ndiFrameBuffer[i + 2]);
                int red1 = pixel1[0] & 0xff;
                int green1 = pixel1[1] & 0xff;
                int blue1 = pixel1[2] & 0xff;

                rgb[rgbCounterNumber] = (byte) blue1;
                rgbCounterNumber++;
                rgb[rgbCounterNumber] = (byte) green1;
                rgbCounterNumber++;
                rgb[rgbCounterNumber] = (byte) red1;
                rgbCounterNumber++;

                //Second Pixel
                int[] pixel2 = YUV2RGB.toRGB(ndiFrameBuffer[i + 3], ndiFrameBuffer[i], ndiFrameBuffer[i + 2]);
                int red2 = pixel2[0] & 0xff;
                int green2 = pixel2[1] & 0xff;
                int blue2 = pixel2[2] & 0xff;

                rgb[rgbCounterNumber] = (byte) blue2;
                rgbCounterNumber++;
                rgb[rgbCounterNumber] = (byte) green2;
                rgbCounterNumber++;
                rgb[rgbCounterNumber] = (byte) red2;
                rgbCounterNumber++;

            }

            //Rotation 90 Degree:
            //conversion into Points
            Point[] rgbPoint = new Point[rgb.length / 3];
            int a = 0;
            for (int i = 0; i < rgb.length / 3; i++) {
                rgbPoint[i] = new Point(rgb[a], rgb[a + 1], rgb[a + 2]);
                a = a + 3;
            }

            //conversion into 2D Array
            Point[][] rotated = new Point[ndiPixelX][ndiPixelY];
            int rgbArrayPositionCounter = 0;
            for (int i = 0; i < rotated.length; i++) {
                for (int j = 0; j < rotated[i].length; j++) {
                    rotated[i][j] = rgbPoint[rgbArrayPositionCounter];
                    rgbArrayPositionCounter++;
                }
            }

            /*
            //Rotation of the array
            int n = mat.length;

            // Initialize the result matrix with zeros
            Point[][] res = new Point[n][n];

            // Flip the matrix clockwise using nested loops
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    res[j][n - i - 1] = mat[i][j];
                }
            }

            // Copy result back to mat
            for (int i = 0; i < n; i++) {
                System.arraycopy(res[i], 0, mat[i], 0, n);
            }


             */
            //conversion to 1D Array
            rgbArrayPositionCounter = 0;
            for (int i = 0; i < rotated.length; i++) {
                for (int j = 0; j < rotated[i].length; j++) {
                    rgbPoint[rgbArrayPositionCounter] = rotated[i][j];
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

            //System.out.println(" Y: " + ((int) ndiFrameBuffer[1] & 0xff) + " U: " + (ndiFrameBuffer[0] & 0xff) + " V: " + (ndiFrameBuffer[2] & 0xff));
            //System.out.println(" R: " + rgb[2] + " G: " + rgb[1] + " B: " + rgb[0]);

            // Here is the clock. The frame-sync is smart enough to adapt the video and audio to match 30Hz with this.
            Thread.sleep((long) (1000 / clockSpeed));
        }
    }

    private static void sendNDI() throws InterruptedException {
        //Thread.sleep(1000);
        //System.out.println("NEW FRAME");
        //getVideoDataOnlyEverySecondPixel();
        getVideoData_4_2_2_subsampling();
        //artNetCheck();

        int pixel = 0;
        int MaxPackets;

        //TODO: RGB565

        //TODO: Detecting if Stream isn´t working anymore
 /*
            if (color_mode == 30) {
                MaxPackets = ((Main.getPanelSizeX() * Main.getPanelSizeY() * 2) / 1440) + 1; //rgb -> 3 rgb565 -> 2
            } else {

            */

        MaxPackets = ((Main.getPanelSizeX() * Main.getPanelSizeY() * 3) / 1440) + 1; //rgb -> 3 rgb565 -> 2
        //    }


        for (int counter = 0; counter <= MaxPackets; counter++) {
            byte[] message = new byte[1450];
            message[0] = 36;
            message[1] = 36;
            message[2] = 20;
            message[3] = Main.getCourantFrame();
            message[4] = (byte) (10); //RGB -> 10 JPG -> 20 RGB565 -> 30
            message[5] = (byte) (counter >> 8);
            message[6] = (byte) (counter & 255);
            message[7] = (byte) (MaxPackets >> 8);
            message[8] = (byte) (MaxPackets & 255);
            message[9] = 45;

            for (int i = 1; i < 1440; i = i + 3) {
                if (pixel >= rgb.length) {
                    //setzt die letzten bytes des Packsets auf 0
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
            SendSync.send_data(message);
            //System.out.println(Arrays.toString(message));
            //System.out.println(message[10] + " " + message[11] + " " + message[12]);
        }
        try {
            Thread.sleep(Main.getSleep());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        SendSync.send_end_frame();
    }
}
