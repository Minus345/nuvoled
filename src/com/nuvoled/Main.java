package com.nuvoled;

import com.nuvoled.sender.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramSocket;
import java.util.Objects;

public class Main {

    private static String broadcastIpAddress;
    private static int port;
    private static byte courantFrame;
    private static int panelSizeX;
    private static int panelSizeY;
    private static boolean rotation;

    public static void main(String[] args) throws IOException, AWTException, InterruptedException {
        for (String arg : args) {
            System.out.println("Parameter: " + arg);
        }

        if (Objects.equals(args[0], "start")) {
            System.out.println("Start");
            courantFrame = 2;
            panelSizeX = 128 * 2;
            panelSizeY = 128;
            port = 2000;
            broadcastIpAddress = args[1];
            int numberPanelX = Integer.parseInt(args[2]);
            int numberPanelY = Integer.parseInt(args[3]);
            panelSizeX = numberPanelX * 128;
            panelSizeY = numberPanelY * 128;
            rotation = false;
            //Reset.send(2000);
            //Thread.sleep(1000);
            //Receiver.run();
            //Thread.sleep(1000);
            System.out.println("Ready");
            if (args.length > 4) {
                if (Objects.equals(args[4], "color")) {
                    System.out.println("Color mode");
                    int red = Integer.parseInt(args[5]);
                    int green = Integer.parseInt(args[6]);
                    int blue = Integer.parseInt(args[7]);
                    byte redToByte = (byte) red;
                    byte greenToByte = (byte) green;
                    byte blueToByte = (byte) blue;

                    SendColor.send(redToByte, greenToByte, blueToByte);
                    SendSync.send((byte) (Main.getCourantFrame() - 1));
                    Thread.sleep(10);
                    SendSync.send(Main.getCourantFrame());
                }
                if (Objects.equals(args[4], "picture")) {
                    System.out.println("Picture mode");
                    System.out.println("Enter Path / == \\");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                    String Path = reader.readLine();
                    System.out.println(Path);
                    BufferedImage image = ImageIO.read(new File(Path));
                    System.out.println("Height: " + image.getHeight());
                    System.out.println("Width: " + image.getWidth());

                    DatagramSocket datagramSocket = new DatagramSocket();
                    PictureSender.send(image, datagramSocket);

                    SendSync.send((byte) (Main.getCourantFrame() - 1));
                    Thread.sleep(10);
                    SendSync.send(Main.getCourantFrame());
                }
                if (Objects.equals(args[4], "screen") || Objects.equals(args[4], "video")) {
                    GraphicsDevice[] screens = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
                    int screenNumber = Integer.parseInt(args[6]);
                    Robot robot = new Robot(screens[screenNumber]);
                    Rectangle rectangle = new Rectangle();
                    rotation = Boolean.parseBoolean(args[5]);
                    Rectangle screenBounds = screens[screenNumber].getDefaultConfiguration().getBounds();
                    int x = Integer.parseInt(args[7]) + screenBounds.x;
                    int y = Integer.parseInt(args[8]) + screenBounds.y;
                    rectangle.setLocation(x, y);
                    rectangle.setSize(panelSizeX + 1, panelSizeY + 1);

                    DatagramSocket datagramSocket = new DatagramSocket();

                    if (Objects.equals(args[4], "screen")) {
                        System.out.println("Screen mode");
                        while (true) {
                            BufferedImage image = robot.createScreenCapture(rectangle);
                            PictureSender.send(image, datagramSocket);
                        }
                    }
                    if (Objects.equals(args[4], "video")) {
                        System.out.println("Video mode");
                        while (true) {
                            BufferedImage image = robot.createScreenCapture(rectangle);
                            VideoSender.send(image, datagramSocket);
                        }
                    }
                }
            }
        }
    }

    public static String getBroadcastIpAddress() {
        return broadcastIpAddress;
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

    public static int getPanelSizeX() {
        return panelSizeX;
    }

    public static int getPanelSizeY() {
        return panelSizeY;
    }

    public static boolean isRotation() {
        return rotation;
    }
}
