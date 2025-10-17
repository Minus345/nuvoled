package com.nuvoled.configurartion;

public class P4 extends Panel{
    public P4(byte[] mac) {
        super(mac, 128, 128, "P4");
    }

    //for yaml file reading
    public P4() {
        super();
    }
}
