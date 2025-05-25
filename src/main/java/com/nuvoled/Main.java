package com.nuvoled;

import ch.bildspur.artnet.ArtNetClient;
import com.nuvoled.Util.CLI;
import com.nuvoled.Util.Fps;
import com.nuvoled.ndi.Ndi;
import com.nuvoled.sender.PictureSender;
import com.nuvoled.sender.SendSync;
import org.apache.log4j.varia.NullAppender;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Main {

    private static int port = 2000;
    private static byte courantFrame = 2;
    private static String broadcastIpAddress = "169.254.255.255";
    private static int panelSizeX;
    private static int panelSizeY;
    private static String mode = "screen";
    private static boolean bindToInterface = false;
    private static Float scaleFactor = 0.6F;
    private static Float offSet = 0F;
    private static Integer[] pictureConfiguration;
    private static int rotation = 0;
    private static int sleep = 0;

    private static boolean artnetEnabled = false;
    private static boolean artnetDebug = false;
    private static ArtNetClient artnet;
    private static int subnet = 0;
    private static int universum = 0;
    private static int channel = 0;

    private static String wichPanel;
    private static int colorMode = 10;
    private static boolean showFps = false;

    private static int xPanelCount = 1;
    private static int yPanelCount = 1;
    private static int screenNumber = 0;
    private static int xPosition = 0;
    private static int yPosition = 0;

    public static void main(String[] args) throws IOException, AWTException, InterruptedException {
        System.out.println("Nuvoled Presenter");

        org.apache.log4j.BasicConfigurator.configure(new NullAppender());

        CLI.commandLineParameters(args);

        if (wichPanel == null || wichPanel.isEmpty()) {
            System.out.println("Choose Panel");
            System.out.println("Usage: -p \"P4\" / -p \"P5\"");
            System.exit(-1);
        }

        int onepanelSizeX = 0;
        int onepanelSizeY = 0;

        switch (wichPanel) {
            case "P4" -> {
                onepanelSizeX = 128;
                onepanelSizeY = 128;
                break;
            }
            case "P5" -> {
                onepanelSizeX = 128;
                onepanelSizeY = 96;
                break;
            }
            default -> {
                System.out.println("No Panel defined");
                System.exit(1);
            }
        }

        panelSizeX = xPanelCount * onepanelSizeX; //Anzahl Panel X * 128 pixel
        panelSizeY = yPanelCount * onepanelSizeY; //Anzahl Panel Y * 128 pixel

        System.out.println("Panel                    : " + wichPanel);
        System.out.println("x/y Panel Count          : " + Main.getxPanelCount() + "/" + Main.getyPanelCount());
        System.out.println("x/y Panel Size           : " + onepanelSizeX + "/" + onepanelSizeY);
        System.out.println("x/y Pixels               : " + panelSizeX + "/" + panelSizeY);
        System.out.println("rotation Degree          : " + Main.getRotation());
        System.out.println("mode                     : " + mode);
        System.out.println("Screen Number            : " + screenNumber);
        System.out.println("x/y Start Position       : " + xPosition + "/" + yPosition);
        System.out.println("broadcastIpAddress       : " + broadcastIpAddress);
        System.out.println("bind to interface        : " + bindToInterface);
        System.out.println("scaleFactor (Brightness) : " + scaleFactor.toString());
        System.out.println("offset (Contrast)        : " + offSet.toString());
        System.out.println("color (10/rgb 20/jpg 30/rgb 565)    : " + colorMode);
        System.out.println("sleep time               : " + sleep);

        switch (mode) {
            case "screen" -> captureFromScreen();
            case "ndi" -> Ndi.ndi();
        }
    }

    public static void captureFromScreen() throws AWTException {
        GraphicsDevice[] screens = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
        Robot robot = new Robot(screens[getScreenNumber()]);
        Rectangle rectangle = new Rectangle();
        Rectangle screenBounds = screens[getScreenNumber()].getDefaultConfiguration().getBounds();
        int x = getxPosition() + screenBounds.x;
        int y = getyPosition() + screenBounds.y;
        rectangle.setLocation(x, y);

        switch (Main.getRotation()) {
            case 90, 270 -> {
                int buf = Main.panelSizeX;
                Main.panelSizeX = Main.getPanelSizeY();
                Main.panelSizeY = buf;
            }
            case 180 -> {
                System.out.println("not Supported");
                System.exit(-1);
            }
        }
        rectangle.setSize(panelSizeX, panelSizeY);
        System.out.println(panelSizeX + " : " + panelSizeY);

        if (!SendSync.setDatagramSocket()) {
            return;
        }

        while (true) {
            Fps.fpsStart();
            BufferedImage image = robot.createScreenCapture(rectangle);
            PictureSender.send(image);
            Fps.fpsEnd();
        }

    }

    public static ArtNetClient getArtnet() {
        return artnet;
    }

    public static void setArtnet(ArtNetClient artnet) {
        Main.artnet = artnet;
    }

    public static boolean isArtnetDebug() {
        return artnetDebug;
    }

    public static void setArtnetDebug(boolean artnetDebug) {
        Main.artnetDebug = artnetDebug;
    }

    public static boolean isArtnetEnabled() {
        return artnetEnabled;
    }

    public static void setArtnetEnabled(boolean artnetEnabled) {
        Main.artnetEnabled = artnetEnabled;
    }

    public static boolean isBindToInterface() {
        return bindToInterface;
    }

    public static void setBindToInterface(boolean bindToInterface) {
        Main.bindToInterface = bindToInterface;
    }

    public static String getBroadcastIpAddress() {
        return broadcastIpAddress;
    }

    public static void setBroadcastIpAddress(String broadcastIpAddress) {
        Main.broadcastIpAddress = broadcastIpAddress;
    }

    public static int getChannel() {
        return channel;
    }

    public static void setChannel(int channel) {
        Main.channel = channel;
    }

    public static int getColorMode() {
        return colorMode;
    }

    public static void setColorMode(int colorMode) {
        Main.colorMode = colorMode;
    }

    public static byte getCourantFrame() {
        return courantFrame;
    }

    public static void setCourantFrame(byte courantFrame) {
        Main.courantFrame = courantFrame;
    }

    public static String getMode() {
        return mode;
    }

    public static void setMode(String mode) {
        Main.mode = mode;
    }

    public static Float getOffSet() {
        return offSet;
    }

    public static void setOffSet(Float offSet) {
        Main.offSet = offSet;
    }

    public static int getPanelSizeX() {
        return panelSizeX;
    }

    public static void setPanelSizeX(int panelSizeX) {
        Main.panelSizeX = panelSizeX;
    }

    public static int getPanelSizeY() {
        return panelSizeY;
    }

    public static void setPanelSizeY(int panelSizeY) {
        Main.panelSizeY = panelSizeY;
    }

    public static Integer[] getPictureConfiguration() {
        return pictureConfiguration;
    }

    public static void setPictureConfiguration(Integer[] pictureConfiguration) {
        Main.pictureConfiguration = pictureConfiguration;
    }

    public static int getPort() {
        return port;
    }

    public static void setPort(int port) {
        Main.port = port;
    }

    public static int getRotation() {
        return rotation;
    }

    public static void setRotation(int rotation) {
        Main.rotation = rotation;
    }

    public static Float getScaleFactor() {
        return scaleFactor;
    }

    public static void setScaleFactor(Float scaleFactor) {
        Main.scaleFactor = scaleFactor;
    }

    public static int getScreenNumber() {
        return screenNumber;
    }

    public static void setScreenNumber(int screenNumber) {
        Main.screenNumber = screenNumber;
    }

    public static boolean isShowFps() {
        return showFps;
    }

    public static void setShowFps(boolean showFps) {
        Main.showFps = showFps;
    }

    public static int getSleep() {
        return sleep;
    }

    public static void setSleep(int sleep) {
        Main.sleep = sleep;
    }

    public static int getSubnet() {
        return subnet;
    }

    public static void setSubnet(int subnet) {
        Main.subnet = subnet;
    }

    public static int getUniversum() {
        return universum;
    }

    public static void setUniversum(int universum) {
        Main.universum = universum;
    }

    public static String getWichPanel() {
        return wichPanel;
    }

    public static void setWichPanel(String wichPanel) {
        Main.wichPanel = wichPanel;
    }

    public static int getxPanelCount() {
        return xPanelCount;
    }

    public static void setxPanelCount(int xPanelCount) {
        Main.xPanelCount = xPanelCount;
    }

    public static int getxPosition() {
        return xPosition;
    }

    public static void setxPosition(int xPosition) {
        Main.xPosition = xPosition;
    }

    public static int getyPanelCount() {
        return yPanelCount;
    }

    public static void setyPanelCount(int yPanelCount) {
        Main.yPanelCount = yPanelCount;
    }

    public static int getyPosition() {
        return yPosition;
    }

    public static void setyPosition(int yPosition) {
        Main.yPosition = yPosition;
    }
}
