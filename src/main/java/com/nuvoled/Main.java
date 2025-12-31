package com.nuvoled;

import com.nuvoled.configurartion.*;
import com.nuvoled.panel.P4;
import com.nuvoled.panel.P5;
import com.nuvoled.panel.Panel;
import com.nuvoled.util.Fps;
import com.nuvoled.util.Rgb565;
import com.nuvoled.util.rotation.Rotation;
import com.nuvoled.yaml.YamlReader;
import com.nuvoled.yaml.YamlWriter;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Map;

public class Main {

    //not changed
    private static ManageNetworkConnection manageNetworkConnection;
    private static final int PORT = 2000;
    private static byte courantFrame = 2;
    private static final String BROADCAST_IP_ADDRESS = "169.254.255.255";
    private static int globalPixelInX;
    private static int globalPixelInY;

    //global settings
    private static double brightness = 0.6D;
    private static double offSet = 0;
    private static int rotation = 0;
    private static int sleep = 0;
    private static int timeout = 0;

    private static String mode = "screen";

    //panel settings
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

    public static void main(String[] args) throws AWTException {
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

        manageNetworkConnection = new ManageNetworkConnection(PORT, BROADCAST_IP_ADDRESS);

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

                new YamlReader(args[1]);

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

                new YamlReader(args[1]);
                manageNetworkConnection.setDatagramSocketForListeningAndSending(timeout);
                SendConfigureMessages sendConfigureMessages = new SendConfigureMessages(manageNetworkConnection);
                sendConfigureMessages.reset();

                sendConfigureMessages.sendGlobalConfigMessage(PanelConfigFileManager.read(args[2]).getAlreadyConfiguredPanelMatrix());

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

                new YamlReader(args[1]);

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


        System.out.println("Panel                               : " + panelType.getVersion());
        System.out.println("x/y Panel Count                     : " + xPanelCount + "/" + yPanelCount);
        System.out.println("x/y Panel Size                      : " + panelType.getSizeX() + "/" + panelType.getSizeY());
        System.out.println("x/y Pixels                          : " + globalPixelInX + "/" + globalPixelInY);
        System.out.println("rotation Degree                     : " + rotation);
        System.out.println("mode                                : " + mode);
        System.out.println("Screen Number                       : " + screenNumber);
        System.out.println("x/y Start Position                  : " + xPosition + "/" + yPosition);
        System.out.println("broadcastIpAddress                  : " + BROADCAST_IP_ADDRESS);
        System.out.println("scaleFactor (Brightness)            : " + brightness);
        System.out.println("offset (Contrast)                   : " + offSet);
        System.out.println("color (10/rgb 20/jpg 30/rgb 565)    : " + colorMode);
        System.out.println("sleep time                          : " + sleep);

        captureFromScreen();
    }

