package com.nuvoled.configurartion;

import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.inspector.TagInspector;

import java.io.*;

public class PanelConfigFileManager {
    public static void write(String path) {
        try {
            File file = new File(path);
            FileWriter writer = new FileWriter(file);
            Yaml yaml = new Yaml();
            yaml.dump(ConfigManager.getStorage(), writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Storage read(String path) {
        try {
            InputStream input = new FileInputStream(path);
            var loaderoptions = new LoaderOptions();
            TagInspector taginspector = tag -> true;
            loaderoptions.setTagInspector(taginspector);
            Yaml yaml = new Yaml(new Constructor(Storage.class, loaderoptions));
            return yaml.load(input);
        } catch (FileNotFoundException e) {
            System.out.println("File Not Found");
            System.exit(-1);
            return null;
        }
    }
}
