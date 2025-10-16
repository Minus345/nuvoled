package com.nuvoled.yaml;

import com.nuvoled.Main;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

public class YamlReader {
    Map<String, Object> settings;

    public YamlReader(String path) {
        //try to load config from path
        try {
            loadYamlFromFileInToMemory(path);
        } catch (FileNotFoundException e) {
            System.out.println("The path is not correct");
            System.exit(-1);
        }

        loadParameters();
    }

    private void loadYamlFromFileInToMemory(String path) throws FileNotFoundException {
        InputStream input = new FileInputStream(path);
        Yaml yaml = new Yaml();
        settings = yaml.load(input);
    }

    private void loadParameters() {
        try {
            //global settings
            Main.setWichPanel((String) settings.get("PanelVersion"));
            Main.setxPanelCount((int) settings.get("PanelCountX"));
            Main.setyPanelCount((int) settings.get("PanelCountY"));
            Main.setBrightness((float) ((double) settings.get("brightness")));
            if ((boolean) settings.get("rgb565")) {
                Main.setColorMode(30);
            } else {
                Main.setColorMode(10);
            }

            //panel settings
            Main.setRotation((int) settings.get("rotation"));
            Main.setSleep((int) settings.get("sleep"));
            Main.setOffSet((float) ((double) settings.get("offSet")));
            Main.setShowFps((boolean) settings.get("showFps"));
            Main.setTimeout((int) settings.get("timeout"));

            //ndi
            Main.setMode((String) settings.get("mode"));

            //art-net
            Main.setArtnetEnabled((boolean) settings.get("artnetEnabled"));
            Main.setArtnetDebug((boolean) settings.get("artnetDebug"));
            Main.setSubnet((int) settings.get("artnetSubnet"));
            Main.setUniversum((int) settings.get("artnetUniversum"));
            Main.setChannel((int) settings.get("artnetChannel"));

            //panel specific
            Main.setScreenNumber((int) settings.get("screenNumber"));
            Main.setxPosition((int) settings.get("PositionX"));
            Main.setyPosition((int) settings.get("PositionY"));
        } catch (Exception e) {
            System.out.println("Loading the config file went wrong. There is an error in your config file:");
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
