package com.nuvoled.util;

import com.nuvoled.Main;

public class Fps {
    private static long time = 0;
    private static int i = 0;

    public static void fpsStart() {
        if (Main.isShowFps()) {
            i++;
            time = System.currentTimeMillis();
        }
    }

    public static void fpsEnd(){
        if (Main.isShowFps()) {
            float fps = (float) 1 / ((float) (System.currentTimeMillis() - time) / 1000);

            if (i == 100) {
                System.out.println("fps: " + fps);
                i = 0;
            }
        }
    }
}
