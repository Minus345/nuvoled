package com.nuvoled;

import java.util.Objects;

public class Main {

    private static String addr;

    public static void main(String[] args) {
        for (String arg : args) {
            System.out.println("Parameter: " + arg);
        }

        if (Objects.equals(args[0], "start")) {
            System.out.println("Start");
            addr = args[1];
            new Receiver().run(2000);
            System.out.println("Ready");
            if (args.length > 3) {
                if (Objects.equals(args[2], "color")) {
                    System.out.println("Color modus");
                    int red = Integer.parseInt(args[3]);
                    int green = Integer.parseInt(args[4]);
                    int blue = Integer.parseInt(args[5]);
                    byte redToByte = (byte) red;
                    byte greenToByte = (byte) green;
                    byte blueToByte = (byte) blue;

                    SendColor.send(redToByte, greenToByte, blueToByte);
                }
            }
        }
    }

    public static String getAddr() {
        return addr;
    }

}
