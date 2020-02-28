package rocknrol.project.chippotto.lib.chip8;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

class Chip8dasm {
    byte[] MEM;
    int gameRomLen;
    String debugFile;
    boolean debugFileAppend;
    boolean preDataBlockFlag;
    boolean dataBlockFlag;
    boolean oddPC;
    short dataBlockEntry;

    Util util;

    Chip8dasm(byte[] MEM, int gameRomLen, String debugFile, boolean debugFileAppend) {
        util = new Util();
        this.gameRomLen = gameRomLen;
        this.MEM = MEM;
        this.debugFile = debugFile;
        this.debugFileAppend = debugFileAppend;
        this.dataBlockFlag = false;
        this.preDataBlockFlag = false;
        this.oddPC = false;
    }

    void dump () {
        String dump = "";

        short i = 0x200;
        int j;
        do {
            dump += util.shortToHex(i) + "   ";
            for (j=0; j < 4; j++)
                dump += util.byteToHex(MEM[i+j]) + " ";
            dump += "  ";
            for (; j < 8; j++)
                dump += util.byteToHex(MEM[i+j]) + " ";
            dump += "  ";
            for (j=0; j < 8; j++)
                if(MEM[i+j] >= 0x21 && MEM[i+j] <= 0x7E)
                    dump += (char) MEM[i+j];
                else
                    dump += ".";
            dump += "\n";
            i += 8;
        } while(i < 0x200 + gameRomLen);

        if (debugFile.equals(""))
            System.out.println(dump);
        else
            writeDebugFile("\n\n" + dump + "\n\n");
    }

    void dasm () {
        short PC = 0x200;
        String assemblyCode = "";

        while(PC < 0x200 + gameRomLen) {
            assemblyCode += buildAssemblyLine(PC);
            dataBlockFlag = preDataBlockFlag;
            if(dataBlockFlag) {
                if(PC == dataBlockEntry-1) {
                    PC += 1;
                    preDataBlockFlag = false;
                    dataBlockFlag = false;
                } else
                    PC += 2;
            } else
                PC += 2;
        }

        if (debugFile.equals(""))
            System.out.println(assemblyCode);
        else
            writeDebugFile("\n\n" + assemblyCode + "\n\n");
    }

    void trace(short PC) {
        String assemblyCode = "";

        assemblyCode = buildAssemblyLine(PC);

        if (debugFile.equals(""))
            System.out.println(assemblyCode);
        else
            writeDebugFile(assemblyCode);
    }

