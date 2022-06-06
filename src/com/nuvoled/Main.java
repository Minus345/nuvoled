package com.nuvoled;

import java.util.Objects;

public class Main {

    private static String addr;

    public static void main(String[] args) {
        for(int i = 0; i < args.length; i++) {
            System.out.println("Parameter: " + args[i]);
        }
        if (Objects.equals(args[0], "start")) {
            System.out.println("Start");
            addr = args[1];
            new Receiver().run(2000);
        }
    }

    public static String getAddr() {
        return addr;
    }

    public static void setAddr(String addr) {
        Main.addr = addr;
    }
}
