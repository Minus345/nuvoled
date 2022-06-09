package com.nuvoled.sender;

import com.nuvoled.Main;

import java.awt.image.BufferedImage;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class PictureSender {
    public static void send(BufferedImage image){
        //BufferedImage image = ImageIO.read(new File(Path)); // "C:\\Users\\Tina\\Documents\\green.jpg"
        if (image.getHeight() < 128 || image.getWidth() < 128) {
            System.out.println("Falsches Format");
            System.out.println("Bitte Format von mindestens 128 * 128 Pixeln verwenden");
            System.exit(0);
        }
        int pannelGroese = 128;
        pannelGroese++; //wegen 0
        byte[] rgb = new byte[pannelGroese * pannelGroese * 3];// 128*128*3
        int rgbCounterNumber = 0;
        for (int y = 0; y <= 128; y++) {
            for (int x = 0; x <= 128; x++) {
                int pixel = image.getRGB(x, y);
                int red = (pixel >> 16) & 0xff;
                int green = (pixel >> 8) & 0xff;
                int blue = (pixel) & 0xff;
                rgb[rgbCounterNumber] = (byte) red;
                rgbCounterNumber++;
                rgb[rgbCounterNumber] = (byte) green;
                rgbCounterNumber++;
                rgb[rgbCounterNumber] = (byte) blue;
                rgbCounterNumber++;
                //System.out.println("x: " + x + " " + "y: " + y + " | " + "red: " + red + ", green: " + green + ", blue: " + blue + " | " + rgbCounterNumber + " | " + rgbCounterNumber / 3);
            }
        }

        try {
            int port = 2000;
            for (int counter = 0; counter <= 35; counter++) {
                byte[] message = new byte[1450];
                message[0] = 36;
                message[1] = 36;
                message[2] = 20;
                message[3] = Main.getCourantFrame();
                message[4] = 10; //RGB
                message[5] = 0;
                message[6] = (byte) counter; //counter
                message[7] = 0;
                message[8] = 35;
                message[9] = 45;

                for (int i = 1; i < 1440; i = i + 3) {
                    message[9 + i] = rgb[i - 1];
                    message[9 + 1 + i] = rgb[i];
                    message[9 + 2 + i] = rgb[1 + i];
                }

                // for (byte b : message) {
                //   System.out.print((b & 0xff) + " ");
                //}

                InetAddress address = InetAddress.getByName(Main.getAddr());
                DatagramSocket datagramSocket = new DatagramSocket();
                DatagramPacket packet = new DatagramPacket(message, message.length, address, port);
                datagramSocket.send(packet);
                //datagramSocket.close();
                //Thread.sleep(10);
            }
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}
