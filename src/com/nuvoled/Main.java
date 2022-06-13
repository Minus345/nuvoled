package com.nuvoled;

import com.nuvoled.sender.PictureSender;
import com.nuvoled.sender.Reset;
import com.nuvoled.sender.SendColor;
import com.nuvoled.sender.SendSync;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.StandardSocketOptions;
import java.nio.channels.DatagramChannel;
import java.util.Objects;

public class Main {

    private static String addr;
    private static int port;
    private static byte courantFrame;
    private static int pannelGroessex;
    private static int pannelGroessey;

    public static void main(String[] args) throws IOException, AWTException, InterruptedException {
        for (String arg : args) {
            System.out.println("Parameter: " + arg);
        }

        if (Objects.equals(args[0], "start")) {
            System.out.println("Start");
            courantFrame = 2;
            pannelGroessex = 256;
            pannelGroessey = 128;
            port = 2000;
            addr = args[1];
            //Reset.send(2000);
            //Thread.sleep(1000);
            //Receiver.run();
            //Thread.sleep(1000);
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
                    SendSync.send((byte) (Main.getCourantFrame() - 1));
                    Thread.sleep(10);
                    SendSync.send(Main.getCourantFrame());
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
                    SendSync.send((byte) (Main.getCourantFrame() - 1));
                    Thread.sleep(10);
                    SendSync.send(Main.getCourantFrame());
                }
                if (Objects.equals(args[2], "screen")) {
                    System.out.println("Screen Modus");
                    GraphicsDevice[] screens = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();  //same screens[] with JRE7 and JRE8
                    Robot robot = new Robot(screens[0]);
                    Rectangle rectangle = new Rectangle();
                    int x = Integer.parseInt(args[3]);
                    int y = Integer.parseInt(args[4]);
                    rectangle.setLocation(x, y);
                    rectangle.setSize(pannelGroessex + 1, pannelGroessey + 1);
                    SendSync.send((byte) (Main.getCourantFrame() - 1));
                    while (true) {
                        BufferedImage image = robot.createScreenCapture(rectangle);
                        PictureSender.send(image);
                        Thread.sleep(10);
                        SendSync.send((byte) (Main.getCourantFrame() - 1));
                        Thread.sleep(60);
                        SendSync.send(Main.getCourantFrame());
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

    public static int getPort() {
        return port;
    }

    public static void setPort(int port) {
        Main.port = port;
    }

    public static int getPannelGroessex() {
        return pannelGroessex;
    }

    public static void setPannelGroessex(int pannelGroessex) {
        Main.pannelGroessex = pannelGroessex;
    }

    public static int getPannelGroessey() {
        return pannelGroessey;
    }

    public static void setPannelGroessey(int pannelGroessey) {
        Main.pannelGroessey = pannelGroessey;
    }
}
