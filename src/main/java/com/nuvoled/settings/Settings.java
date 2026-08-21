package com.nuvoled.settings;

import com.nuvoled.panel.P4;
import com.nuvoled.panel.P5;
import com.nuvoled.panel.Panel;

import java.awt.*;
import java.util.Map;

public class Settings {
    private final Panel panelType;
    private final int xPanelCount;
    private final int yPanelCount;
    private int globalPixelInX;
    private int globalPixelInY;

    private final double brightness;
    private final double offSet;

    /**
     * 10/rgb 20/jpg 30/rgb565
     */
    private final int colorMode;
    private final int rotation;
    private final int sleep;
    private final int timeout;
    private final boolean showFps;

    private final String mode;
    //camera
    private final int cameraIndex;
    //screen capture
    private final int screenNumber;
    private final int xPosition;
    private final int yPosition;

    /**
     * Pareses Settings
     * Trows SettingsException on wrong user input and prints out error message
     *
     * @param settings
     */
    public Settings(Map<String, Object> settings) {
        // TODO: int values genauer checken > 0 ...
        String panelVersion = getWithError(settings, "PanelVersion", String.class);
        switch (panelVersion) {
            case "P4" -> panelType = new P4();
            case "P5" -> panelType = new P5();
            default -> {
                System.err.println("[CONFIG_FILE] Panel not supported: " + panelVersion);
                throw new SettingsException("Panel not supported");
            }
        }
        xPanelCount = getWithError(settings, "PanelCountX", Integer.class);
        yPanelCount = getWithError(settings, "PanelCountY", Integer.class);
        globalPixelInX = xPanelCount * panelType.getSizeX();
        globalPixelInY = yPanelCount * panelType.getSizeY();

        brightness = getWithError(settings, "brightness", Double.class);
        offSet = getWithError(settings, "offSet", Double.class);
        if (getWithError(settings, "rgb565", Boolean.class)) {
            colorMode = 30;
        } else {
            colorMode = 10;
        }
        rotation = getWithError(settings, "rotation", Integer.class);
        if (!(rotation == 0 || rotation == 90 || rotation == 180 || rotation == 270)) {
            System.out.println("[CONFIG_FILE] Rotation not supported: " + rotation);
            throw new SettingsException("Wrong rotation degree");
        }

        sleep = getWithError(settings, "sleep", Integer.class);
        timeout = getWithError(settings, "timeout", Integer.class);
        showFps = getWithError(settings, "showFps", Boolean.class);

        mode = getWithError(settings, "mode", String.class);

        cameraIndex = getWithError(settings, "camera", Integer.class);

        GraphicsDevice[] screens = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
        screenNumber = getWithError(settings, "screenNumber", Integer.class);
        if (screenNumber > screens.length) {
            System.out.println("[CONFIG_FILE] screen number out of bounce");
            throw new SettingsException("screen number to big");
        }
        xPosition = getWithError(settings, "PositionX", Integer.class);
        yPosition = getWithError(settings, "PositionY", Integer.class);
    }

    private <T> T getWithError(Map<String, Object> map, String key, Class<T> type) {
        Object value = map.get(key);
        if (value == null) {
            System.err.println("[CONFIG_FILE] The key: \"" + key + "\" could not be found in the config File. The config file could not be read correctly. The settings may have changed. Try making a new config file (java -jar nuvoled.jar create [<path where you want your default config file>])");
            throw new SettingsException("Key is null");
        }

        try {
            return type.cast(value);
        } catch (ClassCastException e) {
            System.err.println("[CONFIG_FILE] Loading the config file went wrong. There is an error in your config file at: \n\"" + key + " : " + value + "\"\n\"" + value + "\" is not a " + value.getClass().getSimpleName());
            throw new SettingsException("Wrong Type");
        }
    }

    public int getyPanelCount() {
        return yPanelCount;
    }

    public Panel getPanelType() {
        return panelType;
    }

    public int getxPanelCount() {
        return xPanelCount;
    }

    public int getGlobalPixelInX() {
        return globalPixelInX;
    }

    public int getGlobalPixelInY() {
        return globalPixelInY;
    }

    public double getBrightness() {
        return brightness;
    }

    public double getOffSet() {
        return offSet;
    }

    public int getColorMode() {
        return colorMode;
    }

    public int getRotation() {
        return rotation;
    }

    public int getSleep() {
        return sleep;
    }

    public int getTimeout() {
        return timeout;
    }

    public boolean isShowFps() {
        return showFps;
    }

    public String getMode() {
        return mode;
    }

    public int getCameraIndex() {
        return cameraIndex;
    }

    public int getScreenNumber() {
        return screenNumber;
    }

    public int getxPosition() {
        return xPosition;
    }

    public int getyPosition() {
        return yPosition;
    }

    public void setGlobalPixelInX(int globalPixelInX) {
        this.globalPixelInX = globalPixelInX;
    }

    public void setGlobalPixelInY(int globalPixelInY) {
        this.globalPixelInY = globalPixelInY;
    }
}

