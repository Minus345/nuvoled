package com.nuvoled;

import com.nuvoled.sender.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

public class Main {

    private static String broadcastIpAddress;
    private static int port;
    private static byte courantFrame;
    private static int panelSizeX;
    private static int panelSizeY;
    private static String[] pictureConfiguration;
    private static int rotation;

    public static void main(String[] args) throws IOException, AWTException, InterruptedException {
        for (String arg : args) {
            System.out.println("Parameter: " + arg);
        }

        if (!Objects.equals(args[0], "start")) {
            System.out.println("Falsches Argumt");
            return;
        }
        if (args.length < 10) {
            System.out.println("Fehlende argumente");
            System.out.println("java -jar nuvoled.jar start [ip] [Pannal x] [Pannel y] screen [ 90/180/270] [screen number] [x] [y] [colorMode]");
            return;
        }

        System.out.println("Start");
        courantFrame = 2; //set default values
        panelSizeX = 128;
        panelSizeY = 128;
        port = 2000;
        broadcastIpAddress = args[1];

        rotation = Integer.parseInt(args[5]);

        pictureConfiguration = new String[]{args[5],args[6], args[7],args[8],args[9]};

        panelSizeX = Integer.parseInt(args[2]) * panelSizeX; //Anzahl Panel X * 128 pixel
        panelSizeY = Integer.parseInt(args[3]) * panelSizeY; //Anzahl Panel Y * 128 pixel

        System.out.println("x/y " + panelSizeX + "/" + panelSizeY);
        System.out.println("rotation " + rotationDegree());

        switch (args[4]) {
            case "color" -> colorMode(args);
            case "picture" -> pictureMode();
            case "screen", "video" -> screenAndVideo(pictureConfiguration);
        }
    }

    public static void colorMode(String[] args) throws InterruptedException {
        System.out.println("Color mode");
        int red = Integer.parseInt(args[5]);
        int green = Integer.parseInt(args[6]);
        int blue = Integer.parseInt(args[7]);
        byte redToByte = (byte) red;
        byte greenToByte = (byte) green;
        byte blueToByte = (byte) blue;

        SendColor.send(redToByte, greenToByte, blueToByte);
        //DatagramSocket datagramSocket = null;
        //datagramSocket = new DatagramSocket();
        SendSync.sendSyncro((byte) (Main.getCourantFrame() - 1));
        Thread.sleep(10);
        SendSync.sendSyncro(Main.getCourantFrame());

    }

    public static void pictureMode() throws IOException, InterruptedException {
        System.out.println("Picture mode");
        System.out.println("Enter Path / == \\");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String Path = reader.readLine();
        System.out.println(Path);
        BufferedImage image = ImageIO.read(new File(Path));
        System.out.println("Height: " + image.getHeight());
        System.out.println("Width: " + image.getWidth());

        if (SendSync.setDatagramSocket()) {

            //DatagramSocket datagramSocket = new DatagramSocket();
            PictureSender.send(image);

            SendSync.sendSyncro((byte) (Main.getCourantFrame() - 1));
            Thread.sleep(10);
            SendSync.sendSyncro(Main.getCourantFrame());

        }


    }

    public static void screenAndVideo(String[] pictureConfiguration) throws AWTException {
        GraphicsDevice[] screens = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
        int screenNumber = Integer.parseInt(pictureConfiguration[1]);
        Robot robot = new Robot(screens[screenNumber]);
        Rectangle rectangle = new Rectangle();
        rotation = Integer.parseInt(pictureConfiguration[0]);
        Rectangle screenBounds = screens[screenNumber].getDefaultConfiguration().getBounds();
        int x = Integer.parseInt(pictureConfiguration[2]) + screenBounds.x;
        int y = Integer.parseInt(pictureConfiguration[3]) + screenBounds.y;
        int colorMode = Integer.parseInt(pictureConfiguration[4]);
        System.out.println("color (10/rgb 20/jpg): " + colorMode);
        rectangle.setLocation(x, y);
        //+1 -> Fehler Zähler
        rectangle.setSize(panelSizeX , panelSizeY );

        if (!SendSync.setDatagramSocket()) {
            return;
        }

        if (Objects.equals(pictureConfiguration[4], "screen")) {
            System.out.println("Screen mode");
            PictureSender.setScreenMode(true, colorMode);

        }
        if (Objects.equals(pictureConfiguration[4], "video")) {
            System.out.println("Video mode");
            PictureSender.setScreenMode(false, colorMode);
        }

        while (true) {
            BufferedImage image = robot.createScreenCapture(rectangle);
            PictureSender.send(image);
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

    public static int rotationDegree() {
        return rotation;
    }
}
