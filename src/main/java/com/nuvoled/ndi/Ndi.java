package com.nuvoled.ndi;

import com.nuvoled.Main;
import com.nuvoled.sender.SendSync;
import me.walkerknapp.devolay.*;

import java.nio.ByteBuffer;

public class Ndi {

    private static DevolayReceiver receiver;
    private static DevolayFinder finder;
    private static int pixelX;
    private static int pixelY;
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
            System.exit(101);
        }

        System.out.println("Setting up NDI");
        videoFrame = new DevolayVideoFrame();
        frameSync = new DevolayFrameSync(receiver);

        while (true) {
            sendNDI();
        }
    }

    private static void findSources() {
        // Run for one minute
        DevolaySource[] sources = null;
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < 1000 * 60) {
            // Query with a timeout of 5 seconds
            if (!finder.waitForSources(5000)) {
                // If no new sources were found
                System.out.println("No change to the sources list found.");
                break;
            }

            //ToDO: Select Sources - Command Line Input

            // Query the updated list of sources
            sources = finder.getCurrentSources();
            System.out.println("Network sources (" + sources.length + " found).");
            for (int i = 0; i < sources.length; i++) {
                System.out.println((i + 1) + ". " + sources[i].getSourceName());
            }
        }
        receiver.connect(sources[0]);
    }

    /**
     * NDI Source muss doppelt so viel Pixel horizontal und vertikal haben wie Panel Source
     * https://docs.ndi.video/all/using-ndi/ndi-for-video/digital-video-basics
     *
     * @throws InterruptedException
     */
    private static void getVideoDataOnlyEverySecondPixel() throws InterruptedException {

        // Run at 30Hz
        final float clockSpeed = 30;

        // while (true) {

        // Capture a video frame
        if (frameSync.captureVideo(videoFrame)) {
            System.out.println("frame here");
        } else {
            System.out.println("no frame");
        }


        if (frameSync.captureVideo(videoFrame)) { // Only returns true if a video frame was returned
            DevolayVideoFrame videoFrame1 = videoFrame;
            ByteBuffer byteBuffer = videoFrame1.getData();

            //TODO: put this outside of loop
            pixelX = videoFrame1.getXResolution();
            pixelY = videoFrame1.getYResolution();

            System.out.println(pixelX + " / " + pixelY);

                /*
                if (Main.getPanelSizeX() != pixelX || Main.getPanelSizeY() != pixelY) {
                    System.out.println("Pixel X and Y from NDI Source are not the same as your panel configuration");
                    System.exit(10);
                }

                 */

            //Get the hole frame in one array:
            int pannlXY = Main.getPanelSizeX() * Main.getPanelSizeY();
            int pixelCount = pixelX / 2 * pixelY / 2; // Nur jeder 2 Pixel
            int pixelBufferLength = pixelCount * 4 * 2; //4: Wegen YUV 2: Wegen wir nehmen nur jeden 2 Pixel
            byte[] frameBuffer = new byte[pixelBufferLength];
            int bufferLength = byteBuffer.limit();
            System.out.println(bufferLength);
            byteBuffer.get(frameBuffer, 0, pixelBufferLength);

            if (pixelBufferLength != bufferLength) System.exit(101);

            //TODO: Rotatoin

            int rgbCounterNumber = 0;

            for (int i = 0; i <= pixelBufferLength / 2 - 1; i = i + 4) { // /2, weil wir nur jeden zweiten pixel nehmen
                int[] pixel = YUV2RGB.toRGB(frameBuffer[i + 1], frameBuffer[i], frameBuffer[i + 2]);  //The ordering of these pixels is U0, Y0, V0, Y1.
                int red = pixel[0] & 0xff;
                int green = pixel[1] & 0xff;
                int blue = pixel[2] & 0xff;
                rgb[rgbCounterNumber] = (byte) blue;
                rgbCounterNumber++;
                rgb[rgbCounterNumber] = (byte) green;
                rgbCounterNumber++;
                rgb[rgbCounterNumber] = (byte) red;
                rgbCounterNumber++;
            }
            //System.out.println(Arrays.toString(rgb));
            System.out.println(" Y: " + frameBuffer[0] + " U: " + frameBuffer[1] + " V: " + frameBuffer[2]);
            System.out.println(" r: " + rgb[2] + " g: " + rgb[1] + "b: " + rgb[0]);


            // Here is the clock. The frame-sync is smart enough to adapt the video and audio to match 30Hz with this.
            Thread.sleep((long) (1000 / clockSpeed));
            //   }

            // Destroy the references to each. Not necessary, but can free up the memory faster than Java's GC by itself
            //videoFrame.close();
            //audioFrame.close();
            // Make sure to close the framesync before the receiver
            //frameSync.close();
            //receiver.close();
        }
    }

    private static void getVideoData_4_2_2_subsampling() throws InterruptedException {
        // Run at 30Hz
        final float clockSpeed = 30;
        // Capture a video frame
        if (frameSync.captureVideo(videoFrame)) {
            System.out.println("frame here");
        } else {
            System.out.println("no frame");
        }

        if (frameSync.captureVideo(videoFrame)) { // Only returns true if a video frame was returned
            DevolayVideoFrame videoFrame1 = videoFrame;
            ByteBuffer byteBuffer = videoFrame1.getData();

            //TODO: put this outside of loop
            pixelX = videoFrame1.getXResolution();
            pixelY = videoFrame1.getYResolution();

            System.out.println(pixelX + " / " + pixelY);
                /*
                if (Main.getPanelSizeX() != pixelX || Main.getPanelSizeY() != pixelY) {
                    System.out.println("Pixel X and Y from NDI Source are not the same as your panel configuration");
                    System.exit(10);
                }
                 */

            //Get the hole frame in one array:
            int pannlXY = Main.getPanelSizeX() * Main.getPanelSizeY();
            int pixelCount = pixelX * pixelY;

            int pixelBufferLength = pixelCount * 4 / 2; //4: Wegen YUV -> Eigentlich ja nur mal 2 weil pro pixel 2 bytes
            byte[] frameBuffer = new byte[pixelBufferLength];
            int bufferLength = byteBuffer.limit();
            System.out.println(bufferLength);
            byteBuffer.get(frameBuffer, 0, pixelBufferLength);

            if (pixelCount != pannlXY) System.exit(101);
            if (pixelBufferLength != bufferLength) System.exit(102);

            //TODO: Rotatoin

            int rgbCounterNumber = 0;

            for (int i = 0; i <= pixelBufferLength - 1; i = i + 4) {


                /*
                Pixel in FrameBuffer:
                The ordering of these pixels is U0, Y0, V0, Y1.

                                Y | U | V

                  Pixel 1:      Y0 | U0 | V0
                  Pixel 2:      Y1 | U0 | V0

                                U0 | Y0 | V0 | Y1
                                0  | 1  | 2 | 3
                 */


                //First Pixel
                int[] pixel1 = YUV2RGB.toRGB(frameBuffer[i + 1], frameBuffer[i], frameBuffer[i + 2]);
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
                int[] pixel2 = YUV2RGB.toRGB(frameBuffer[i + 3], frameBuffer[i], frameBuffer[i + 2]);
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
            //System.out.println(Arrays.toString(rgb));
            System.out.println(" Y: " + frameBuffer[0] + " U: " + frameBuffer[1] + " V: " + frameBuffer[2]);
            System.out.println(" r: " + rgb[2] + " g: " + rgb[1] + "b: " + rgb[0]);

            // Here is the clock. The frame-sync is smart enough to adapt the video and audio to match 30Hz with this.
            Thread.sleep((long) (1000 / clockSpeed));
        }
    }

    private static void sendNDI() throws InterruptedException {
        //Thread.sleep(1000);
        System.out.println("NEW FRAME");
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
                    //setzt die letzten bytes des Psackest auf 0
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
