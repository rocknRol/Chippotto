package rocknrol.project.chippotto.lib.chip8;

import java.io.File;
import java.io.FileInputStream;

public class Chip8System {
    byte[] V = new byte[16]; // cpu register: V0, V1, V2, V3, V4, V5, V6, V7, V8, V9, VA, VB, VC, VD, VE, VF
    short I; // address register
    short PC; // program counter register
    byte[] MEM = new byte[4096]; // ram memory
    byte[] stack = new byte[16]; // Stack array not used. In this implementation the stack is in memory at 0xEA0 address
    short BP; // base stack
    short SP; // stack pointer
    byte delayTimer; // delay timer
    byte soundTimer; // sound timer

    byte X; // V register number
    byte Y; // V register number
    byte N;
    byte NN;
    short NNN;

    int gameRomLen;

    boolean isReadyGfxMen;

    boolean keyPressed;
    byte keyCode;

    Timer timer = new Timer();
    Util util;

    Chip8dasm chip8dasm;
    byte debugSwitch = 0x00;
    String debugFile = "";
    boolean debugFileAppend = false;

    public Chip8System() {
        util = new Util();
        init();
    }

    public void init() {
        PC = 0x200;
        BP = 0xEA0;
        SP = BP;
        keyPressed = false;

        loadFonset();
    }

    public void setDebugInfo(byte debugSwitch, String debugFile, boolean debugFileAppend) { //, dumpFile, String dasmFile, String traceFile) {
        this.debugSwitch = debugSwitch;
        this.debugFile = debugFile;
        this.debugFileAppend = debugFileAppend;
    }

    public int loadRom(String pathname) {
        byte[] gameRom;

        try {
            File file = new File(pathname);
            gameRom = new byte[(int) file.length()];
            FileInputStream fis = new FileInputStream(file);
            fis.read(gameRom);
            fis.close();

            this.gameRomLen = gameRom.length;

            for (int i = 0; i < this.gameRomLen; i++)
                MEM[0x200 + i] = gameRom[i];

        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }

        if((debugSwitch & 0xFF) != 0x00)
            chip8dasm = new Chip8dasm(MEM, gameRomLen, debugFile, debugFileAppend);

        if((debugSwitch & 0x01) == 0x01)
            chip8dasm.dump();

        if((debugSwitch & 0x02) == 0x02)
            chip8dasm.dasm();

        return gameRomLen;
    }

    public void loadFonset() {
        int [] fontset = new int[] {
            0xF0, 0x90, 0x90, 0x90, 0xF0, // 0
            0x20, 0x60, 0x20, 0x20, 0x70, // 1
            0xF0, 0x10, 0xF0, 0x80, 0xF0, // 2
            0xF0, 0x10, 0xF0, 0x10, 0xF0, // 3
            0x90, 0x90, 0xF0, 0x10, 0x10, // 4
            0xF0, 0x80, 0xF0, 0x10, 0xF0, // 5
            0xF0, 0x80, 0xF0, 0x90, 0xF0, // 6
            0xF0, 0x10, 0x20, 0x40, 0x40, // 7
            0xF0, 0x90, 0xF0, 0x90, 0xF0, // 8
            0xF0, 0x90, 0xF0, 0x10, 0xF0, // 9
            0xF0, 0x90, 0xF0, 0x90, 0x90, // A
            0xE0, 0x90, 0xE0, 0x90, 0xE0, // B
            0xF0, 0x80, 0x80, 0x80, 0xF0, // C
            0xE0, 0x90, 0x90, 0x90, 0xE0, // D
            0xF0, 0x80, 0xF0, 0x80, 0xF0, // E
            0xF0, 0x80, 0xF0, 0x80, 0x80  // F
        };

        for(int i = 0; i < 80; i++)
            MEM[0x050 + i] = (byte) fontset[i];
    }

    public  boolean isReadyGfxMen() {
        return isReadyGfxMen;
    }

    public byte[] getGfxMem() {
        byte[] gfxMem = new byte[256];

        for(int i = 0; i < 256; i++)
            gfxMem[i] = MEM[0xF00 + i];

        isReadyGfxMen = false;
        return gfxMem;
    }

    public void setKeyboardValue(byte keyCode) {
        this.keyCode = keyCode;
    }

    public void setKeyboardStatus(boolean keyStatus) {
        this.keyPressed = keyStatus;
    }

    public boolean playSound(){
        if(soundTimer > 0)
            return true;
        else
            return false;
    }

    private void updateTimer() {

        if(timer.isExceedTimer(16)) { // original deltaTime: 16.667 ms --> 60 Hz
            if (delayTimer > 0)
                delayTimer--;

            if (soundTimer > 0)
                soundTimer--;

            timer.startTimer();
            }
    }

