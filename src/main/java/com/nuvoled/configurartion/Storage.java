package com.nuvoled.configurartion;

import com.nuvoled.Main;

import java.lang.reflect.Array;

public class Storage {
    private  Panel[][] alreadyConfiguredPanelMatrix;

    public Storage() {
        alreadyConfiguredPanelMatrix = new Panel[Main.getxPanelCount()][Main.getyPanelCount()];
    }

    public Panel[][] getAlreadyConfiguredPanelMatrix() {
        return alreadyConfiguredPanelMatrix;
    }

    public void setAlreadyConfiguredPanelMatrix(Panel[][] alreadyConfiguredPanelMatrix) {
        this.alreadyConfiguredPanelMatrix = alreadyConfiguredPanelMatrix;
    }
}
