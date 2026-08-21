package com.nuvoled;

import com.nuvoled.ImagGetter.ImageGetter;
import com.nuvoled.ImagGetter.ScreenCapture;
import com.nuvoled.ImagGetter.WebcamCapture;
import com.nuvoled.configurartion.*;
import com.nuvoled.panel.Panel;
import com.nuvoled.settings.Settings;
import com.nuvoled.settings.SettingsException;
import com.nuvoled.util.Fps;
import com.nuvoled.util.Rgb565;
import com.nuvoled.util.rotation.Rotation;
import com.nuvoled.settings.Yaml;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.util.Map;

public class Main {

    private static Settings settings;

    private static final int PORT = 2000;
    private static final String BROADCAST_IP_ADDRESS = "169.254.255.255";

    private static byte courantFrame = 2;

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

        ManageNetworkConnection manageNetworkConnection = new ManageNetworkConnection(PORT, BROADCAST_IP_ADDRESS);

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
                new Yaml().createYamlFile(args[1]);
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

                parseSetting(args[1]);

                manageNetworkConnection.setDatagramSocketForListeningAndSending(settings.getTimeout());
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

                parseSetting(args[1]);
                manageNetworkConnection.setDatagramSocketForListeningAndSending(settings.getTimeout());
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

                parseSetting(args[1]);
                manageNetworkConnection.setDatagramSocket();
            }
            case null, default -> exitSetup();
        }

        if (settings.getRotation() != 0) {
            System.out.println("""
                    If you use **rotation**:
                    * configure your panels resolution in _Nuvoled Home_ **AND** _Nuvoled Presenter_ as if they were not rotated in reality
                    * then configure your rotation start parameter (_-r_)""");
        }
        System.out.println();


        System.out.println("Panel                               : " + settings.getPanelType().getVersion());
        System.out.println("x/y Panel Count                     : " + settings.getxPanelCount() + "/" + settings.getyPanelCount());
        System.out.println("x/y Panel Size                      : " + settings.getPanelType().getSizeX() + "/" + settings.getPanelType().getSizeY());
        System.out.println("x/y Pixels                          : " + settings.getGlobalPixelInX() + "/" + settings.getGlobalPixelInY());
        System.out.println("rotation Degree                     : " + settings.getRotation());
        System.out.println("mode                                : " + settings.getMode());
        System.out.println("camera                              : " + settings.getCameraIndex());
        System.out.println("Screen Number                       : " + settings.getScreenNumber());
        System.out.println("x/y Start Position                  : " + settings.getxPosition() + "/" + settings.getyPosition());
        System.out.println("broadcastIpAddress                  : " + BROADCAST_IP_ADDRESS);
        System.out.println("scaleFactor (Brightness)            : " + settings.getBrightness());
        System.out.println("offset (Contrast)                   : " + settings.getOffSet());
        System.out.println("color (10/rgb 20/jpg 30/rgb 565)    : " + settings.getColorMode());
        System.out.println("sleep time                          : " + settings.getSleep());

        // setup rotation
        switch (settings.getRotation()) {
            case 90, 270 -> {
                //switch x and y
                int buf = settings.getGlobalPixelInX();
                settings.setGlobalPixelInX(settings.getGlobalPixelInY());
                settings.setGlobalPixelInY(buf);
            }
            case 180 -> {
                System.out.println("not Supported");
                System.exit(1);
            }
        }

        // setup mode
        ImageGetter imageGetter = null;
        switch (settings.getMode()) {
            case "screen" ->
                    imageGetter = new ScreenCapture(settings.getGlobalPixelInX(), settings.getGlobalPixelInY(), settings.getxPosition(), settings.getyPosition(), settings.getScreenNumber());
            case "camera" -> {
                try {
                    imageGetter = new WebcamCapture(settings.getCameraIndex());
                } catch (IllegalArgumentException e) {
                    System.err.println("Invalid Camera " + e.getMessage());
                    System.exit(1);
                } catch (IllegalStateException e) {
                    System.err.println("No camera found");
                    System.exit(1);
                } catch (IndexOutOfBoundsException e) {
                    System.err.println("Camera index out of bounce " + e.getMessage());
                    System.exit(1);
                } catch (RuntimeException e) {
                    System.err.println("Error while opening the camera: " + e.getMessage());
                    System.exit(1);
                }
            }
            default -> {
                System.err.println("\" " + settings.getMode() + "\" is not a valid mode.");
                System.exit(1);
            }
        }

        int rgbLength = settings.getGlobalPixelInX() * settings.getGlobalPixelInY() * 3;

        //noinspection InfiniteLoopStatement
        while (true) {
            Fps.fpsStart(settings.isShowFps());

            BufferedImage image = imageGetter.getImage();

            BufferedImage imageWithBrightness = PackagePicture.applyFilter(image, settings.getBrightness(), settings.getOffSet());
            byte[] rgbPixelData = PackagePicture.getLedBGRDataFormImage(imageWithBrightness, rgbLength, settings.getGlobalPixelInX(), settings.getGlobalPixelInY()); // returns Blue, Green, Red
            if (settings.getRotation() != 0) {
                rgbPixelData = Rotation.rotateRgbData(rgbPixelData, settings.getRotation(), settings.getGlobalPixelInX(), settings.getGlobalPixelInY());
            }

            //if mode = rgb565
            if (settings.getColorMode() == 30) {
                rgbPixelData = Rgb565.convertBGR888ToBGR565(rgbPixelData);
            }

            PackagePicture.packageAndSendPixels(rgbPixelData, manageNetworkConnection, settings.getColorMode());

            //sleep
            if (settings.getSleep() > 0) {
                try {
                    //noinspection BusyWait
                    Thread.sleep(settings.getSleep());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            //send sendSynchronized Message
            manageNetworkConnection.sendSyncro();

            Fps.fpsEnd(settings.isShowFps());
        }
    }

    private static void parseSetting(String path) {
        Map<String, Object> map = null;
        try {
            map = new Yaml().readYamlFormFile(path);
        } catch (FileNotFoundException e) {
            System.err.println("[CONFIG_FILE] could not load config file");
            System.exit(1);
        }

        try {
            settings = new Settings(map);
        } catch (SettingsException e) {
            System.exit(1);
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

    public static byte getCourantFrame() {
        return courantFrame;
    }

    public static void setCourantFrame(byte courantFrame) {
        Main.courantFrame = courantFrame;
    }

    public static int getGlobalPixelInX() {
        return settings.getGlobalPixelInX();
    }

    public static int getGlobalPixelInY() {
        return settings.getGlobalPixelInY();
    }

    public static int getxPanelCount() {
        return settings.getxPanelCount();
    }

    public static int getyPanelCount() {
        return settings.getyPanelCount();
    }

    public static Panel getPanelType() {
        return settings.getPanelType();
    }
}
