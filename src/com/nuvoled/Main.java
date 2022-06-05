package com.nuvoled;

import java.util.Objects;

public class Main {

    public static void main(String[] args) {

        for(int i = 0; i < args.length; i++) {
            System.out.println("Parameter: " + args[i]);
        }

        if (Objects.equals(args[0], "start")) {
            System.out.println("Start");
            //new Receiver().run(2000);
            for (int i = 0; i < Receiver.mac.length; i++) {
                System.out.print(Receiver.mac[i] + " ");
            }
        }
    }
}