    String buildAssemblyLine(short PC) {
        String assemblyLine = "";

        short opcode = (short) ((MEM[PC] << 8) | MEM[PC + 1] & 0x00FF); //0x00FF is necessary or Java puts FF instead of 00's !!
        byte X = (byte) ((opcode & 0x0F00) >> 8);
        byte Y = (byte) ((opcode & 0x00F0) >> 4);
        byte N = (byte) (opcode & 0x000F);
        byte NN = (byte) (opcode & 0x00FF);
        short NNN = (short) (opcode & 0x0FFF);

        assemblyLine = util.shortToHex(PC) + "  " + util.shortToHex(opcode) + "  ";

        switch(opcode & 0xF000) {
            case 0x0000:
                switch(opcode) {
                    case 0x0000:
                        assemblyLine += "NOP";
                        break;
                    case 0x00E0:
                        assemblyLine += "CLRSCR";
                        break;
                    case 0x00EE:
                        assemblyLine += "RET";
                        break;
                    default:
                        assemblyLine += "Unknown opcode";
                }
                break;
            case 0x1000:
                assemblyLine += "JMP 0" + util.shortToHex(NNN, 3);
                if(!oddPC)
                    if(NNN%2 != 0 && NNN > PC) {
                        preDataBlockFlag = true;
                        dataBlockEntry = NNN;
                        oddPC = true;
                    }
                else
                    if(NNN%2 == 0 && NNN > PC) {
                        preDataBlockFlag = true;
                        dataBlockEntry = NNN;
                        oddPC = false;
                    }
                break;
            case 0x2000:
                assemblyLine += "CALL 0" + util.shortToHex(NNN, 3);
                break;
            case 0x3000:
                assemblyLine += "JE V" + util.byteToHex(X, 1) + "," + util.byteToHex(NN);
                break;
            case 0x4000:
                assemblyLine += "JNE V" + util.byteToHex(X, 1) + "," + util.byteToHex(NN);
                break;
            case 0x5000:
                assemblyLine += "JE V" + util.byteToHex(X, 1) + ",V" + util.byteToHex(Y, 1);
                break;
            case 0x6000:
                assemblyLine += "SET V" + util.byteToHex(X, 1) + "," + util.byteToHex(NN);
                break;
            case 0x7000:
                assemblyLine += "ADD V" + util.byteToHex(X, 1) + "," + util.byteToHex(NN);
                break;
            case 0x8000:
                switch(opcode & 0xF00F) {
                    case 0x8000:
                        assemblyLine += "SET V" + util.byteToHex(X, 1) + ",V" + util.byteToHex(Y, 1);
                        break;
                    case 0x8001:
                        assemblyLine += "OR V" + util.byteToHex(X, 1) + ",V" + util.byteToHex(Y, 1);
                        break;
                    case 0x8002:
                        assemblyLine += "AND V" + util.byteToHex(X, 1) + ",V" + util.byteToHex(Y, 1);
                        break;
                    case 0x8003:
                        assemblyLine += "XOR V" + util.byteToHex(X, 1) + ",V" + util.byteToHex(Y, 1);
                        break;
                    case 0x8004:
                        assemblyLine += "ADD V" + util.byteToHex(X, 1) + ",V" + util.byteToHex(Y, 1);
                        break;
                    case 0x8005:
                        assemblyLine += "SUB V" + util.byteToHex(X, 1) + ",V" + util.byteToHex(Y, 1);
                        break;
                    case 0x8006:
                        assemblyLine += "SHR V" + util.byteToHex(X, 1) + ",V" + util.byteToHex(Y, 1);
                        break;
                    case 0x8007:
                        assemblyLine += "MSUB V" + util.byteToHex(X, 1) + ",V" + util.byteToHex(Y, 1);
                        break;
                    case 0x800E:
                        assemblyLine += "SHL V" + util.byteToHex(X, 1) + ",V" + util.byteToHex(Y, 1);
                        break;
                    default:
                        assemblyLine += "Unknown opcode";
                }
                break;
            case 0x9000:
                assemblyLine += "JNE V" + util.byteToHex(X, 1) + ",V" + util.byteToHex(Y, 1);
                break;
            case 0xA000:
                assemblyLine += "SET I," + util.shortToHex(NNN, 3);
                break;
            case 0xB000:
                assemblyLine += "JMPV 0" + util.shortToHex(NNN, 3);
                break;
            case 0xC000:
                assemblyLine += "RND V" + util.byteToHex(X, 1) + "," + util.byteToHex(NN);
                break;
            case 0xD000:
                assemblyLine += "DRAW V" + util.byteToHex(X, 1) + ",V" + util.byteToHex(Y,1) + "," + util.byteToHex(N, 1);
                break;
            case 0xE000:
                switch(opcode & 0xF0FF) {
                    case 0xE09E:
                        assemblyLine += "JKP V" + util.byteToHex(X, 1);
                        break;
                    case 0xE0A1:
                        assemblyLine += "JKNP V" + util.byteToHex(X, 1);
                        break;
                    default:
                        assemblyLine += "Unknown opcode";
                }
                break;
            case 0xF000:
                switch(opcode & 0xF0FF) {
                    case 0xF007:
                        assemblyLine += "GETDT V" + util.byteToHex(X, 1);
                        break;
                    case 0xF00A:
                        assemblyLine += "WKP V" + util.byteToHex(X, 1);
                        break;
                    case 0xF015:
                        assemblyLine += "SETDT V" + util.byteToHex(X, 1);
                        break;
                    case 0xF018:
                        assemblyLine += "SETST V" + util.byteToHex(X, 1);
                        break;
                    case 0xF01E:
                        assemblyLine += "ADDI V" + util.byteToHex(X, 1);
                        break;
                    case 0xF029:
                        assemblyLine += "FONT V" + util.byteToHex(X, 1);
                        break;
                    case 0xF033:
                        assemblyLine += "BCD V" + util.byteToHex(X, 1);
                        break;
                    case 0xF055:
                        assemblyLine += "SAVE V" + util.byteToHex(X, 1);
                        break;
                    case 0xF065:
                        assemblyLine += "LOAD V" + util.byteToHex(X, 1);
                        break;
                    default:
                        assemblyLine += "Unknown opcode";
                }
                break;
            default:
                assemblyLine += "Unknown opcode";
        }

        assemblyLine += "\n";

        return assemblyLine;
    }

    private void writeDebugFile(String fileContent) {
        try {
            File file = new File(this.debugFile);
            FileOutputStream fos = new FileOutputStream(file, true);
            fos.write(fileContent.getBytes());
            fos.close();
        } catch(IOException ioe) {

        }
    }

    //function not used but very Useful to investigate into Chippotto's stack
    String getStackImage(short BP, short SP) {
        String stack= "\n";
        for(short i=(short)(BP+0x0016); i>=BP; i-=2) {
            stack += util.shortToHex(i) + " " + util.byteToHex(MEM[i]) + util.byteToHex(MEM[i + 1]);
            if(i == SP)
                stack += " <--";
            stack += "\n";
        }
        return stack + "\n";
    }
}