    private static void captureFromScreen() throws AWTException {
        //setup screen capture
        GraphicsDevice[] screens = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
        Robot robot = new Robot(screens[screenNumber]);
        Rectangle rectangle = new Rectangle();
        Rectangle screenBounds = screens[screenNumber].getDefaultConfiguration().getBounds();
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

            BufferedImage imageWithBrightness = PackagePicture.applyFilter(image, brightness, offSet);
            byte[] rgbPixelData = PackagePicture.getLedRgbDataFormImage(imageWithBrightness, rgbLength);
            if (rotation != 0) {
                rgbPixelData = Rotation.rotateRgbData(rgbPixelData, rotation, globalPixelInX, globalPixelInY);
            }

            //if mode = rgb565
            if (colorMode == 30) {
                rgbPixelData = Rgb565.getLedRgb565Data(rgbPixelData);
            }

            //send the rgb data
            PackagePicture.packageAndSendPixels(rgbPixelData, maxPackets, manageNetworkConnection, colorMode);

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

    public static void setupConfiguration(Map<String, Object> settings) {
        String wichPanel = getWithErrorString(settings, "PanelVersion");
        switch (wichPanel) {
            case "P4" -> panelType = new P4();
            case "P5" -> panelType = new P5();
            default -> {
                System.out.println("[CONFIG_FILE] Panel not supported: " + wichPanel);
                throw new RuntimeException("Panel not supported");
            }
        }

        globalPixelInX = xPanelCount * panelType.getSizeX(); //Anzahl Panel X * 128 pixel
        globalPixelInY = yPanelCount * panelType.getSizeY(); //Anzahl Panel Y * 128 pixel

        xPanelCount = getWithErrorInteger(settings, "PanelCountX");
        yPanelCount = getWithErrorInteger(settings, "PanelCountY");
        brightness = getWithErrorDouble(settings, "brightness");
        if (getWithErrorBoolean(settings, "rgb565")) {
            colorMode = 30;
        } else {
            colorMode = 10;
        }

        //panel settings
        rotation = getWithErrorInteger(settings, "rotation");
        if (!(rotation == 0 || rotation == 90 || rotation == 180 || rotation == 270)) {
            System.out.println("[CONFIG_FILE] Rotation not supported: " + rotation);
            throw new RuntimeException("Wrong rotation degree");
        }
        sleep = getWithErrorInteger(settings, "sleep");
        offSet = getWithErrorDouble(settings, "offSet");
        showFps = getWithErrorBoolean(settings, "showFps");
        timeout = getWithErrorInteger(settings, "timeout");

        // screen capture stuff
        GraphicsDevice[] screens = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
        screenNumber = getWithErrorInteger(settings, "screenNumber");
        if (screenNumber > screens.length) {
            System.out.println("[CONFIG_FILE] screen number out of bounce");
            throw new ArrayIndexOutOfBoundsException("screen number to big");
        }
        xPosition = getWithErrorInteger(settings, "PositionX");
        yPosition = getWithErrorInteger(settings, "PositionY");
    }

    private static String getWithErrorString(Map<String, Object> map, String key) {
        String returnValue = null;
        try {
            returnValue = (String) map.get(key);
        } catch (ClassCastException e) {
            castError(key, "String");
        }
        if (returnValue == null) {
            nullError(key);
        }
        return returnValue;
    }

    private static int getWithErrorInteger(Map<String, Object> map, String key) {
        Object returnValue;
        returnValue = map.get(key);
        if (returnValue == null) {
            nullError(key);
        }
        int value = 0;
        try {
            value = (int) returnValue;
        } catch (ClassCastException e) {
            castError(key, "Integer");
        }

        if (value < 0) {
            negativeError(key);
        }

        return value;
    }

    private static double getWithErrorDouble(Map<String, Object> map, String key) {
        Object returnValue;
        returnValue = map.get(key);
        if (returnValue == null) {
            nullError(key);
        }
        double value = 0;
        try {
            value = (double) returnValue;
        } catch (ClassCastException e) {
            castError(key, "Double");
        }

        if (value < 0) {
            negativeError(key);
        }

        return value;
    }

    private static boolean getWithErrorBoolean(Map<String, Object> map, String key) {
        Object returnValue;
        returnValue = map.get(key);
        if (returnValue == null) {
            nullError(key);
        }
        try {
            return (boolean) returnValue;
        } catch (ClassCastException e) {
            castError(key, "Boolean");
        }
        return false;
    }

    private static void castError(String object, String datatype) {
        System.out.println("[CONFIG_FILE] Loading the config file went wrong. There is an error in your config file at: " + object + "\n" + object + " : is not a " + datatype);
        throw new ClassCastException();
    }

    private static void nullError(String object) {
        System.out.println("[CONFIG_FILE] The config file cannot be read correctly. The settings may have changed. Try making a new config file (java -jar nuvoled.jar create [<path where you want your default config file>]) \n" + "At: " + object);
        throw new NullPointerException();
    }

    private static void negativeError(String key) {
        System.out.println("[CONFIG_FILE] " + key + " : no negative value supported");
        throw new RuntimeException("Negative Value");
    }

    public static byte getCourantFrame() {
        return courantFrame;
    }

    public static void setCourantFrame(byte courantFrame) {
        Main.courantFrame = courantFrame;
    }

    public static int getGlobalPixelInX() {
        return globalPixelInX;
    }

    public static int getGlobalPixelInY() {
        return globalPixelInY;
    }

    public static int getxPanelCount() {
        return xPanelCount;
    }

    public static int getyPanelCount() {
        return yPanelCount;
    }

    public static Panel getPanelType() {
        return panelType;
    }
}