    public void cpuStep() {
        short opcode;

        if((debugSwitch & 0x04) == 0x04)
            chip8dasm.trace(PC);

            opcode = fetchOpcode();
            decodeOpcode(opcode);
            executeOpcode(opcode);

            updateTimer();
    }

    private short fetchOpcode() {
        short opcode = (short) ((MEM[PC] << 8) | MEM[PC + 1] & 0x00FF); //0x00FF is necessary or Java puts FF instead of 00's !!
        return opcode;
    }

    private void decodeOpcode(short opcode){
        X = (byte) ((opcode & 0x0F00) >> 8);
        Y = (byte) ((opcode & 0x00F0) >> 4);
        N = (byte) (opcode & 0x000F);
        NN = (byte) (opcode & 0x00FF);
        NNN = (short) (opcode & 0x0FFF);
    }

    private void executeOpcode(short opcode) {
        int hundreds, tens, ones;

        switch(opcode & 0xF000) {
            case 0x0000:
                switch(opcode) {
                    case 0x0000:
                        System.out.println("nop");
                        break;
                    case 0x00E0: //00E0    CLRSCR
                        isReadyGfxMen = true;
                        for(int i = 0; i < 256; i++)
                            MEM[0xF00 + i] = 0;
                            PC += 2;
                        break;
                    case 0x00EE: //00EE    RET
                        SP -= 2;
                        PC = (short)((MEM[SP] << 8) | MEM[SP + 1] & 0x00FF);
                        break;
                    default:
                        System.out.println("Unknown opcode: " + util.shortToHex(opcode));
                        PC += 2;
                }
                break;
            case 0x1000: //1NNN    JMP NNN
                PC = NNN;
                break;
            case 0x2000: //2NNN    CALL NNN
                PC += 2;
                MEM[SP] = (byte)(PC >> 8 & 0xFF);
                MEM[SP+1] = (byte)((PC & 0xFF)) ;
                SP += 2;
                PC = NNN;
                break;
            case 0x3000: //3XNN    JE VX,NN
                if(V[X] == NN)
                    PC += 4;
                else
                    PC += 2;
                break;
            case 0x4000: //4XNN    JNE VX,NN
                if(V[X] != NN)
                    PC += 4;
                else
                    PC += 2;
                break;
            case 0x5000: //5XY0    JE VX,VY
                if(V[X] == V[Y])
                    PC += 4;
                else
                    PC += 2;
                break;
            case 0x6000: //6XNN    SET VX,NN
                V[X] = NN;
                PC += 2;
                break;
            case 0x7000: //7XNN    ADD VX,NN
                V[X] += NN;
                PC += 2;
                break;
            case 0x8000:
                switch(opcode & 0xF00F) {
                    case 0x8000: //8XY0    SET VX,VY
                        V[X] = V[Y];
                        PC += 2;
                        break;
                    case 0x8001: //8XY1    OR VX,VY
                        V[X] |= V[Y];
                        PC += 2;
                        break;
                    case 0x8002: //8XY2    AND VX,VY
                        V[X] &= V[Y];
                        PC += 2;
                        break;
                    case 0x8003: //8XY3    XOR VX,VY
                        V[X] ^= V[Y];
                        PC += 2;
                        break;
                    case 0x8004: //8XY4    ADD VX,VY
                        byte tmpAdd = V[X];
                        V[X] += V[Y];
                        if((V[X] & 0xFF) < (tmpAdd & 0xFF))
                            V[15] = 1;
                        else
                            V[15] = 0;
                        PC += 2;
                        break;
                    case 0x8005: //8XY5    SUB VX,VY
                        byte tmpSub = V[X];
                        V[X] -= V[Y];
                        if((V[X] & 0xFF) > (tmpSub & 0xFF))
                            V[15] = 0;
                        else
                            V[15] = 1;
                        PC += 2;
                        break;
                    case 0x8006: //8XY6    SHR VX,VY
                        V[15] = (byte) ((V[X] & 0x01));
                        V[X] >>= 1;
                        PC += 2;
                        break;
                    case 0x8007: //8XY7    MSUB VX,VY (mirror sub)
                        V[X] = (byte) (V[Y] - V[X]);
                        if(V[X] < 0)
                            V[15] = 0;
                        else
                            V[15] = 1;
                        PC += 2;
                        break;
                    case 0x800E: //8XYE    SHL VX,VY
                        V[15] = (byte) ((V[X] & 0x80) >> 7);
                        V[X] <<= 1;
                        PC += 2;
                        break;
                    default:
                        System.out.println("Unknown opcode + pc: " + util.shortToHex(opcode));
                        PC += 2;
                }
                break;
            case 0x9000: //9XY0    JNE VX,VY
                if(V[X] != V[Y])
                    PC += 4;
                else
                    PC += 2;
                break;
            case 0xA000: //ANNN    SET I,NNN
                I = NNN;
                PC += 2;
                break;
            case 0xB000: //BNNN    JMPV NNN
                PC = (short) (NNN + V[0]);
                break;
            case 0xC000: //CXNN    RND VX,NN
                int rnd = (int) (Math.random() * 256);
                V[X] = (byte) rnd;
                V[X] &= NN;
                PC += 2;
                break;
            case 0xD000: //DXYN    DRAW VX,VY,N
                int offset, x_offset, y_offset;
                isReadyGfxMen = true;
                V[15] = 0x00;
                for(int n = 0; n < N; n++) {
                    for(int i = 0; i < 8; i++) {

                        V[X] = V[X] < 0 ? (byte) (64 + V[X]) : V[X];
                        x_offset = (V[X] + i) % 64;

                        V[Y] = V[Y] < 0 ? (byte) (32 + V[Y]) : V[Y];
                        y_offset = ((V[Y] + n) % 33) * 64;

                        offset = x_offset + y_offset;
                        int nbyte = offset / 8;
                        int nbit = (7 - (offset % 8));

                        if (((MEM[I + n] >> (7-i)) & 0x01) == 1)
                            if (0xF00 + nbyte < 4096){
                                if(((MEM[0xF00 + nbyte] >> nbit) & 0x01) == 1) {
                                   V[15] = 0x01;
                                    MEM[0xF00 + nbyte] = (byte) (MEM[0xF00 + nbyte] & ~(1 << nbit));
                                }
                                else
                                    MEM[0xF00 + nbyte] = (byte) (MEM[0xF00 + nbyte] | (1 << nbit));
                                }
                    }
                }
                PC += 2;
                break;
            case 0xE000:
                switch(opcode & 0xF0FF) {
                    case 0xE09E: //EX9E    JKP VX
                        if(keyPressed) {
                            if(V[X] == keyCode)
                                PC += 2;
                        }
                        PC += 2;
                        break;
                    case 0xE0A1: //EXA1    JKNP VX
                        if(keyPressed) {
                            if(V[X] == keyCode)
                                PC += 2;
                            else
                                PC += 4;
                        }
                        else
                            PC += 4;
                        break;
                    default:
                        System.out.println("Unknown opcode: " + util.shortToHex(opcode));
                        PC += 2;
                }
                break;
            case 0xF000:
                switch(opcode & 0xF0FF) {
                    case 0xF007: //FX07    GETDT VX (get delay timer)
                        V[X] = delayTimer;
                        PC += 2;
                        break;
                    case 0xF00A: //FX0A    WKP VX (wait key press)
                        if(keyPressed) {
                            V[X] = keyCode;
                            PC += 2;
                        }
                        break;
                    case 0xF015: //FX15    SETDT VX (set delay timer)
                        delayTimer = V[X];
                        timer.startTimer();
                        PC += 2;
                        break;
                    case 0xF018: //FX18    SETST VX (set sound timer)
                        soundTimer = V[X];
                        timer.startTimer();
                        PC += 2;
                        break;
                    case 0xF01E: //FX1E    ADDI VX
                        I += V[X];
                        if(I > 0x0FFF) //undocumented feature
                            V[0x0f]=1;
                        PC += 2;
                        break;
                    case 0xF029: //FX29    FONT VX
                        I = (short) (0x050 + (V[X] * 5));
                        PC += 2;
                        break;
                    case 0xF033: //FX33    BCD VX (binary-coded decimal)
                        hundreds = (int) V[X]  / 100;
                        tens = ((V[X]  - (hundreds*100)) / 10);
                        ones = ((V[X]  - (tens*10) - (hundreds*100)));
                        MEM[I] = (byte) hundreds;
                        MEM[I + 1] = (byte) tens;
                        MEM[I + 2] = (byte) ones;
                        PC += 2;
                        break;
                    case 0xF055: //FX55    SAVEV
                        for(int i=0; i<=X; i++)
                            MEM[I+i] = V[i];
                        PC += 2;
                        break;
                    case 0xF065: //FX65    LOADV
                        for(int i=0; i<=X; i++)
                            V[i] = MEM[I+i];
                        PC += 2;
                        break;
                    default:
                        System.out.println("Unknown opcode: " + util.shortToHex(opcode));
                        PC += 2;
                }
                break;
            default:
                System.out.println("Unknown opcode: " + util.shortToHex(opcode));
                PC += 2;
        }
    }
}
