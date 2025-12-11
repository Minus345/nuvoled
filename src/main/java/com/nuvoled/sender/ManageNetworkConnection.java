package com.nuvoled.sender;

import com.nuvoled.Main;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Scanner;

public class ManageNetworkConnection {
    private DatagramSocket datagramSocket;
    private final int port;
    private final String broadcastIpaddr;

    public ManageNetworkConnection(int port, String broadcastIpaddr) {
        this.port = port;
        this.broadcastIpaddr = broadcastIpaddr;
    }

    /**
     * creates DatagrammSocket for broadcasting out messages
     */
    public void setDatagramSocket() {
        try {
            InetAddress address = findNetworkInterface();
            datagramSocket = new DatagramSocket(port, address); //,InetAddress.getByName("255.255.255.255")
            System.out.println(datagramSocket.getLocalSocketAddress().toString());
            datagramSocket.setBroadcast(true);
        } catch (BindException e) {
            System.out.println("Address already in use. Another application is already running on the same network card");
            System.exit(-1);
        } catch (SocketException e) {
            System.out.println("Could not bind to Network Card");
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * Set up the datagram socket for sending and receiving.
     * It will only bind to the port, because the panels send udp broadcast wich we have to receive from anywhere
     */
    public void setDatagramSocketForListeningAndSending(int timeOut) {
        try {
            datagramSocket = new DatagramSocket(port);
            System.out.println(datagramSocket.getLocalSocketAddress().toString());
            datagramSocket.setBroadcast(true);
            datagramSocket.setSoTimeout(timeOut);
        } catch (BindException e) {
            System.out.println("Address already in use. Another application is already running on the same network card");
            System.exit(-1);
        } catch (SocketException e) {
            System.out.println("Could not bind to Network Card");
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * finds the network interface with address: "169.254.255.255"
     *
     * @return
     */
    private InetAddress findNetworkInterface() {
        ArrayList<NetworkInterface> suitableInterfaces = new ArrayList<>();
        try {
            System.out.println("Full list of Network Interfaces:");
            for (Enumeration<NetworkInterface> networkInterfaceEnumeration = NetworkInterface.getNetworkInterfaces(); networkInterfaceEnumeration.hasMoreElements(); ) {
                NetworkInterface networkInterface = networkInterfaceEnumeration.nextElement();
                System.out.println("    " + networkInterface.getName() + " " + networkInterface.getDisplayName());
                for (Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses(); inetAddresses.hasMoreElements(); ) {//inetAddresses -> Ip4/Ip6
                    InetAddress currInetAddress = inetAddresses.nextElement();
                    String currInetAddressString = currInetAddress.toString();
                    System.out.println("        " + currInetAddressString);
                    if (currInetAddressString.startsWith("/169.254")) {
                        suitableInterfaces.add(networkInterface);
                    }
                }
            }
        } catch (SocketException e) {
            System.out.println("Error retrieving network interface list");
            System.exit(-1);
        }

        if (suitableInterfaces.isEmpty()) {
            System.out.println("No suitable Network Interfaces found");
            System.exit(1);
        }

        if (suitableInterfaces.size() == 1) {
            return loopOverInterface(suitableInterfaces.getFirst());
        }

        System.out.println("More suitable Network Interfaces found: ");

        for (int i = 0; i < suitableInterfaces.size(); i++) {
            System.out.println("[" + i + "] : " + suitableInterfaces.get(i).getName() + " " + suitableInterfaces.get(i).getDisplayName());
        }

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter Number:");
        String line = scanner.nextLine();
        int interfaceNumber = 0;
        try {
            interfaceNumber = Integer.parseInt(line);
        } catch (NumberFormatException e) {
            System.out.println("Usage: this is not a number");
            System.exit(1);
        }

        if (interfaceNumber < 0 || interfaceNumber > (suitableInterfaces.size() - 1)) {
            System.out.println("Usage: number not in range");
            System.exit(1);
        }

        return loopOverInterface(suitableInterfaces.get(interfaceNumber));
    }

    private InetAddress loopOverInterface(NetworkInterface networkInterface) {
        for (Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses(); inetAddresses.hasMoreElements(); ) {
            InetAddress currInetAddress = inetAddresses.nextElement();
            String currInetAddressString = currInetAddress.toString();
            if (currInetAddressString.startsWith("/169.254")) {
                System.out.println("==>> Binding to this adapter..." + currInetAddressString + "\n");
                return currInetAddress;
            }
        }
        System.out.println("Could´nt find 169.254.xxx.xxx on your specified network interface");
        System.exit(1);
        return null;
    }

    public void send_data(byte[] message) {
        try {
            DatagramPacket packet = new DatagramPacket(message, message.length, InetAddress.getByName(broadcastIpaddr), port);
            datagramSocket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //TODO: make Main.CurrantFram hier raus

    /**
     * sends the Syncro message after all pixels of the frame were send out
     */
    public void sendSyncro() {
        try {
            int port = 2000;
            byte[] FrameFinish = new byte[4];
            FrameFinish[0] = 36;
            FrameFinish[1] = 36;
            FrameFinish[2] = 100;
            FrameFinish[3] = (byte) (Main.getCourantFrame() - 1); //currant frame

            DatagramPacket packet = new DatagramPacket(FrameFinish, FrameFinish.length, InetAddress.getByName(broadcastIpaddr), port);
            datagramSocket.send(packet);

            Main.setCourantFrame((byte) (Main.getCourantFrame() + 1));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (Main.getCourantFrame() == (byte) 255) {
            //resets frame counter
            Main.setCourantFrame((byte) 0);
        }
    }

    public void closeSocket() {
        datagramSocket.close();
    }

    public DatagramSocket getDatagramSocket() {
        return datagramSocket;
    }
}