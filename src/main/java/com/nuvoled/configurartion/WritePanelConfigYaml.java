package com.nuvoled.configurartion;

import org.yaml.snakeyaml.Yaml;

public class WritePanelConfigYaml {
    public static void write(){
        String output = new Yaml().dump(ConfigManager.getAlreadyConfiguredPanelMatrix());
    }
}
