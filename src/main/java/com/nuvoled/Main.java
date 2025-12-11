package com.nuvoled;

import com.nuvoled.configurartion.*;
import com.nuvoled.panel.P4;
import com.nuvoled.panel.P5;
import com.nuvoled.panel.Panel;
import com.nuvoled.util.Fps;
import com.nuvoled.sender.PictureSender;
import com.nuvoled.sender.ManageNetworkConnection;
import com.nuvoled.util.Rgb565;
import com.nuvoled.util.rotation.Rotation;
import com.nuvoled.yaml.YamlReader;
import com.nuvoled.yaml.YamlWriter;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Main {

    //not changed
    private static ManageNetworkConnection manageNetworkConnection;
    private static final int port = 2000;
    private static byte courantFrame = 2;
    private static final String broadcastIpAddress = "169.254.255.255";
    private static int globalPixelInX;
    private static int globalPixelInY;

    //global settings
    private static float brightness = 0.6F;
    private static float offSet = 0F;
    private static int rotation = 0;
    private static int sleep = 0;
    private static int timeout = 0;

    private static String mode = "screen";

    //panel settings
    /**
     * "P4" / "P5"
     */
    //TODO: merge into one variable
    private static String wichPanel;
    private static Panel panelType;
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


        if (args.length < 2 || args.length > 3) {
            exitSetup();
        }

        manageNetworkConnection = new ManageNetworkConnection(port,broadcastIpAddress);

        //start parameters
        switch (args[0]) {
            case "create" -> {
                System.out.println("---create---");
                if (args.length != 2) {
                    exitSetup();
                }
                if (args[1] == null) {
                    exitSetup();
                }
                //creates yaml file then terminates
                //create file where user wants
                new YamlWriter(args[1]);
                System.exit(0);
            }
            case "config" -> {
                System.out.println("---config---");
                //starts the configuration progress for the LED wall the terminates
                if (args.length != 2) {
                    exitSetup();
                }
                if (args[1] == null) {
                    exitSetup();
                }

                startSetupReadConfig(args[1]);

                manageNetworkConnection.setDatagramSocketForListeningAndSending(timeout);
                ConfigManager.start(manageNetworkConnection);

                manageNetworkConnection.closeSocket();
                System.exit(0);
            }
            case "load" -> {
                System.out.println("---load---");
                if (args.length != 3) {
                    exitSetup();
                }
                if (args[1] == null || args[2] == null) {
                    exitSetup();
                }

                startSetupReadConfig(args[1]);
                manageNetworkConnection.setDatagramSocketForListeningAndSending(timeout);
                SendConfigureMessages.reset(manageNetworkConnection);

                SendConfigureMessages.sendGlobalConfigMessage(manageNetworkConnection, PanelConfigFileManager.read(args[2]).getAlreadyConfiguredPanelMatrix());

                manageNetworkConnection.closeSocket();
                System.exit(0);
            }
            case "start" -> {
                System.out.println("---start---");
                if (args.length != 2) {
                    exitSetup();
                }
                if (args[1] == null) {
                    exitSetup();
                }

                startSetupReadConfig(args[1]);

                manageNetworkConnection.setDatagramSocket();
            }
            case null, default -> exitSetup();
        }

        if (rotation != 0) {
            System.out.println("""
                    If you use **rotation**:
                    * configure your panels resolution in _Nuvoled Home_ **AND** _Nuvoled Presenter_ as if they were not rotated in reality
                    * then configure your rotation start parameter (_-r_)""");
        }
        System.out.println();


        System.out.println("Panel                               : " + wichPanel);
        System.out.println("x/y Panel Count                     : " + xPanelCount + "/" + yPanelCount);
        System.out.println("x/y Panel Size                      : " + panelType.getSizeX() + "/" + panelType.getSizeY());
        System.out.println("x/y Pixels                          : " + globalPixelInX + "/" + globalPixelInY);
        System.out.println("rotation Degree                     : " + rotation);
        System.out.println("mode                                : " + mode);
        System.out.println("Screen Number                       : " + screenNumber);
        System.out.println("x/y Start Position                  : " + xPosition + "/" + yPosition);
        System.out.println("broadcastIpAddress                  : " + broadcastIpAddress);
        System.out.println("scaleFactor (Brightness)            : " + brightness);
        System.out.println("offset (Contrast)                   : " + offSet);
        System.out.println("color (10/rgb 20/jpg 30/rgb 565)    : " + colorMode);
        System.out.println("sleep time                          : " + sleep);

        switch (mode) {
            case "screen" -> captureFromScreen();
        }
    }

    public static void captureFromScreen() throws AWTException {
        //setup screen capture
        GraphicsDevice[] screens = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
        Robot robot = new Robot(screens[getScreenNumber()]);
        Rectangle rectangle = new Rectangle();
        Rectangle screenBounds = screens[getScreenNumber()].getDefaultConfiguration().getBounds();
        int x = xPosition + screenBounds.x;
        int y = yPosition + screenBounds.y;
        rectangle.setLocation(x, y);

        // setup colour mode
        int maxPackets = 0;
        switch (colorMode) {
            case 10: //rgb:
                maxPackets = ((globalPixelInX * globalPixelInY * 3) / 1440) + 1; //rgb -> 3 rgb565 -> 2
                break;
            case 30: //rgb565:
                maxPackets = ((globalPixelInX * globalPixelInY * 2) / 1440) + 1; //rgb -> 3 rgb565 -> 2
                break;
            default:
                System.out.println("Error: Colour mode " + colorMode + " not supported");
                System.exit(1);
        }

        // setup rotation
        switch (rotation) {
            case 90, 270 -> {
                //switch x and y
                int buf = globalPixelInX;
                //noinspection SuspiciousNameCombination
                globalPixelInX = globalPixelInY;
                globalPixelInY = buf;
            }
            case 180 -> {
                System.out.println("not Supported");
                System.exit(-1);
            }
        }
        rectangle.setSize(globalPixelInX, globalPixelInY);

        int rgbLength = globalPixelInX * globalPixelInY * 3;

        //noinspection InfiniteLoopStatement
        while (true) {
            Fps.fpsStart(showFps);

            //get picture form screen
            BufferedImage image = robot.createScreenCapture(rectangle);

            BufferedImage imageWithBrightness = PictureSender.applyFilter(image, brightness, offSet);
            byte[] rgbPixelData = PictureSender.getLedRgbDataFormImage(imageWithBrightness, rgbLength);
            rgbPixelData = Rotation.rotateRgbData(rgbPixelData, rotation);

            //if mode = rgb565
            if (colorMode == 30) {
                rgbPixelData = Rgb565.getLedRgb565Data(rgbPixelData);
            }

            //send the rgb data
            PictureSender.packageAndSendPixels(rgbPixelData, maxPackets,manageNetworkConnection);

            //sleep
            if (sleep > 0) {
                try {
                    //noinspection BusyWait
                    Thread.sleep(sleep);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            //send sendSynchronized Message
            manageNetworkConnection.sendSyncro();

            Fps.fpsEnd(showFps);
        }

    }

    private static void startSetupReadConfig(String pathToConfigFile) {
        new YamlReader(pathToConfigFile);
        wichPanel();
    }

    private static void exitSetup() {
        String defaultMessage = """
                Usage
                1. Create Config file:
                `java -jar nuvoled.jar create [<path where you want your default config file>]`
                2. Configure your LED Wall:
                `java -jar nuvoled.jar config <path to config file>`
                `java -jar nuvoled.jar load <path to config file> <path to panel-config file>`
                3. Normal Sender:
                `java -jar nuvoled.jar start <path to config file>`
                further information on github: https://github.com/Minus345/nuvoled""";

        System.out.println(defaultMessage);
        System.exit(1);

    }

    private static void wichPanel() {
        switch (wichPanel) {
            case "P4" -> panelType = new P4();
            case "P5" -> panelType = new P5();
            default -> {
                System.out.println("No Panel defined");
                System.exit(-1);
            }
        }

        globalPixelInX = xPanelCount * panelType.getSizeX(); //Anzahl Panel X * 128 pixel
        globalPixelInY = yPanelCount * panelType.getSizeY(); //Anzahl Panel Y * 128 pixel
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

    public static void setMode(String mode) {
        Main.mode = mode;
    }

    public static void setOffSet(Float offSet) {
        Main.offSet = offSet;
    }

    public static int getGlobalPixelInX() {
        return globalPixelInX;
    }

    public static int getGlobalPixelInY() {
        return globalPixelInY;
    }

    public static int getPort() {
        return port;
    }

    public static void setRotation(int rotation) {
        Main.rotation = rotation;
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

    public static void setShowFps(boolean showFps) {
        Main.showFps = showFps;
    }

    public static void setSleep(int sleep) {
        Main.sleep = sleep;
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

    public static void setxPosition(int xPosition) {
        Main.xPosition = xPosition;
    }

    public static int getyPanelCount() {
        return yPanelCount;
    }

    public static void setyPanelCount(int yPanelCount) {
        Main.yPanelCount = yPanelCount;
    }

    public static void setyPosition(int yPosition) {
        Main.yPosition = yPosition;
    }

    public static void setTimeout(int timeout) {
        Main.timeout = timeout;
    }

    public static Panel getPanelType() {
        return panelType;
    }
}
