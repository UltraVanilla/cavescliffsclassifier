package lordpipe.cavescliffsclassifier;

import java.lang.IllegalStateException;

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

    public static int getSubBits(byte[] data, int pos) {
        if (data.length == 8192) {
            return getBit(data, pos);
        } else if (data.length == 16384) {
            return getCrumb(data, pos);
        } else if (data.length == 32768) {
            return getNibble(data, pos);
        } else {
            throw new IllegalStateException("CavesCliffsClassifier data is wrong length!");
        }
    }

    public static int getBit(byte[] data, int pos) {
        int posByte = pos / 8;
        int posBit = pos % 8;
        byte valByte = data[posByte];
        int valInt = valByte >> (8 - (posBit + 1)) & 1;
        return valInt;
    }

    public static int getCrumb(byte[] data, int pos) {
        int offset = pos * 2;
        int posByte = offset / 8;
        int posBit = offset % 8;
        byte valByte = data[posByte];
        int valInt = valByte >> (8 - (posBit + 2)) & 0b11;
        return valInt;
    }

    public static int getNibble(byte[] data, int pos) {
        int offset = pos * 4;
        int posByte = offset / 8;
        int posBit = offset % 8;
        byte valByte = data[posByte];
        int valInt = valByte >> (8 - (posBit + 4)) & 0b1111;
        return valInt;
    }
}
