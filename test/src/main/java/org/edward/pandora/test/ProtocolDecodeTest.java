package org.edward.pandora.test;

import com.alibaba.fastjson2.JSON;
import org.edward.pandora.common.util.DataUtil;
import org.edward.pandora.onion.Knife;
import org.edward.pandora.turbosnail.Papers;
import org.edward.pandora.turbosnail.Path;
import org.edward.pandora.turbosnail.ProtocolLoader;
import org.edward.pandora.turbosnail.decoder.ProtocolDecoder;
import org.edward.pandora.turbosnail.decoder.model.Info;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class ProtocolDecodeTest {
    private static final Logger logger = LoggerFactory.getLogger(ProtocolDecodeTest.class);
    private static final String PATH = "D:\\edward\\file\\我的工作\\项目\\2023.01.02 极速蜗牛";
    private static final String PROTOCOL_PATH = PATH + File.separator + "测试" + File.separator + "protocol";
    private static final String PROTOCOL_ID = "gbt32960";
    private static final String DATA_PATH = PATH + File.separator + "测试" + File.separator + "测试数据-车辆登入.txt";

    public static void main(String[] args) throws Exception {
        logger.info("loading protocol \"{}\" [{}]", PROTOCOL_ID, PROTOCOL_PATH);
        Path path = new Path(PROTOCOL_PATH, PROTOCOL_ID);
        Papers papers = ProtocolLoader.build()
                .setFormat(ProtocolLoader.DEFAULT_FORMAT)
                .load(path);
        BufferedReader reader = new BufferedReader(new FileReader(DATA_PATH));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while((line=reader.readLine()) != null) {
            sb.append(line.replaceAll(" ", ""));
        }
        logger.info("decoding data [{}]", DATA_PATH);
        ProtocolDecoder decoder = new ProtocolDecoder(papers);
        Info info = decoder.decode(DataUtil.hexToBytes(sb.toString()));
        Knife knife = Knife.build();
        Object result = knife.peel(info);
        logger.info("result: {}", JSON.toJSONString(result));
    }
}