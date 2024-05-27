package org.edward.pandora.test;

import com.alibaba.fastjson2.JSON;
import org.edward.pandora.common.util.DataUtil;
import org.edward.pandora.onion.Knife;
import org.edward.pandora.turbosnail.Papers;
import org.edward.pandora.turbosnail.Path;
import org.edward.pandora.turbosnail.ProtocolLoader;
import org.edward.pandora.turbosnail.decoder.ProtocolDecoder;
import org.edward.pandora.turbosnail.decoder.model.Info;

import java.io.*;
import java.util.Map;

public class ProtocalDecodeTest {
    private static final String PATH = "D:\\edward\\file\\我的工作\\项目\\2023.01.02 极速蜗牛";
    private static final String PROTOCOL_PATH = PATH + File.separator + "protocol" + File.separator + "gbt32960";
    private static final String PROTOCOL_ID = "gbt32960";
    private static final String DATA_PATH = PATH + File.separator + "protocol" + File.separator + "gbt32960" + File.separator + "测试数据.txt";

    public static void main(String[] args) throws Exception {
        Path path = new Path(PROTOCOL_PATH, PROTOCOL_ID);
        Map<String, Papers> papersMap = ProtocolLoader.build()
                .setFormat("xml")
                .load(new Path[]{path});
        Papers papers = papersMap.get(PROTOCOL_ID);
        BufferedReader reader = new BufferedReader(new FileReader(DATA_PATH));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while((line=reader.readLine()) != null) {
            sb.append(line.replaceAll(" ", ""));
        }
        // TODO PROTOCOL_ID的值要与根协议的ID一样
        ProtocolDecoder decoder = new ProtocolDecoder(papers, PROTOCOL_ID);
        Info info = decoder.decode(DataUtil.hexToBytes(sb.toString()));
        Knife knife = Knife.build();
        Object result = knife.peel(info);
        System.out.println("result = " + JSON.toJSONString(result));
    }
}