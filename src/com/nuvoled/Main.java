package com.nuvoled;

import com.nuvoled.sender.SendColor;
import com.nuvoled.sender.SendSync;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class Main {

    private static String addr;
    private static byte curantFrame;

    public static void main(String[] args) throws IOException {
        for (String arg : args) {
            System.out.println("Parameter: " + arg);
        }

        if (Objects.equals(args[0], "start")) {
            System.out.println("Start");
            curantFrame = 1;
            addr = args[1];
            //new Receiver().run(2000);
            System.out.println("Ready");
            if (args.length > 2) {
                if (Objects.equals(args[2], "color")) {
                    System.out.println("Color modus");
                    int red = Integer.parseInt(args[3]);
                    int green = Integer.parseInt(args[4]);
                    int blue = Integer.parseInt(args[5]);
                    byte redToByte = (byte) red;
                    byte greenToByte = (byte) green;
                    byte blueToByte = (byte) blue;

                    SendColor.send(redToByte, greenToByte, blueToByte);
                    SendSync.send();
                }
                if (Objects.equals(args[2], "picture")) {
                    BufferedImage image = ImageIO.read(new File("C:\\Users\\Tina\\Documents\\green.jpg"));
                    System.out.println("Height: " + image.getHeight());
                    System.out.println("Width: " + image.getWidth());
                    if (image.getHeight() < 128 || image.getWidth() < 128) {
                        System.out.println("Falsches Format");
                        System.out.println("Bitte Format von mindestens 128 * 128 Pixeln verwenden");
                        System.exit(0);
                    }

                    byte[] rgb  = new byte[49152];// 128*128*3
                    int i = 0;
                    for (int y = 0; y <= 128; y++) {
                        for (int x = 0; x <= 128; x++) {
                            int pixel = image.getRGB(x, y);
                            int red = (pixel >> 16) & 0xff;
                            int green = (pixel >> 8) & 0xff;
                            int blue = (pixel) & 0xff;
                            System.out.println("x: " + x + " " + "y: " + y + " | " + "red: " + red + ", green: " + green + ", blue: " + blue);
                            rgb[i] = (byte) red;
                            i++;
                            rgb[i] = (byte) green;
                            i++;
                            rgb[i] = (byte) blue;
                            i++;
                        }
                    }
                }
            }
        }
    }

    public static String getAddr() {
        return addr;
    }

    public static byte getCurantFrame() {
        return curantFrame;
    }

    public static void setCurantFrame(byte curantFrame) {
        Main.curantFrame = curantFrame;
    }
}
