package com.nuvoled.configurartion;

public class P5 extends Panel {
    public P5(byte[] mac) {
        super(mac, 128, 96, "P5");
    }

    //for yaml file reading
    public P5() {
        super();
    }
}
