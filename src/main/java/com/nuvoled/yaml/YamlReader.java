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

        Main.setupConfiguration(settings);
    }

    private void loadYamlFromFileInToMemory(String path) throws FileNotFoundException {
        InputStream input = new FileInputStream(path);
        Yaml yaml = new Yaml();
        settings = yaml.load(input);
    }

}
