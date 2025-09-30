package org.edward.pandora.test;

import com.alibaba.fastjson2.JSON;
import org.apache.commons.cli.*;
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
import java.nio.charset.StandardCharsets;

public class ProtocolDecodeTest {
    private static final Logger logger = LoggerFactory.getLogger(ProtocolDecodeTest.class);
    private static final int MAX_LENGTH = 10;

    private static final String PROTOCOL_ID = "protocol.id";
    private static final String PROTOCOL_PATH = "protocol.path";
    private static final String DATA_PATH = "data.path";
    private static final String OUTPUT_PATH = "output.path";

    public static void main(String[] args) throws Exception {
        Options options = new Options();
        options.addOption(Option.builder().longOpt(PROTOCOL_ID).required(true).hasArg(true).build());
        options.addOption(Option.builder().longOpt(PROTOCOL_PATH).required(true).hasArg(true).build());
        options.addOption(Option.builder().longOpt(DATA_PATH).required(true).hasArg(true).build());
        options.addOption(Option.builder().longOpt(OUTPUT_PATH).required(true).hasArg(true).build());
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);
        String protocolId = cmd.getOptionValue(PROTOCOL_ID);
        String protocolPath = cmd.getOptionValue(PROTOCOL_PATH);
        String dataPath = cmd.getOptionValue(DATA_PATH);
        String outputPath = cmd.getOptionValue(OUTPUT_PATH);
        logger.info("loading protocol \"{}\" [{}]", protocolId, protocolPath);
        Path path = new Path(protocolPath, protocolId);
        Papers papers = ProtocolLoader.build()
                .setFormat(ProtocolLoader.DEFAULT_FORMAT)
                .load(path);
        papers.setMaxLength(MAX_LENGTH);
        org.edward.pandora.common.util.FileReader fileReader = new org.edward.pandora.common.util.FileReader();
        String hex = fileReader.read(dataPath);
        hex = hex.replaceAll(" ", "");
        logger.info("decoding data [{}]", DATA_PATH);
        ProtocolDecoder decoder = new ProtocolDecoder(papers);
        Info info = decoder.decode(DataUtil.hexToBytes(hex));
        Knife knife = Knife.build();
        Object result = knife.peel(info);
        logger.info("result: {}", JSON.toJSONString(result));
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(outputPath+File.separator+"out.txt"),
                            StandardCharsets.UTF_8
                    )
            );
            writer.write(JSON.toJSONString(result));
            writer.flush();
        } finally {
            if(writer != null) {
                writer.close();
            }
        }
    }
}