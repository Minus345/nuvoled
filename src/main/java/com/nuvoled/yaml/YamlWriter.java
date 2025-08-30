package com.nuvoled.yaml;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class YamlWriter {

    public static void main(String[] args) {
        new YamlWriter("C:\\Users\\Max\\IdeaProjects\\nuvoled");
    }

    public YamlWriter(String path) {

        try {
            //create empty file in path
            File file = new File(path + "\\config.yaml");
            if (file.createNewFile()) {
                System.out.println("File created: " + file.getName());
            } else {
                System.out.println("File already exists.");
            }

            //write to file
            FileWriter writer = new FileWriter(file);

            DumperOptions options = new DumperOptions();
            options.setPrettyFlow(true);
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

            Yaml yaml = new Yaml(options);

            yaml.dump(createYamlData(), writer);
            writer.close();
        } catch (IOException e) {
            System.out.println("An error occurred, while creating config file");
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private Map<String, Object> createYamlData() {
        Map<String, Object> data = new LinkedHashMap<>();

        //global settings
        data.put("bindToInterface", false);
        data.put("scaleFactor", 0.6);
        data.put("offSet", 0.0);
        data.put("rotation", 0);
        data.put("sleep", 0);

        //ndi
        data.put("mode", "screen");

        //art-net
        data.put("artnetEnabled", false);
        data.put("artnetDebug", false);
        data.put("artnetSubnet", 0);
        data.put("artnetUniversum", 0);
        data.put("artnetChannel", 0);

        //panel settings
        data.put("colorMode", 10);
        data.put("showFps", false);

        //panel specific
        data.put("PanelCountX", 1);
        data.put("PanelCountY", 1);
        data.put("screenNumber", 0);
        data.put("PositionX", 0);
        data.put("PositionY", 0);

        return data;
    }
}
