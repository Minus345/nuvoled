package com.nuvoled.ndi;

import me.walkerknapp.devolay.*;

import java.nio.ByteBuffer;

public class Ndi {

    private static DevolayReceiver receiver;
    private static DevolayFinder finder;

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

                System.out.println(videoFrame1.getXResolution() + " / " + videoFrame1.getYResolution());

                byte[] a = new byte[4];
                byteBuffer.get(a, 0, 4);

            }
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
