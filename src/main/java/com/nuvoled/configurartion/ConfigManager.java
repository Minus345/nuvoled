package com.nuvoled.configurartion;

import com.nuvoled.Main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class ConfigManager {

    private static ArrayList<Panel> waitingList;
    private static Panel[][] alreadyConfiguredPanelMatrix;
    private static ArrayList<Integer> alreadyConfiguredPanels  = new ArrayList<>();

    public static void start() {
        System.out.println();
        System.out.println("Nuvoled Panel Configurator");

        waitingList = new ArrayList<>();

        SendConfigureMessages.reset();
        SendConfigureMessages.getPanelConnected(1000);

        alreadyConfiguredPanelMatrix = new Panel[Main.getxPanelCount()][Main.getyPanelCount()];

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
        System.out.println("x/y: " + Main.getxPanelCount() + "/" + Main.getyPanelCount());
        System.out.println();

        String a = "---+";
        String b = " ";
        String c = " |";
        String row1 = "+";
        String row2 = "|";

        int x = 1;
        for (int i = 0; i < Main.getyPanelCount(); i++) {
            for (int j = 0; j < Main.getxPanelCount(); j++) {
                row1 = String.join("", row1, a);
                if (alreadyConfiguredPanels.contains(x)) {
                    row2 = String.join("", row2, b, "X", c);
                } else {
                    row2 = String.join("", row2, b, Integer.toString(x), c);
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

    private static void loopOverPanelsInList(){
        Scanner scanner = new Scanner(System.in);
        while (true){
            Panel currant = waitingList.getFirst();
            System.out.println("Selected Mac: " + Arrays.toString(currant.getMac()));
            SendConfigureMessages.sendRedCross(currant.getMac());

            System.out.println("Input position number:");
            String number = scanner.nextLine();
            //Fehlerbehandlung
            currant.setConfigured(true);
            currant.setPosition(Integer.parseInt(number));

            //TODO send config Message

            //TODO add panel to "alreadyConfiguredPanelMatrix"

            waitingList.remove(currant);
            alreadyConfiguredPanels.add(currant.getPosition());
            createCLI();
            System.exit(1);
        }
    }
}
