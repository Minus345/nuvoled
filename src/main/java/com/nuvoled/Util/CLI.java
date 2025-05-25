package com.nuvoled.Util;

import ch.bildspur.artnet.ArtNetClient;
import com.nuvoled.Main;
import org.apache.commons.cli.*;

public class CLI {
    public static void commandLineParameters(String[] args) {
        var options = new Options()
                .addOption("h", "help", false, "Help Message")
                .addOption("b", "bind", false, "bind to interface 169.254")
                .addOption("ad", "artnetDebug", false, "enables artnet debug")
                .addOption("p", "Panel", true, "choose Panel")
                .addOption("rgb", "rgb565", false, "sets the mode to rgb565")
                .addOption("fps", "fps", false, "prints out fps")
                .addOption("ndi", "ndi", false, "enables ndi mode")
                .addOption(Option.builder("px")
                        .longOpt("panelsx")
                        .hasArg(true)
                        .desc("Number of Panels horizontal ")
                        .argName("1")
                        .build())
                .addOption(Option.builder("py")
                        .longOpt("panelsy")
                        .hasArg(true)
                        .desc("Number of Panels vertical ")
                        .argName("1")
                        .build())
                .addOption(Option.builder("sx")
                        .longOpt("startx")
                        .hasArg(true)
                        .desc("Pixel start horizontal ")
                        .argName("0")
                        .build())
                .addOption(Option.builder("sy")
                        .longOpt("starty")
                        .hasArg(true)
                        .desc("Pixal start vertical ")
                        .argName("0")
                        .build())
                .addOption(Option.builder("r")
                        .longOpt("rotation")
                        .hasArg(true)
                        .desc("rotation degree 0/90/180/270 ")
                        .argName("0")
                        .build())
                .addOption(Option.builder("br")
                        .longOpt("brightness")
                        .hasArg(true)
                        .desc("brightness value with 0.x -1.x")
                        .argName("0.6")
                        .build())
                .addOption(Option.builder("sn")
                        .longOpt("screennr")
                        .hasArg(true)
                        .desc("number of screen")
                        .argName("0")
                        .build())
                .addOption(Option.builder("s")
                        .longOpt("sleep")
                        .hasArg(true)
                        .desc("sleep ime in ms")
                        .argName("0")
                        .build())
                .addOption(Option.builder("o")
                        .longOpt("offset")
                        .hasArg(true)
                        .desc("offset (Contrast) ")
                        .argName("0")
                        .build())
                .addOption(Option.builder("a")
                        .longOpt("artnet")
                        .hasArg(true)
                        .desc("enables artnet")
                        .argName("<ip>")
                        .build())
                .addOption(Option.builder("as")
                        .longOpt("artnetSubnet")
                        .hasArg(true)
                        .desc("artnet subnet")
                        .argName("< 0 - 16 >")
                        .build())
                .addOption(Option.builder("au")
                        .longOpt("artnetUniverse")
                        .hasArg(true)
                        .desc("artnet universe")
                        .argName("< 0 - 16 >")
                        .build())
                .addOption(Option.builder("ac")
                        .longOpt("artnetChannel")
                        .hasArg(true)
                        .desc("artnet channel")
                        .argName("< 0 - 513 >")
                        .build());

        CommandLineParser parser = new DefaultParser();
        CommandLine line;
        try {
            // parse the command line arguments
            line = parser.parse(options, args);
            if (line.hasOption("b")) {
                Main.setBindToInterface(true);
            }
            if (line.hasOption("h")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("java -jar nuvoled.jar ", options);
                Main.setBindToInterface(true);
                System.exit(0);
            }
            if (line.hasOption("px")) {
                Main.setxPanelCount(Integer.parseInt(line.getOptionValue("px")));
            }
            if (line.hasOption("py")) {
                Main.setyPanelCount(Integer.parseInt(line.getOptionValue("py")));
            }
            if (line.hasOption("sx")) {
                Main.setxPosition(Integer.parseInt(line.getOptionValue("sx")));
            }
            if (line.hasOption("sy")) {
                Main.setyPosition(Integer.parseInt(line.getOptionValue("sy")));
            }
            if (line.hasOption("r")) {
                Main.setRotation(Integer.parseInt(line.getOptionValue("r")));
            }
            if (line.hasOption("br")) {
                Main.setScaleFactor(Float.parseFloat(line.getOptionValue("br")));
            }
            if (line.hasOption("sn")) {
                Main.setScreenNumber(Integer.parseInt(line.getOptionValue("sn")));
            }
            if (line.hasOption("s")) {
                Main.setSleep(Integer.parseInt(line.getOptionValue("s")));
            }
            if (line.hasOption("o")) {
                Main.setOffSet(Float.valueOf(line.getOptionValue("s")));
            }
            if (line.hasOption("ad")) {
                System.out.println("ad");
            }
            if (line.hasOption("a")) {
                System.out.println("Starting Artnet");
                Main.setArtnet(new ArtNetClient());
                Main.getArtnet().start(line.getOptionValue("a"));
                Main.setArtnetEnabled(true);
                if (line.hasOption("as")) Main.setSubnet(Integer.parseInt(line.getOptionValue("as")));
                if (line.hasOption("au")) Main.setUniversum(Integer.parseInt(line.getOptionValue("au")));
                if (line.hasOption("ac")) Main.setChannel(Integer.parseInt(line.getOptionValue("ac")));
                if (line.hasOption("ad")) Main.setArtnetDebug(true);
                System.out.println("Subnet: " + Main.getSubnet());
                System.out.println("Universe: " + Main.getUniversum());
                System.out.println("Channel: " + Main.getChannel());
                System.out.println("Debug: " + Main.isArtnetDebug());
            }
            if (line.hasOption("p")) {
                Main.setWichPanel(String.valueOf(line.getOptionValue("p")));
            }
            if (line.hasOption("rgb565")) {
                Main.setColorMode(30);
            }
            if (line.hasOption("fps")) {
                Main.setShowFps(true);
            }
            if (line.hasOption("ndi")) {
                Main.setMode("ndi");
            }
        } catch (ParseException exp) {
            // oops, something went wrong
            System.err.println("Parsing failed.  Reason: " + exp.getMessage());
        }
    }
}
