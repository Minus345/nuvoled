package com.nuvoled.configurartion;

import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.*;

public class PanelConfigFileManager {
    public static void write() {
        try {
            File file = new File("panelConfig.yaml");
            FileWriter writer = new FileWriter(file);
            Yaml yaml = new Yaml();
            yaml.dump(ConfigManager.getStorage(), writer);
            writer.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void read(){
        try {
            InputStream input = new FileInputStream("panelConfig.yaml");
            Constructor contructor = new Constructor(Storage.class, new LoaderOptions());
            Storage storage = new Yaml(contructor).load(input);
        } catch (FileNotFoundException e){
            e.printStackTrace();
        }
    }
}
