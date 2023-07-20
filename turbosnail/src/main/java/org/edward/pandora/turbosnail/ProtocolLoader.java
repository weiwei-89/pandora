package org.edward.pandora.turbosnail;

import org.edward.pandora.turbosnail.xml.ProtocolXmlHandler;
import org.edward.pandora.turbosnail.xml.model.Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Map;

public class ProtocolLoader {
    private static final Logger logger = LoggerFactory.getLogger(ProtocolLoader.class);
    private static final String FORMAT = "xml";

    public static Map<String, Papers> load(Path[] pathArray) throws Exception {
        if(pathArray==null || pathArray.length==0) {
            throw new Exception("path is not specified");
        }
        Map<String, Papers> papersMap = new HashMap<>(pathArray.length);
        for(int p=0; p<pathArray.length; p++) {
            logger.info("reading protocol[protocol_id:{}]......", pathArray[p].getProtocolId());
            try {
                papersMap.put(pathArray[p].getProtocolId(), load(pathArray[p].getPath()));
                logger.info("done");
            } catch(Exception e) {
                logger.error("reading protocol goes wrong", e);
                e.printStackTrace();
            }
        }
        return papersMap;
    }

    private static Papers load(String path) throws Exception {
        File pathFile = new File(path);
        if(!pathFile.isDirectory()) {
            throw new Exception("\""+path+"\" is not a path");
        }
        File[] protocolFileArray = pathFile.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith("."+FORMAT);
            }
        });
        if(protocolFileArray==null || protocolFileArray.length==0) {
            throw new Exception("there are no any protocol files in path \""+path+"\"");
        }
        Papers papers = new Papers(protocolFileArray.length);
        for(int f=0; f<protocolFileArray.length; f++) {
            SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
            ProtocolXmlHandler protocolXmlHandler = new ProtocolXmlHandler();
            saxParser.parse(protocolFileArray[f], protocolXmlHandler);
            Protocol protocol = protocolXmlHandler.getProtocol();
            papers.put(protocol.getId(), protocol);
        }
        return papers;
    }
}