package org.edward.pandora.test;

import org.edward.pandora.common.util.DataUtil;

public class DataUtilTest {
    public static void main(String[] args) throws Exception {
        String cc = "232367";
        byte[] dd = DataUtil.hexToBytes(cc);
        System.out.println("dd = " + DataUtil.toHexString(dd));
    }
}