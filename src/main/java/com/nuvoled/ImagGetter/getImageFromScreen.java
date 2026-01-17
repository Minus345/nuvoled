package com.nuvoled.ImagGetter;

import java.awt.*;
import java.awt.image.BufferedImage;

public class getImageFromScreen implements ImageGetter {

    private final Robot robot;
    private final Rectangle rectangle;

    public getImageFromScreen(int globalPixelInX, int globalPixelInY, int xPosition, int yPosition, int screenNumber) {
        GraphicsDevice[] screens = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
        try {
            robot = new Robot(screens[screenNumber]);
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
        rectangle = new Rectangle();
        Rectangle screenBounds = screens[screenNumber].getDefaultConfiguration().getBounds();
        int x = xPosition + screenBounds.x;
        int y = yPosition + screenBounds.y;
        rectangle.setLocation(x, y);
        rectangle.setSize(globalPixelInX, globalPixelInY);
    }

    public BufferedImage getImage() {
        return robot.createScreenCapture(rectangle);
    }
}
