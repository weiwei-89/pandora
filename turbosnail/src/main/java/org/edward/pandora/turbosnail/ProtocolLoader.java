package org.edward.pandora.turbosnail;

import org.edward.pandora.turbosnail.xml.ProtocolXmlHandler;
import org.edward.pandora.turbosnail.xml.model.Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.FilenameFilter;

public class ProtocolLoader {
    private static final Logger logger = LoggerFactory.getLogger(ProtocolLoader.class);
    public static final String DEFAULT_FORMAT = "xml";

    private ProtocolLoader() {

    }

    public static ProtocolLoader build() {
        return new ProtocolLoader();
    }

    private String format = DEFAULT_FORMAT;

    public ProtocolLoader setFormat(String format) {
        this.format = format;
        return this;
    }

    public Papers load(Path path) throws Exception {
        File pathFile = new File(path.getPath());
        if(!pathFile.isDirectory()) {
            throw new Exception(String.format("\"%s\" is not a path", path.getPath()));
        }
        File[] protocolFileArray = pathFile.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith("."+format.toLowerCase());
            }
        });
        if(protocolFileArray==null || protocolFileArray.length==0) {
            throw new Exception("there are no protocol files");
        }
        Papers papers = new Papers(protocolFileArray.length, path.getProtocolId());
        for(int f=0; f<protocolFileArray.length; f++) {
            logger.info("loading protocol file [{}]", protocolFileArray[f].getPath());
            try {
                SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
                ProtocolXmlHandler protocolXmlHandler = new ProtocolXmlHandler();
                saxParser.parse(protocolFileArray[f], protocolXmlHandler);
                Protocol protocol = protocolXmlHandler.getProtocol();
                papers.put(protocol.getId(), protocol);
            } catch(Exception e) {
                logger.error("loading error", e);
            }
        }
        return papers;
    }
}