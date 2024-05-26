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

    public static String[] toHexArray(byte[] bytes) {
        String[] hexArray = new String[bytes.length];
        for(int i=0; i<bytes.length; i++) {
            hexArray[i] = toHexString(bytes[i]);
        }
        return hexArray;
    }

    public static String[] toHexArray(char[] chars) {
        String[] hexArray = new String[chars.length];
        for(int i=0; i<chars.length; i++) {
            hexArray[i] = toHexString(chars[i]);
        }
        return hexArray;
    }

    public static String toAsciiString(byte b) {
        char c = (char) b;
        return String.valueOf(c);
    }

    public static String toAsciiString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<bytes.length; i++) {
            sb.append(toAsciiString(bytes[i]));
        }
        return sb.toString();
    }

    public static String[] toAsciiArray(byte[] bytes) {
        String[] hexArray = new String[bytes.length];
        for(int i=0; i<bytes.length; i++) {
            hexArray[i] = toAsciiString(bytes[i]);
        }
        return hexArray;
    }

    public static int toInt(byte b) {
        return b&0xFF;
    }

    public static int toIntForBigEndian(byte[] bytes) throws Exception {
        if(bytes==null || bytes.length==0) {
            throw new Exception("there are no any bytes");
        }
        if(bytes.length != 4) {
            throw new Exception("the count of bytes must be 4");
        }
        int total = 0;
        for(int b=bytes.length-1; b>=0; b--) {
            int offset = (bytes.length-1-b)*8;
            total |= (bytes[b]&0xFF)<<offset;
        }
        return total;
    }

    public static int toIntForLittleEndian(byte[] bytes) throws Exception {
        if(bytes==null || bytes.length==0) {
            throw new Exception("there are no any bytes");
        }
        if(bytes.length != 4) {
            throw new Exception("the count of bytes must be 4");
        }
        int total = 0;
        for(int b=0; b<bytes.length; b++) {
            total |= (bytes[b]&0xFF)<<(b*8);
        }
        return total;
    }

    public static byte[] intToBytesForBigEndian(int number) {
        byte[] bytes = new byte[4];
        bytes[3] = (byte) (number&0xFF);
        bytes[2] = (byte) ((number>>8)&0xFF);
        bytes[1] = (byte) ((number>>16)&0xFF);
        bytes[0] = (byte) ((number>>24)&0xFF);
        return bytes;
    }

    public static byte[] intToBytesForLittleEndian(int number) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (number&0xFF);
        bytes[1] = (byte) ((number>>8)&0xFF);
        bytes[2] = (byte) ((number>>16)&0xFF);
        bytes[3] = (byte) ((number>>24)&0xFF);
        return bytes;
    }

    public static short toShortForBigEndian(byte[] bytes) throws Exception {
        if(bytes==null || bytes.length==0) {
            throw new Exception("there are no any bytes");
        }
        if(bytes.length != 2) {
            throw new Exception("the count of bytes must be 2");
        }
        short total = 0;
        for(int b=bytes.length-1; b>=0; b--) {
            int offset = (bytes.length-1-b)*8;
            total |= (bytes[b]&0xFF)<<offset;
        }
        return total;
    }

    public static short toShortForLittleEndian(byte[] bytes) throws Exception {
        if(bytes==null || bytes.length==0) {
            throw new Exception("there are no any bytes");
        }
        if(bytes.length != 2) {
            throw new Exception("the count of bytes must be 2");
        }
        short total = 0;
        for(int b=0; b<bytes.length; b++) {
            total |= (bytes[b]&0xFF)<<(b*8);
        }
        return total;
    }

    public static byte[] shortToBytesForBigEndian(short number) {
        byte[] bytes = new byte[2];
        bytes[1] = (byte) (number&0xFF);
        bytes[0] = (byte) ((number>>8)&0xFF);
        return bytes;
    }

    public static byte[] shortToBytesForLittleEndian(short number) {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) (number&0xFF);
        bytes[1] = (byte) ((number>>8)&0xFF);
        return bytes;
    }

    public static long toLongForBigEndian(byte[] bytes) throws Exception {
        if(bytes==null || bytes.length==0) {
            throw new Exception("there are no any bytes");
        }
        if(bytes.length != 8) {
            throw new Exception("the count of bytes must be 8");
        }
        long total = 0;
        for(int b=bytes.length-1; b>=0; b--) {
            int offset = (bytes.length-1-b)*8;
            total |= ((long)(bytes[b]&0xFF))<<offset;
        }
        return total;
    }

    public static long toLongForLittleEndian(byte[] bytes) throws Exception {
        if(bytes==null || bytes.length==0) {
            throw new Exception("there are no any bytes");
        }
        if(bytes.length != 8) {
            throw new Exception("the count of bytes must be 8");
        }
        long total = 0;
        for(int b=0; b<bytes.length; b++) {
            total |= ((long)(bytes[b]&0xFF))<<(b*8);
        }
        return total;
    }

    public static byte[] longToBytesForBigEndian(long number) {
        byte[] bytes = new byte[8];
        bytes[7] = (byte) (number&0xFF);
        bytes[6] = (byte) ((number>>8)&0xFF);
        bytes[5] = (byte) ((number>>16)&0xFF);
        bytes[4] = (byte) ((number>>24)&0xFF);
        bytes[3] = (byte) ((number>>32)&0xFF);
        bytes[2] = (byte) ((number>>40)&0xFF);
        bytes[1] = (byte) ((number>>48)&0xFF);
        bytes[0] = (byte) ((number>>56)&0xFF);
        return bytes;
    }

    public static byte[] longToBytesForLittleEndian(long number) {
        byte[] bytes = new byte[8];
        bytes[0] = (byte) (number&0xFF);
        bytes[1] = (byte) ((number>>8)&0xFF);
        bytes[2] = (byte) ((number>>16)&0xFF);
        bytes[3] = (byte) ((number>>24)&0xFF);
        bytes[4] = (byte) ((number>>32)&0xFF);
        bytes[5] = (byte) ((number>>40)&0xFF);
        bytes[6] = (byte) ((number>>48)&0xFF);
        bytes[7] = (byte) ((number>>56)&0xFF);
        return bytes;
    }

    public static double toDoubleForBigEndian(byte[] bytes) throws Exception {
        if(bytes==null || bytes.length==0) {
            throw new Exception("there are no any bytes");
        }
        if(bytes.length != 8) {
            throw new Exception("the count of bytes must be 8");
        }
        long total = 0;
        for(int b=bytes.length-1; b>=0; b--) {
            int offset = (bytes.length-1-b)*8;
            total |= ((long)(bytes[b]&0xFF))<<offset;
        }
        return Double.longBitsToDouble(total);
    }

    public static double toDoubleForLittleEndian(byte[] bytes) throws Exception {
        if(bytes==null || bytes.length==0) {
            throw new Exception("there are no any bytes");
        }
        if(bytes.length != 8) {
            throw new Exception("the count of bytes must be 8");
        }
        long total = 0;
        for(int b=0; b<bytes.length; b++) {
            total |= ((long)(bytes[b]&0xFF))<<(b*8);
        }
        return Double.longBitsToDouble(total);
    }

    public static byte[] doubleToBytesForBigEndian(double number) {
        long longValue = Double.doubleToRawLongBits(number);
        byte[] bytes = new byte[8];
        bytes[7] = (byte) (longValue&0xFF);
        bytes[6] = (byte) ((longValue>>8)&0xFF);
        bytes[5] = (byte) ((longValue>>16)&0xFF);
        bytes[4] = (byte) ((longValue>>24)&0xFF);
        bytes[3] = (byte) ((longValue>>32)&0xFF);
        bytes[2] = (byte) ((longValue>>40)&0xFF);
        bytes[1] = (byte) ((longValue>>48)&0xFF);
        bytes[0] = (byte) ((longValue>>56)&0xFF);
        return bytes;
    }

    public static byte[] doubleToBytesForLittleEndian(double number) {
        long longValue = Double.doubleToRawLongBits(number);
        byte[] bytes = new byte[8];
        bytes[0] = (byte) (longValue&0xFF);
        bytes[1] = (byte) ((longValue>>8)&0xFF);
        bytes[2] = (byte) ((longValue>>16)&0xFF);
        bytes[3] = (byte) ((longValue>>24)&0xFF);
        bytes[4] = (byte) ((longValue>>32)&0xFF);
        bytes[5] = (byte) ((longValue>>40)&0xFF);
        bytes[6] = (byte) ((longValue>>48)&0xFF);
        bytes[7] = (byte) ((longValue>>56)&0xFF);
        return bytes;
    }

    public static byte hexToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(String.valueOf(c).toUpperCase());
    }

    public static byte[] hexToBytes(String hex) {
        int length = hex.length() / 2;
        byte[] bytes = new byte[length];
        char[] chars = hex.toCharArray();
        for(int i=0; i<length; i++) {
            int position = i * 2;
            bytes[i] = (byte) ((hexToByte(chars[position])<<4)|(hexToByte(chars[position+1])));
        }
        return bytes;
    }
}