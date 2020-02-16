# **CHIPPOTTO**

Yet another emulator but...let's start from the meaning of the name: Chippotto is an "italianism" of the term "Chip-8" where the number "eight" in italian language became "otto".


### What is and what does Chippotto

Chippotto is an Chip-8 system emulator which allows you to play all its games.
You can decide to run game in several screen size from the smallest to the fullscreen mode. Moreover you can choose the color of the pixels and the color of the background screen.
An interesting feature of Chippotto is the debug system that allow you to use three different functions:

1. **Dump**: ROM dump
2. **Dasm**: Disassember (see below for details)
3. **Trace**: trace the code execution

The output of these functions is the standard output but you can direct all data in a file.

### How to use Chippotto

Chippotto is a command line Java application witch the follow options:
    
	chippotto [-bgcolor <arg>] [-dasm] [-debugfile <arg>] [-dump] [-fullscreen] [-hz <arg>] [-ips <arg>] [-ow] [-pxcolor <arg>] [-pxsize <arg>] [-trace] <rom_file>

-- **bgcolor** <*arg*>: Background Color (default value: BLACK)
-- **dasm**: ROM disassembler
-- **debugfile** <*arg*>: Debug file for dump/disassembler/trace option
-- **dump**: ROM dump
-- **fullscreen**: Fullscreen mode (ignored option pxsize)
-- **hz** <*arg*>: Frequency for beeping sound (default value: 330 Hz)
-- **ips** <*arg*>: Instructions Per Second (default value: 250)
-- **ow**: Overwrite option for debug file
-- **pxcolor** <*arg*>: Pixel Color (default value: GREEN)
-- **pxsize** <*arg*>: Pixel Size (default value: 8)
-- **trace**: Trace code execution

examples of use:
    
        java -jar chippotto.jar -pxcolor magenta -pxsize 4 INVADERS
        
        java -jar chippotto.jar -bgcolor white -pxcolor black -fullscreen BRIX
        
        java -jar chippotto.jar -dump -dasm -trace -debugfile /tmp/debug.log TICTAC

To exit of the application press the button ESC.
The layout of the Chippotto's keyboard is the following

Original Chip-8 keyboard layout:

        1	2	3	C
        4	5	6	D
        7	8	9	E
        A	0	B	F

Chippotto keyboard layout:
    
        1   2   3   4
        q   w   e   r
        a   s   d   f
        z   x   c   v


### Games ROM emulated

Chippotto emulator has been tested using these ROMs:

- IBM
- MAZE
- GUESS
- TICTAC
- KALEID
- HIDDEN
- INVADERS
- TANK
- UFO
- BRIX
- VBRIX
- PONG
- PONG2
- WIPEOFF
- MERLIN
- SYZYGY
- MISSILE
- PUZZLE
- CONNECT4
- VERS
- 15PUZZLE
- TETRIS
- TRON
- BLITZ


### Notes on Chip-8 and its assembly
Chip-8 system is an ancestor of the current virtual machines, it interprets the Chip-8 programming language based on opcode concept like a common assembly language. It was developed by Joseph Weisbecker and showed at world in an article published on BYTE magazine in december 1978 (volume 3, number 12). The Chip-8 system was used on the COSMAC VIP and Telmac 1800 that was 8-bit microcomputers of RCA manufacturer. The goal was to simplify video games development. Nowadays Chip-8 is been implemented in a multitude of systems hardware and software.
Now I bring back to You some notes about Chip-8 system.


#### CPU

The CPU has 35 opcodes whose length is 2 bytes-
 The cpu register are 16 whose length is 1 byte: V0, V1, V2, V3, V4, V5, V6, V7, V8, V9, VA, VB, VC, VD, VE, VF
VF register is used for:

- in addiction aritmetic operation it is the carry flag
- in subtraction aritmetic operation it is the no borrow flag
- in the graphics context it is set when a collition is detected

The register I is the address register with 16 bit long
PC is program counter, range value is 0x000 - 0xFFF


#### Memory

