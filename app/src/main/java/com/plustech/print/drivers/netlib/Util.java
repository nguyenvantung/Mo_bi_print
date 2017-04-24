package com.plustech.print.drivers.netlib;
/*
 * Copyright 2013 NVan
 */


/**
 * Various mundate utility methods.
 * @author NVan
 */
public class Util {

    public static String hexDump(byte[] bytes) {
        return hexDump(bytes, 0, bytes.length);
    }

    public static String hexDump(byte[] bytes, int offset, int length) {
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<length; i+=16) {
            int rowSize = length - i;
            if (rowSize > 16) { rowSize = 16; }
            byte[] row = new byte[rowSize];
            System.arraycopy(bytes, offset+i, row, 0, rowSize);
            hexDumpRow(sb, row, i);
        }
        return sb.toString();
    }

    private static void hexDumpRow(StringBuilder sb, byte[] bytes, int offset) {
        sb.append(String.format("%04X: ",offset));
        for (int i=0; i<16; i++) {
            if (bytes.length > i) {
                sb.append(String.format("%02X ",bytes[i]));
            } else {
                sb.append("   ");
            }
        }
        for (int i=0; i<16; i++) {
            if (bytes.length > i) {
                char c = '.';
                int v = (int)bytes[i];
                if ((v > 0x20) && (v < 0x7F)) {
                    c = (char)v;
                }
                sb.append(c);
            }
        }
        sb.append('\n');
    }

}
