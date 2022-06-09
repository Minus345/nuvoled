package com.nuvoled;

import com.nuvoled.sender.PictureSender;
import com.nuvoled.sender.SendColor;
import com.nuvoled.sender.SendSync;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

public class Main {

    private static String addr;
    private static byte courantFrame;

    public static void main(String[] args) throws IOException, AWTException {
        for (String arg : args) {
            System.out.println("Parameter: " + arg);
        }

        if (Objects.equals(args[0], "start")) {
            System.out.println("Start");
            courantFrame = 1;
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
                    System.out.println("Picture Modus");
                    System.out.println("Enter Path / == \\");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                    String Path = reader.readLine();
                    System.out.println(Path);
                    BufferedImage image = ImageIO.read(new File(Path));
                    System.out.println("Height: " + image.getHeight());
                    System.out.println("Width: " + image.getWidth());
                    PictureSender.send(image);
                    SendSync.send();
                }
                if (Objects.equals(args[2], "screen")){
                    System.out.println("Screen Modus");
                    Robot robot = new Robot();
                    Rectangle rectangle = new Rectangle();
                    rectangle.setSize(129,129);
                    while (true){
                        BufferedImage image =  robot.createScreenCapture(rectangle);
                        PictureSender.send(image);
                        SendSync.send();
                    }

                }
            }
        }
    }

    public static String getAddr() {
        return addr;
    }

    public static byte getCourantFrame() {
        return courantFrame;
    }

    public static void setCourantFrame(byte courantFrame) {
        Main.courantFrame = courantFrame;
    }
}
