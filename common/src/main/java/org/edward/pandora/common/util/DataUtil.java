package org.edward.pandora.common.util;

public class DataUtil {
    public static String toHexString(byte b) {
        String hex = Integer.toHexString(b&0xFF);
        if(hex.length() == 1) {
            return '0' + hex;
        }
        return hex;
    }

    public static String toHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<bytes.length; i++) {
            sb.append(toHexString(bytes[i]));
        }
        return sb.toString();
    }

    public static String toHexString(char c) {
        return Integer.toHexString(c);
    }

    public static String toHexString(char[] chars) {
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<chars.length; i++) {
            sb.append(toHexString(chars[i]));
        }
        return sb.toString();
    }

    public static String[] toHexStringArray(byte[] bytes) {
        String[] hexArray = new String[bytes.length];
        for(int i=0; i<bytes.length; i++) {
            hexArray[i] = toHexString(bytes[i]);
        }
        return hexArray;
    }

    public static String[] toHexStringArray(char[] chars) {
        String[] hexArray = new String[chars.length];
        for(int i=0; i<chars.length; i++) {
            hexArray[i] = toHexString(chars[i]);
        }
        return hexArray;
    }

    public static String toAscii(byte b) {
        char c = (char) b;
        return String.valueOf(c);
    }

    public static String toAscii(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<bytes.length; i++) {
            sb.append(toAscii(bytes[i]));
        }
        return sb.toString();
    }

    public static String[] toAsciiArray(byte[] bytes) {
        String[] hexArray = new String[bytes.length];
        for(int i=0; i<bytes.length; i++) {
            hexArray[i] = toAscii(bytes[i]);
        }
        return hexArray;
    }

    public static int toNumber(byte b) {
        return b&0xFF;
    }

    public static int toNumberForBigEndian(byte[] bytes) throws Exception {
        if(bytes==null || bytes.length==0) {
            throw new Exception("there are no any bytes");
        }
        if(bytes.length > 4) {
            throw new Exception("the count of bytes must be less than or equal to 4");
        }
        int total = 0;
        for(int b=bytes.length-1; b>=0; b--) {
            total += (bytes[b]&0xFF)<<(b*8);
        }
        return total;
    }

    public static int toNumberForLittleEndian(byte[] bytes) throws Exception {
        if(bytes==null || bytes.length==0) {
            throw new Exception("there are no any bytes");
        }
        if(bytes.length > 4) {
            throw new Exception("the count of bytes must be less than or equal to 4");
        }
        int total = 0;
        for(int b=0; b<bytes.length; b++) {
            total += (bytes[b]&0xFF)<<(b*8);
        }
        return total;
    }

    public static byte[] toBytesForBigEndian(int number) {
        byte[] bytes = new byte[4];
        bytes[3] = (byte) (number&0xFF);
        bytes[2] = (byte) ((number>>8)&0xFF);
        bytes[1] = (byte) ((number>>16)&0xFF);
        bytes[0] = (byte) ((number>>24)&0xFF);
        return bytes;
    }

    public static byte[] toBytesForLittleEndian(int number) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (number&0xFF);
        bytes[1] = (byte) ((number>>8)&0xFF);
        bytes[2] = (byte) ((number>>16)&0xFF);
        bytes[3] = (byte) ((number>>24)&0xFF);
        return bytes;
    }
}