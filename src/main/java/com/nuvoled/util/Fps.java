package com.nuvoled.util;

public class Fps {
    private static long time = 0;
    private static int i = 0;

    public static void fpsStart(boolean enabled) {
        if (enabled) {
            i++;
            time = System.currentTimeMillis();
        }
    }

    public static void fpsEnd(boolean enabled){
        if (enabled) {
            float fps = (float) 1 / ((float) (System.currentTimeMillis() - time) / 1000);

            if (i == 100) {
                System.out.println("fps: " + fps);
                i = 0;
            }
        }
    }
}
