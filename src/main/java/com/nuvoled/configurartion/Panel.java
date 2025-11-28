package com.nuvoled.configurartion;

public class Panel {
    private String version;
    private int sizeX;
    private int sizeY;
    private byte[] mac;
    private int position;
    private int offsetX;
    private int offsetY;
    private boolean configured;

    public Panel(byte[] mac, int sizeY, int sizeX, String version) {
        this.mac = mac;
        this.sizeY = sizeY;
        this.sizeX = sizeX;
        this.version = version;
    }

    //for yaml file reading
    public Panel() {
    }

    public byte[] getMac() {
        return mac;
    }

    public String getVersion() {
        return version;
    }

    public int getSizeX() {
        return sizeX;
    }

    public int getSizeY() {
        return sizeY;
    }

    public int getPosition() {
        return position;
    }

    public int getOffsetX() {
        return offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public boolean isConfigured() {
        return configured;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setConfigured(boolean configured) {
        this.configured = configured;
    }

    public void setOffsetX(int offsetX) {
        this.offsetX = offsetX;
    }

    public void setOffsetY(int offsetY) {
        this.offsetY = offsetY;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setSizeX(int sizeX) {
        this.sizeX = sizeX;
    }

    public void setSizeY(int sizeY) {
        this.sizeY = sizeY;
    }

    public void setMac(byte[] mac) {
        this.mac = mac;
    }
}
