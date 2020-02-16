package rocknrol.project.chippotto.lib.chip8;

public class Util {

    private long timer;

    public String byteToHex(byte b) {
        char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[2];
        int v = b & 0xFF;
        hexChars[0] = hexArray[v >>> 4];
        hexChars[1] = hexArray[v & 0x0F];
        return new String(hexChars);
    }

    public String byteToHex(byte b, int n) {
        if(n == 1 || n == 2)
            return(byteToHex(b).substring(2-n,2));
        else
            return("");
    }

    public String shortToHex(short s) {
        byte b = (byte)(s & 0xFF);
        String l = byteToHex(b);

        b = (byte)(s >> 8 & 0xFF);
        String h = byteToHex(b);

        return new String(h + l);
    }

    public String shortToHex(short s, int n) {
        if(n >= 1 && n <= 4)
            return(shortToHex(s).substring(4-n,4));
        else
            return("");
    }
}
