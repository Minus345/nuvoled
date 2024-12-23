package com.nuvoled;

import ch.bildspur.artnet.ArtNetClient;
import com.nuvoled.ndi.Ndi;
import com.nuvoled.sender.PictureSender;
import com.nuvoled.sender.SendSync;
import org.apache.commons.cli.*;
import org.apache.log4j.varia.NullAppender;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

    private static int port = 2000;
    private static byte courantFrame = 2;
    private static String broadcastIpAddress = "169.254.255.255";
    private static int panelSizeX;
    private static int panelSizeY;
    private static String mode = "ndi";
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

        commandLineParameters(args);

        if (wichPanel == null || wichPanel.isEmpty()) {
            System.out.println("Choose Panel");
            System.exit(10);
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
        System.out.println("x/y Panel Count          : " + xPanelCount + "/" + yPanelCount);
        System.out.println("x/y Panel Size           : " + onepanelSizeX + "/" + onepanelSizeY);
        System.out.println("x/y Pixels               : " + panelSizeX + "/" + panelSizeY);
        System.out.println("rotation Degree          : " + rotationDegree());
        System.out.println("Screen Number            : " + screenNumber);
        System.out.println("x/y Start Position       : " + xPosition + "/" + yPosition);
        System.out.println("broadcastIpAddress       : " + broadcastIpAddress);
        System.out.println("bind to interface        : " + bindToInterface);
        System.out.println("scaleFactor (Brightness) : " + scaleFactor.toString());
        System.out.println("offset (Contrast)        : " + offSet.toString());
        System.out.println("color (10/rgb 20/jpg 30/rgb 565)    : " + colorMode);
        System.out.println("sleep time               : " + sleep);

        switch (mode) {
            case "picture" -> pictureMode();
            case "screen", "video" -> screenAndVideo();
            case "ndi" -> Ndi.ndi();
        }
    }

    public static void commandLineParameters(String[] args) {
        var options = new Options()
                .addOption("h", "help", false, "Help Message")
                .addOption("b", "bind", false, "bind to interface 169.254")
                .addOption("ad", "artnetDebug", false, "enables artnet debug")
                .addOption("p", "Panel", true, "choose Panel")
                .addOption("rgb", "rgb565", false, "sets the mode to rgb565")
                .addOption("fps", "fps", false, "prints out fps")
                .addOption(Option.builder("px")
                        .longOpt("panelsx")
                        .hasArg(true)
                        .desc("Number of Panels horizontal ")
                        .argName("1")
                        .build())
                .addOption(Option.builder("py")
                        .longOpt("panelsy")
                        .hasArg(true)
                        .desc("Number of Panels vertical ")
                        .argName("1")
                        .build())
                .addOption(Option.builder("sx")
                        .longOpt("startx")
                        .hasArg(true)
                        .desc("Pixel start horizontal ")
                        .argName("0")
                        .build())
                .addOption(Option.builder("sy")
                        .longOpt("starty")
                        .hasArg(true)
                        .desc("Pixal start vertical ")
                        .argName("0")
                        .build())
                .addOption(Option.builder("r")
                        .longOpt("rotation")
                        .hasArg(true)
                        .desc("rotation degree 0/90/180/270 ")
                        .argName("0")
                        .build())
                .addOption(Option.builder("br")
                        .longOpt("brightness")
                        .hasArg(true)
                        .desc("brightness value with 0.x -1.x")
                        .argName("0.6")
                        .build())
                .addOption(Option.builder("sn")
                        .longOpt("screennr")
                        .hasArg(true)
                        .desc("number of screen")
                        .argName("0")
                        .build())
                .addOption(Option.builder("s")
                        .longOpt("sleep")
                        .hasArg(true)
                        .desc("sleep ime in ms")
                        .argName("0")
                        .build())
                .addOption(Option.builder("o")
                        .longOpt("offset")
                        .hasArg(true)
                        .desc("offset (Contrast) ")
                        .argName("0")
                        .build())
                .addOption(Option.builder("a")
                        .longOpt("artnet")
                        .hasArg(true)
                        .desc("enables artnet")
                        .argName("<ip>")
                        .build())
                .addOption(Option.builder("as")
                        .longOpt("artnetSubnet")
                        .hasArg(true)
                        .desc("artnet subnet")
                        .argName("< 0 - 16 >")
                        .build())
                .addOption(Option.builder("au")
                        .longOpt("artnetUniverse")
                        .hasArg(true)
                        .desc("artnet universe")
                        .argName("< 0 - 16 >")
                        .build())
                .addOption(Option.builder("ac")
                        .longOpt("artnetChannel")
                        .hasArg(true)
                        .desc("artnet channel")
                        .argName("< 0 - 513 >")
                        .build());

        CommandLineParser parser = new DefaultParser();
        CommandLine line;
        try {
            // parse the command line arguments
            line = parser.parse(options, args);
            if (line.hasOption("b")) {
                bindToInterface = true;
            }
            if (line.hasOption("h")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("java -jar nuvoled.jar ", options);
                bindToInterface = true;
                System.exit(0);
            }
            if (line.hasOption("px")) {
                xPanelCount = Integer.parseInt(line.getOptionValue("px"));
            }
            if (line.hasOption("py")) {
                yPanelCount = Integer.parseInt(line.getOptionValue("py"));
            }
            if (line.hasOption("sx")) {
                xPosition = Integer.parseInt(line.getOptionValue("sx"));
            }
            if (line.hasOption("sy")) {
                yPosition = Integer.parseInt(line.getOptionValue("sy"));
            }
            if (line.hasOption("r")) {
                rotation = Integer.parseInt(line.getOptionValue("r"));
            }
            if (line.hasOption("br")) {
                scaleFactor = Float.parseFloat(line.getOptionValue("br"));
            }
            if (line.hasOption("sn")) {
                screenNumber = Integer.parseInt(line.getOptionValue("sn"));
            }
            if (line.hasOption("s")) {
                sleep = Integer.parseInt(line.getOptionValue("s"));
            }
            if (line.hasOption("o")) {
                offSet = Float.valueOf(line.getOptionValue("s"));
            }
            if (line.hasOption("ad")) {
                System.out.println("ad");
            }
            if (line.hasOption("a")) {
                System.out.println("Starting Artnet");
                artnet = new ArtNetClient();
                artnet.start(line.getOptionValue("a"));
                artnetEnabled = true;
                if (line.hasOption("as")) subnet = Integer.parseInt(line.getOptionValue("as"));
                if (line.hasOption("au")) universum = Integer.parseInt(line.getOptionValue("au"));
                if (line.hasOption("ac")) channel = Integer.parseInt(line.getOptionValue("ac"));
                if (line.hasOption("ad")) artnetDebug = true;
                System.out.println("Subnet: " + subnet);
                System.out.println("Universe: " + universum);
                System.out.println("Channel: " + channel);
                System.out.println("Debug: " + artnetDebug);
            }
            if (line.hasOption("p")) {
                wichPanel = String.valueOf(line.getOptionValue("p"));
            }
            if (line.hasOption("rgb565")) {
                Main.setColorMode(30);
            }
            if (line.hasOption("fps")) {
                Main.setShowFps(true);
            }
        } catch (ParseException exp) {
            // oops, something went wrong
            System.err.println("Parsing failed.  Reason: " + exp.getMessage());
        }
    }

    public static void screenAndVideo() throws AWTException {
        GraphicsDevice[] screens = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
        Robot robot = new Robot(screens[getScreenNumber()]);
        Rectangle rectangle = new Rectangle();
        Rectangle screenBounds = screens[getScreenNumber()].getDefaultConfiguration().getBounds();
        int x = getxPosition() + screenBounds.x;
        int y = getyPosition() + screenBounds.y;
        rectangle.setLocation(x, y);
        rectangle.setSize(panelSizeX, panelSizeY);

        if (!SendSync.setDatagramSocket()) {
            return;
        }

        long time = 0;
        int i = 0;
        while (true) {
            if (showFps) {
                i++;
                time = System.currentTimeMillis();
            }

            BufferedImage image = robot.createScreenCapture(rectangle);
            PictureSender.send(image);

            if (showFps) {
                float fps = (float) 1 / ((float) (System.currentTimeMillis() - time) / 1000);

                if (i == 100) {
                    System.out.println("fps: " + fps);
                    i = 0;
                }
            }

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

    public static int getSubnet() {
        return subnet;
    }

    public static int getUniversum() {
        return universum;
    }

    public static int getChannel() {
        return channel;
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

    public static void setScaleFactor(Float scaleFactor) {
        Main.scaleFactor = scaleFactor;
    }

    public static Float getOffset() {
        return offSet;
    }

    public static int getSleep() {
        return sleep;
    }

    public static boolean isArtnetEnabled() {
        return artnetEnabled;
    }

    public static ArtNetClient getArtnet() {
        return artnet;
    }

    public static boolean isArtnetDebug() {
        return artnetDebug;
    }

    public static int getColorMode() {
        return colorMode;
    }

    public static void setColorMode(int colorMode) {
        Main.colorMode = colorMode;
    }

    public static int getScreenNumber() {
        return screenNumber;
    }

    public static void setScreenNumber(int screenNumber) {
        Main.screenNumber = screenNumber;
    }

    public static int getxPosition() {
        return xPosition;
    }

    public static void setxPosition(int xPosition) {
        Main.xPosition = xPosition;
    }

    public static int getyPosition() {
        return yPosition;
    }

    public static void setyPosition(int yPosition) {
        Main.yPosition = yPosition;
    }

    public static boolean isShowFps() {
        return showFps;
    }

    public static void setShowFps(boolean showFps) {
        Main.showFps = showFps;
    }
}
