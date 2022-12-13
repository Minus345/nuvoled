package com.nuvoled;

import ch.bildspur.artnet.ArtNetClient;
import com.nuvoled.sender.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramSocket;
import java.util.Objects;

public class Main {

    private static String broadcastIpAddress;
    private static int port;
    private static byte courantFrame;
    private static int panelSizeX;
    private static int panelSizeY;
    private static boolean rotation;

    private static ArtNetClient artnet;

    public static void main(String[] args) throws IOException, AWTException, InterruptedException {
        for (String arg : args) {
            System.out.println("Parameter: " + arg);
        }

        if (!Objects.equals(args[0], "start")) {
            System.out.println("Falsches Argumt");
            return;
        }

        System.out.println("Start");
        courantFrame = 2; //set default values
        panelSizeX = 128;
        panelSizeY = 128;
        port = 2000;
        broadcastIpAddress = args[1];
        rotation = false;

        panelSizeX = Integer.parseInt(args[2]) * panelSizeX; //Anzahl Panel X * 128 pixel
        panelSizeY = Integer.parseInt(args[3]) * panelSizeY; //Anzahl Panel Y * 128 pixel

        System.out.println("x/y " + panelSizeX + "/" + panelSizeY);
        System.out.println("rotation " + isRotation());

        artnet = new ArtNetClient();
        artnet.start("192.168.178.178");
        SendSync.setDatagramSocket();
        screenAndVideo();
    }

    public static void screenAndVideo() throws InterruptedException {
        while (true) {
           // Thread.sleep(1000);
            PictureSender.send();

        }
    }

    public static String getBroadcastIpAddress() {
        return broadcastIpAddress;
    }

    public static byte getCourantFrame() {
        return courantFrame;
    }

    public static void setCourantFrame(byte courantFrame) {
        Main.courantFrame = courantFrame;
    }

    public static int getPort() {
        return port;
    }

    public static int getPanelSizeX() {
        return panelSizeX;
    }

    public static int getPanelSizeY() {
        return panelSizeY;
    }

    public static boolean isRotation() {
        return rotation;
    }

    public static ArtNetClient getArtnet() {
        return artnet;
    }
}
