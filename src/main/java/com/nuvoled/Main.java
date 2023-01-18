package com.nuvoled;

import com.nuvoled.sender.*;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

public class Main {


    private static int port;
    private static byte courantFrame;
    //Args
    private static String broadcastIpAddress;
    private static int panelSizeX;
    private static int panelSizeY;
    private static String mode;
    private static boolean bindToInterface;
    private static Float scaleFactor;
    private static Float offSet;
    private static Integer[] pictureConfiguration;
    private static int rotation;

    public static void main(String[] args) throws IOException, AWTException, InterruptedException {

        System.out.println("Nuvoled Presenter");
        scaleFactor = Float.parseFloat("1.0");
        offSet = Float.parseFloat("0");
        //set default values

        courantFrame = 2;
        int onepanelSizeX = 128;
        int onepanelSizeY = 128;
        port = 2000;
        bindToInterface = false;

        int xPanelCount = 1;
        int yPanelCount = 1;

        broadcastIpAddress = "169.254.255.255";
        mode = "video";
        rotation = 0;
        int screenNumber = 0;
        int xPosition = 0;
        int yPosition = 0;
        int colorMode = 10;

        var options = new Options()
                .addOption("v", "verbose", false, "Verbose")
                .addOption("b", "bind", false, "bind to interface 169.254")
                .addOption(Option.builder("d")
                        .longOpt("delimiter")
                        .hasArg(true)
                        .desc("The delimiter to use")
                        .argName("delimiter")
                        .build());

        System.out.println(options.getOption("b").toString());
        System.out.println(options.hasOption("b"));
        /*

        if (args.length < 10) {
            System.out.println("Fehlende argumente");
            System.out.println("java -jar nuvoled.jar start [ip] [Pannal x] [Pannel y] screen [ 90/180/270] [screen number] [x] [y] [colorMode] [bind to interface true/false] [brightness] [offset]");
            return;
        }

         */

        pictureConfiguration = new Integer[]{rotation, screenNumber,
                xPosition, yPosition, colorMode};

        panelSizeX = xPanelCount * onepanelSizeX; //Anzahl Panel X * 128 pixel
        panelSizeY = yPanelCount * onepanelSizeY; //Anzahl Panel Y * 128 pixel

        System.out.println("x/y Panel Count          : " + xPanelCount + "/" + yPanelCount);
        System.out.println("x/y Panle Size           : " + onepanelSizeX + "/" + onepanelSizeY);
        System.out.println("x/y Pixels               : " + panelSizeX + "/" + panelSizeY);
        System.out.println("rotation Degree          : " + rotationDegree());
        System.out.println("Screen Number            : " + screenNumber);
        System.out.println("x/y Start Position       : " + xPosition + "/" + yPosition);
        System.out.println("broadcastIpAddress       : " + broadcastIpAddress);
        System.out.println("bind to interface        : " + bindToInterface);
        System.out.println("scaleFactor (Brightness) : " + scaleFactor.toString());
        System.out.println("offset (Contrast)        : " + offSet.toString());
        System.out.println("color (10/rgb 20/jpg)    : " + colorMode);

        switch (mode) {
            case "picture" -> pictureMode();
            case "screen", "video" -> screenAndVideo(pictureConfiguration);
        }
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

    public static void screenAndVideo(Integer[] pictureConfiguration) throws AWTException {
        GraphicsDevice[] screens = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
        int screenNumber = pictureConfiguration[1];
        Robot robot = new Robot(screens[screenNumber]);
        Rectangle rectangle = new Rectangle();
        rotation = pictureConfiguration[0];
        Rectangle screenBounds = screens[screenNumber].getDefaultConfiguration().getBounds();
        int x = pictureConfiguration[2] + screenBounds.x;
        int y = pictureConfiguration[3] + screenBounds.y;
        int colorMode = pictureConfiguration[4];
        rectangle.setLocation(x, y);
        //+1 -> Fehler Zähler
        rectangle.setSize(panelSizeX, panelSizeY);

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

    public static boolean getBindToInterface() {
        return bindToInterface;
    }

    public static Float getScaleFactor() {
        return scaleFactor;
    }

    public static Float getOffset() {
        return offSet;
    }
}
