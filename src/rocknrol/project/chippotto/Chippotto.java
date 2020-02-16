package rocknrol.project.chippotto;

import java.awt.*;
import java.lang.reflect.Field;

import org.apache.commons.cli.*;

public class Chippotto {

    public static void main(String[] args) {
        SessionDataStruct sds = new SessionDataStruct();

        CommandLine cmd;

        Options options = new Options();

        Option optIpsWish = new Option("ips", true, "Instructions Per Second (default value: 250)");
        optIpsWish.setRequired(false);
        options.addOption(optIpsWish);

        Option optPxColor = new Option("pxcolor", true, "Pixel Color (default value: GREEN)");
        optPxColor.setRequired(false);
        options.addOption(optPxColor);

        Option optPixelSize = new Option("pxsize", true, "Pixel Size (default value: 8)");
        optPixelSize.setRequired(false);
        options.addOption(optPixelSize);

        Option optBgColor = new Option("bgcolor", true, "Background Color (default value: BLACK)");
        optBgColor.setRequired(false);
        options.addOption(optBgColor);

        Option optFullscreen = new Option("fullscreen", false, "Fullscreen mode (ignored option pxsize)");
        optFullscreen.setRequired(false);
        options.addOption(optFullscreen);

        Option optDump = new Option("dump", false, "ROM dump");
        optDump.setRequired(false);
        options.addOption(optDump);

        Option optDasm = new Option("dasm", false, "ROM disassembler");
        optDasm.setRequired(false);
        options.addOption(optDasm);

        Option optTrace = new Option("trace", false, "Trace code execution ");
        optTrace.setRequired(false);
        options.addOption(optTrace);

        Option optDebugFile = new Option("debugfile", true, "Debug file for dump/disassembler/trace option");
        optDebugFile.setRequired(false);
        options.addOption(optDebugFile);

        Option optDebugFileAppend = new Option("ow", false, "Overwrite option for debug file");
        optDebugFileAppend.setRequired(false);
        options.addOption(optDebugFileAppend);

        Option optFreqBeep = new Option("hz", true, "Frequency for beeping sound");
        optFreqBeep.setRequired(false);
        options.addOption(optFreqBeep);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();

        String header = "Chip-8 emulator with debug support and some funny options\n\n";
        String footer = "\nPlease report issues or suggests at rocknRol76dev@gmail.com";

        try {
            cmd = parser.parse(options, args);
            sds.rom = cmd.getArgs()[cmd.getArgs().length-1];
        } catch (ParseException | ArrayIndexOutOfBoundsException e) {
            formatter.printHelp("chippotto", header, options, footer, true);
            System.exit(-1);
            return;
        }

        if(cmd.hasOption("ips")) {
            sds.ipsWish = Integer.parseInt(cmd.getOptionValue("ips"));
            if (sds.ipsWish <= 0)
                sds.ipsWish = 250;
        }

        if(cmd.hasOption("pxcolor"))
            try {
                Field field = Color.class.getField(cmd.getOptionValue("pxcolor"));
                sds.pixelColor = (Color) field.get(null);
            } catch (Exception e) {
                sds.pixelColor = Color.GREEN;
            }

        if(cmd.hasOption("pxsize")) {
            sds.pixelSize = Integer.parseInt(cmd.getOptionValue("pxsize"));
            if (sds.pixelSize <= 0)
                sds.pixelSize = 8;
        }

        if(cmd.hasOption("bgcolor"))
            try {
                Field field = Color.class.getField(cmd.getOptionValue("bgcolor"));
                sds.bgColor = (Color) field.get(null);
            } catch (Exception e) {
                sds.bgColor = Color.BLACK;
            }

        if(cmd.hasOption("fullscreen"))
            sds.fullscreen = true;

        if(cmd.hasOption("dump"))
                sds.debugSwitch |= 0x01;

        if(cmd.hasOption("dasm"))
            sds.debugSwitch |= 0x02;

        if(cmd.hasOption("trace"))
            sds.debugSwitch |= 0x04;

        if(cmd.hasOption("debugfile"))
            try {
                sds.debugFile = cmd.getOptionValue("debugfile");
            } catch (Exception e) {
                sds.debugFile = "";
                System.out.println("debugfile error");
                }

        if(cmd.hasOption("optDebugFileAppend"))
            sds.DebugFileAppend = true;

        if(cmd.hasOption("hz")) {
            sds.Hz = Double.parseDouble(cmd.getOptionValue("hz"));
            if (sds.Hz < 0)
                sds.Hz = 330.0;
        }

        ChippottoFrame cf = new ChippottoFrame(sds);
    }
}