package org.edward.pandora.test;

import com.alibaba.fastjson2.JSON;
import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.JexlExpression;
import org.edward.pandora.common.util.DataUtil;

import java.util.Arrays;

public class DataUtilTest {
    public static void main(String[] args) throws Exception {
        String cc = "FE";
        byte[] dd = DataUtil.hexToBytes(cc);
        String ee = DataUtil.toHexString(dd);
        System.out.println("ee: " + ee);
        int total = Integer.parseInt(cc, 16);
        System.out.println("total: " + total);
        String exp = "2*3+(5-9)/2";
        String[] operators = exp.split("\\s*([+\\-*/()])\\s*");
        System.out.println("operators: " + JSON.toJSONString(operators));
        JexlEngine jexlEngine = new JexlBuilder().create();
        String expression = "3 + 4";
        JexlExpression jexlExpression = jexlEngine.createExpression(expression);
        Object result = jexlExpression.evaluate(null);
        System.out.println("result: " + result);
        int aa = 15;
        System.out.println("aa: " + Arrays.toString(DataUtil.toHexArray(DataUtil.intToBytesForBigEndian(aa))));
    }
}