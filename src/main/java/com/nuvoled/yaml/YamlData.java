package com.nuvoled.yaml;

public class YamlData {
    public String string;
    public Object object;

    public YamlData(String string, Object object) {
        this.string = string;
        this.object = object;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
