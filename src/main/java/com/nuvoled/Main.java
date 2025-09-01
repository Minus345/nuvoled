package com.nuvoled;

import ch.bildspur.artnet.ArtNetClient;
import com.nuvoled.util.Fps;
import com.nuvoled.ndi.Ndi;
import com.nuvoled.sender.PictureSender;
import com.nuvoled.sender.ManageNetworkConnection;
import com.nuvoled.yaml.YamlReader;
import com.nuvoled.yaml.YamlWriter;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Main {

    //not changed
    private static final int port = 2000;
    private static byte courantFrame = 2;
    private static final String broadcastIpAddress = "169.254.255.255";
    private static int panelSizeX;
    private static int panelSizeY;

    //global settings
    private static boolean bindToInterface = false;
    private static float brightness = 0.6F;
    private static float offSet = 0F;
    private static int rotation = 0;
    private static int sleep = 0;

    //ndi
    private static String mode = "screen";

    //art-net
    private static ArtNetClient artnet;
    private static boolean artnetEnabled = false;
    private static boolean artnetDebug = false;
    private static int subnet = 0;
    private static int universum = 0;
    private static int channel = 0;

    //panel settings
    /**
     * "P4" / "P5"
     */
    private static String wichPanel;
    /**
     * 10/rgb 20/jpg 30/rgb565
     */
    private static int colorMode = 10;
    private static boolean showFps = false;

    //panel specific
    private static int xPanelCount = 1;
    private static int yPanelCount = 1;
    private static int screenNumber = 0;
    private static int xPosition = 0;
    private static int yPosition = 0;

    public static void main(String[] args) throws IOException, AWTException, InterruptedException {
        System.out.println("""
                  _  _              _        _\s
                 | \\| |_  ___ _____| |___ __| |
                 | .` | || \\ V / _ \\ / -_) _` |
                 |_|\\_|\\_,_|\\_/\\___/_\\___\\__,_|
                                               \
                """);

        String defaultMessage = "To crate a config file type: java -jar nuvoled.jar create <path>\nTo start the application with a config file: java -jar nuvoled.jar <path>";

        //parsing the command line arguments
        if (args.length == 0 || args.length > 2) {
            System.out.println(defaultMessage);
            System.exit(-1);
        } else if (args.length == 1) {
            new YamlReader(args[0]);
        } else if (args[0].equals("create")) {
            new YamlWriter(args[1]);
        } else {
            System.out.println(defaultMessage);
            System.exit(-1);
        }

        if (rotation != 0) {
            System.out.println("""
                    If you use **rotation**:
                    * configure your panels resolution in _Nuvoled Home_ **AND** _Nuvoled Presenter_ as if they were not rotated in reality
                    * then configure your rotation start parameter (_-r_)
                    * if you use a NDI Source: Configure the resolution with the rotation -> like in reality""");
        }
        System.out.println();

        if (Main.isArtnetEnabled()) {
            Main.setArtnet(new ArtNetClient());
            artnet.start();
        }

        int onePanelSizeX = 0;
        int onePanelSizeY = 0;

        switch (wichPanel) {
            case "P4" -> {
                onePanelSizeX = 128;
                onePanelSizeY = 128;
            }
            case "P5" -> {
                onePanelSizeX = 128;
                onePanelSizeY = 96;
            }
            default -> {
                System.out.println("No Panel defined");
                System.exit(-1);
            }
        }

        panelSizeX = xPanelCount * onePanelSizeX; //Anzahl Panel X * 128 pixel
        panelSizeY = yPanelCount * onePanelSizeY; //Anzahl Panel Y * 128 pixel

        System.out.println("Panel                               : " + Main.getWichPanel());
        System.out.println("x/y Panel Count                     : " + Main.getxPanelCount() + "/" + Main.getyPanelCount());
        System.out.println("x/y Panel Size                      : " + onePanelSizeX + "/" + onePanelSizeY);
        System.out.println("x/y Pixels                          : " + Main.getPanelSizeX() + "/" + Main.getPanelSizeY());
        System.out.println("rotation Degree                     : " + Main.getRotation());
        System.out.println("mode                                : " + Main.getMode());
        System.out.println("Screen Number                       : " + Main.getScreenNumber());
        System.out.println("x/y Start Position                  : " + Main.getxPosition() + "/" + Main.getyPosition());
        System.out.println("broadcastIpAddress                  : " + Main.getBroadcastIpAddress());
        System.out.println("bind to interface                   : " + Main.isBindToInterface());
        System.out.println("scaleFactor (Brightness)            : " + Main.getBrightness());
        System.out.println("offset (Contrast)                   : " + Main.getOffSet());
        System.out.println("color (10/rgb 20/jpg 30/rgb 565)    : " + Main.getColorMode());
        System.out.println("sleep time                          : " + Main.getSleep());

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

        ManageNetworkConnection.setDatagramSocket();

        //noinspection InfiniteLoopStatement
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

    public static int getPort() {
        return port;
    }

    public static int getRotation() {
        return rotation;
    }

    public static void setRotation(int rotation) {
        Main.rotation = rotation;
    }

    public static Float getBrightness() {
        return brightness;
    }

    public static void setBrightness(Float brightness) {
        Main.brightness = brightness;
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