CHIP-8 has a memory size of 4KB (0x1000 locations)
 The CHIP-8 interpreter is located in the first 512 byte. The programs starts at 0x200 address
 The character font are stored from 0x050 to 0x0A0
 At 0xF00-0xFFF there is the graphics memory


#### Stack

The stack is stored at 0xEA0-0xEFF (96 bytes) should have 16 levels


#### Interrupt

The interrupt is not managed


#### Timers

CHIP-8 has two timers are decreased to 0 at the frequency of 60Hz
- Delay timer: read/write timer
- Sound timer: the system play a beep until this timer is greater than 0


#### Input

CHIP-8 has 16 keys:

	
    1	2	3	C
    4	5	6	D
    7	8	9	E
    A	0	B	F

There are 3 opcode to know the keys status:
- **EX9E**: jump an instruction when the specific key is pressed
- **EXA1**: jump an instruction when the specific key is not pressed
- **FX0A**: waits until a key is pressed and store the value in a CPU register


#### Graphic

The screen size is 64x32 pixel monochromatics
There is only one instruction to draw a sprite on the screen, this is done by performing an XOR operation between the pixels of the sprite and the screen bitmap. If the pixel is turned off (XOR between two lit pixels) the system interprets it as a collision and sets the VF register.
The CHIP-8 graphic system does not managed vertical black (VBLANK).

There are only 2 opcode that acting on the screen:
	**0x00E0**: clear the screen
	**0xDXYN**: draw a sprite on the screen


#### Font

The character font are stored strarting at 0x050
Example font "0" and "7" characters:

	DEC   HEX    BIN               RESULT    DEC   HEX    BIN               RESULT
	240   0xF0   1111 0000    ****          240   0xF0   1111 0000    ****
	144   0x90   1001 0000    *    *            16     0x10   0001 0000          *
	144   0x90   1001 0000    *    *            32     0x20   0010 0000        *
	144   0x90   1001 0000    *    *            64     0x40   0100 0000   *
	240   0xF0   1111 0000    ****            64     0x40   0100 0000   *

Source code example:

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


### Chippotto assembly language

Chippotto doesn't born with an assembly language so I decided to write one.
At each opcode I have associated an assembly mnemonic instruction.

	0NNN    LOAD NNN
	00E0    CLRSCR
	00EE    RET
	1NNN    JMP NNN
	2NNN    CALL NNN
	3XNN    JE VX,NN
	4XNN    JNE VX,NN
	5XY0    JE VX,VY
	6XNN    SET VX,NN
	7XNN    ADD VX,NN
	8XY0    SET VX,VY
	8XY1    OR VX,VY
	8XY2    AND VX,VY
	8XY3    XOR VX,VY
	8XY4    ADD VX,VY
	8XY5    SUB VX,VY
	8XY6    SHR VX,VY
	8XY7    MSUB VX,VY (mirror sub)
	8XYE    SHL VX,VY
	9XY0    JNE VX,VY
	ANNN    SET I,NNN
	BNNN    JMPV NNN
	CXNN    RND VX,NN
	DXYN    DRAW VX,VY,N
	EX9E    JKP VX
	EXA1    JKNP VX
	FX07    GETDT VX (get delay timer)
	FX0A    WKP VX (wait key press)
	FX15    SETDT VX (set delay timer)
	FX18    SETST VX (set sound timer)
	FX1E    ADDI VX
	FX29    FONT VX
	FX33    BCD VX (binary-coded decimal)
	FX55    SAVE VX
	FX65    LOAD VX


### Why Chippotto source code could be useful for your projects

In Chippotto there is a clear separation between the Chip-8 emulator and the Java application that contains it.
The Chip-8 emulator is contained in lib.chip8 package and You could take it and uses in your projects.
Furthermore in this project You could find some interesting solutions among which:

- How to determine correctly the insets of a Frame container using pack() function
- Using Java Reflection to get color field data in Color class
- [Experimental] How to generate a sine waveform to get a sound of a note of arbitrary length
- See an example of a game loop synchronized using custom timers class
- Using session data pattern (but without get set)
- How to convert byte to hex, short to hex and vice versa


