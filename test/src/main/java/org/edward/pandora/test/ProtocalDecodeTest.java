package org.edward.pandora.test;

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
    private static final String PROTOCOL_PATH = PATH + File.separator + "protocol";
    private static final String PROTOCOL_ID = "protocol_test";
    private static final String FILE_PATH = PATH + File.separator + "日志.txt";

    public static void main(String[] args) throws Exception {
        Path path = new Path(PROTOCOL_PATH, PROTOCOL_ID);
        Map<String, Papers> papersMap = ProtocolLoader.build()
                .setFormat("xml")
                .load(new Path[]{path});
        Papers papers = papersMap.get(PROTOCOL_ID);
        BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while((line=reader.readLine()) != null) {
            sb.append(line.replaceAll(" ", ""));
        }
        ProtocolDecoder decoder = new ProtocolDecoder(papers, PROTOCOL_ID);
        Info info = decoder.decode(DataUtil.hexToBytes(sb.toString()));
        Knife knife = Knife.build();
        Object result = knife.peel(info);
        System.out.println("result = " + result.toString());
    }
}