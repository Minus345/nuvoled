package com.nuvoled.ndi;

import com.nuvoled.Main;
import me.walkerknapp.devolay.*;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class Ndi {

    private static DevolayReceiver receiver;
    private static DevolayFinder finder;
    private static int pixelX;
    private static int pixelY;
    public static byte[] rgb = new byte[Main.getPanelSizeX() * Main.getPanelSizeY() * 3];

    public static void ndi() throws InterruptedException {
        System.out.println("----NDI----");
        Devolay.loadLibraries();
        receiver = new DevolayReceiver();
        finder = new DevolayFinder();

        System.out.println("Starting");
        findSources();
        getVideo();
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

            // Query the updated list of sources
            sources = finder.getCurrentSources();
            System.out.println("Network sources (" + sources.length + " found).");
            for (int i = 0; i < sources.length; i++) {
                System.out.println((i + 1) + ". " + sources[i].getSourceName());
            }
        }
        receiver.connect(sources[0]);
    }

    private static void getVideo() throws InterruptedException {
        DevolayVideoFrame videoFrame = new DevolayVideoFrame();
        DevolayFrameSync frameSync = new DevolayFrameSync(receiver);

        // Run at 30Hz
        final float clockSpeed = 30;

        while (true) {

            // Capture a video frame
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

                //NDI Source muss doppelt so viel Pixel horizontal und vertikal haben wie Panel Source
                // https://docs.ndi.video/all/using-ndi/ndi-for-video/digital-video-basics

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
                    int[] pixel = YUV2RGB.toRGB(frameBuffer[i], frameBuffer[i + 1], frameBuffer[i + 2]);
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
                System.out.println(Arrays.toString(rgb));
                System.out.println(rgb[rgb.length - 1]);


                // Here is the clock. The frame-sync is smart enough to adapt the video and audio to match 30Hz with this.
                Thread.sleep((long) (1000 / clockSpeed));
            }

            // Destroy the references to each. Not necessary, but can free up the memory faster than Java's GC by itself
            //videoFrame.close();
            //audioFrame.close();
            // Make sure to close the framesync before the receiver
            //frameSync.close();
            //receiver.close();
        }

    }
}
