package com.nuvoled.configurartion;

import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.inspector.TagInspector;

import java.io.*;
import java.util.Set;

public class PanelConfigFileManager {
    public static void write() {
        try {
            File file = new File("panelConfig.yaml");
            FileWriter writer = new FileWriter(file);
            Yaml yaml = new Yaml();
            yaml.dump(ConfigManager.getStorage(), writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Storage read() {
        try {
            InputStream input = new FileInputStream("panelConfig.yaml");
            var loaderoptions = new LoaderOptions();
            TagInspector taginspector = tag -> true;

            loaderoptions.setTagInspector(taginspector);
            Yaml yaml = new Yaml(new Constructor(Storage.class, loaderoptions));
            Storage storage = yaml.load(input);
            return storage;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(-1);
            return null;
        }
    }
}
