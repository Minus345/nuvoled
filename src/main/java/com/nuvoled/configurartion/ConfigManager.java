package com.nuvoled.configurartion;

import com.nuvoled.Main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class ConfigManager {

    private static ArrayList<Panel> waitingList;
    private static Storage storage;
    @SuppressWarnings("FieldMayBeFinal")
    private static ArrayList<Integer> alreadyConfiguredPanelsForCLI = new ArrayList<>();

    public static void start() {
        System.out.println();
        System.out.println("Nuvoled Panel Configurator");

        waitingList = new ArrayList<>();

        SendConfigureMessages.reset();
        SendConfigureMessages.getPanelConnected(1000);

        storage = new Storage();

        createCLI();
        loopOverPanelsInList();
    }

    public static void waitingListAdd(Panel panel) {
        waitingList.add(panel);
    }

    /**
     * if panel is already in List the function returns true, otherwise false
     *
     * @param panel
     * @return
     */
    public static boolean lookIfAlreadyInList(Panel panel) {
        for (Panel panelLoop : waitingList) {
            if (Arrays.equals(panelLoop.getMac(), panel.getMac())) {
                return true;
            }
        }
        return false;
    }

    private static void createCLI() {
        System.out.println();
        System.out.println("--------[CLI]--------");
        System.out.println("Global Config x/y: " + Main.getxPanelCount() + "/" + Main.getyPanelCount());
        System.out.println();

        String a = "----+";
        String b = " ";
        String c = "  |";
        String d = " |";
        String row1 = "+";
        String row2 = "|";

        int x = 1;
        for (int i = 0; i < Main.getyPanelCount(); i++) {
            for (int j = 0; j < Main.getxPanelCount(); j++) {
                row1 = String.join("", row1, a);
                if (alreadyConfiguredPanelsForCLI.contains(x)) {
                    row2 = String.join("", row2, b, "X", c);
                } else {
                    // to display two-digit numbers correctly
                    if (x > 9) {
                        row2 = String.join("", row2, b, Integer.toString(x), d);
                    } else {
                        row2 = String.join("", row2, b, Integer.toString(x), c);
                    }
                }
                x++;
            }
            System.out.println(row1);
            System.out.println(row2);
            row1 = "+";
            row2 = "|";
        }
        for (int j = 0; j < Main.getxPanelCount(); j++) {
            row1 = String.join("", row1, a);
        }
        System.out.println(row1);

    }

    private static void loopOverPanelsInList() {
        Scanner scanner = new Scanner(System.in);
        //noinspection InfiniteLoopStatement
        while (true) {
            userInput(scanner);
        }
    }

    private static void configureOnePanel(Scanner scanner) {
        Panel currant = waitingList.getFirst();
        System.out.println("Selected Mac: " + Arrays.toString(currant.getMac()));
        SendConfigureMessages.sendRedCross(currant.getMac());

        System.out.println("Input position number:");
        String line = scanner.nextLine();
        int numberInt;
        try {
            numberInt = Integer.parseInt(line);
        } catch (NumberFormatException nfe) {
            wrongInput();
            return;
        }
        currant.setConfigured(true);
        currant.setPosition(numberInt);

        int panelOffsetX = 0;
        int panelOffsetY = 0;

        //set the panel objekt into the right row/collum in the 2D array
        int x = 1;
        for (int i = 0; i < Main.getyPanelCount(); i++) {
            for (int j = 0; j < Main.getxPanelCount(); j++) {
                if (numberInt == x) {
                    storage.getAlreadyConfiguredPanelMatrix()[j][i] = currant; // row / collum
                    panelOffsetX = j;
                    panelOffsetY = i;
                }
                x++;
            }
        }

        currant.setOffsetX(panelOffsetX * Main.getOnePanelSizeX());
        currant.setOffsetY(panelOffsetY * Main.getOnePanelSizeY());
        // send config Message
        SendConfigureMessages.makeConfigAndSendGreenCross(currant);

        //cli
        alreadyConfiguredPanelsForCLI.add(currant.getPosition());
        createCLI();
        System.out.println("Offset Panel        : " + panelOffsetX + " / " + panelOffsetY);

        waitingList.remove(currant);
    }

    /**
     * All user Input here
     *
     * @param scanner
     * @return
     */

    private static void userInput(Scanner scanner) {
        System.out.println("Input: \"r\" - refresh, \"a\" - apply Configuration, \"e\" - exit, \"n\" - next, \"s\" - save");
        String line = scanner.nextLine();
        switch (line) {
            case "r" -> {
                System.out.println("Refresh");
                SendConfigureMessages.refresh();
                SendConfigureMessages.getPanelConnected(1000);
            }
            case "a" -> {
                System.out.println("Apply Configurations");
                SendConfigureMessages.sendGlobalConfigMessage(storage.getAlreadyConfiguredPanelMatrix());
            }
            case "e" -> {
                System.out.println("Exit");
                System.exit(0);
            }
            case "n" -> {
                System.out.println("Next");
                if (waitingList.isEmpty()) {
                    System.out.println("No more panels in queue");
                    userInput(scanner);
                } else {
                    configureOnePanel(scanner);
                }

            }
            case "s" -> {
                System.out.println("Write config form panels to file");
                System.out.println("Input Path:");
                PanelConfigFileManager.write(scanner.nextLine());
            }
            default -> {
                wrongInput();
                userInput(scanner);
            }
        }
    }

    private static void wrongInput() {
        System.out.println("Wrong Input");
    }

    public static Storage getStorage() {
        return storage;
    }
}
