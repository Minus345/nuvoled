package com.nuvoled.util.rotation;

public class Point {
    private byte r;
    private byte g;
    private byte b;

    public Point(byte r, byte g, byte b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public byte getB() {
        return b;
    }

    public byte getG() {
        return g;
    }

    public byte getR() {
        return r;
    }
}
