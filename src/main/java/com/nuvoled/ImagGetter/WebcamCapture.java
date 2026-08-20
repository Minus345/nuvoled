package com.nuvoled.ImagGetter;

import com.github.sarxos.webcam.Webcam;

import java.awt.image.BufferedImage;
import java.util.List;

public class WebcamCapture implements ImageGetter {
    private final Webcam webcam;

    public WebcamCapture(int webcamIndex) {
        if (webcamIndex < 0) {
            throw new IllegalArgumentException("webcamindex should be >= 0");
        }
        List<Webcam> webcams = Webcam.getWebcams();
        if (webcams.isEmpty()) {
            throw new IllegalStateException("No Webcams found");
        }
        int i = 0;
        for (Webcam w : webcams) {
            System.out.println("[" + i + "] " + w.getName());
            i++;
        }
        webcam = webcams.get(webcamIndex);
        webcam.open();
    }

    public BufferedImage getImage() {
        return webcam.getImage();
    }
}
