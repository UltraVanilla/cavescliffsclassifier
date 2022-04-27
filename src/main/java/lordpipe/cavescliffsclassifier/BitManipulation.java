package lordpipe.cavescliffsclassifier;

public class BitManipulation {
    public static long compact(int x, int z) {
        long compact = 0;
        compact |= x & 0xffffffffL;
        compact |= (z & 0xffffffffL) << 32;
        return compact;
    }

    public static int[] uncompact(long compact) {
        int[] result = new int[2];
        result[0] = (int)(compact & 0xffffffffL);
        result[1] = (int)(compact >>> 32);
        return result;
    }

    public static int getBit(byte[] data, int pos) {
        int posByte = pos / 8;
        int posBit = pos % 8;
        byte valByte = data[posByte];
        int valInt = valByte >> (8 - (posBit + 1)) & 1;
        return valInt;
    }
}
