package org.edward.pandora.turbosnail.decoder;

import org.edward.pandora.turbosnail.Papers;
import org.edward.pandora.turbosnail.decoder.model.Data;
import org.edward.pandora.turbosnail.decoder.model.Info;
import org.edward.pandora.turbosnail.xml.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ProtocolDecoder {
    private static final Logger logger = LoggerFactory.getLogger(ProtocolDecoder.class);

    private final Papers papers;

    public ProtocolDecoder(Papers papers) {
        this.papers = papers;
    }

    public Info decode(byte[] bytes) throws Exception {
        logger.info("decoding protocol [protocol_id:{}]......", this.papers.getProtocolId());
        Protocol protocol = this.papers.get(this.papers.getProtocolId());
        if(protocol == null) {
            throw new Exception(String.format("protocol \"%s\" doesn't exist", this.papers.getProtocolId()));
        }
        Info info = this.decode(new Data(bytes), protocol);
        logger.info("done");
        return info;
    }

    private Info decode(Data data, Protocol protocol) throws Exception {
        List<Segment> segmentList = protocol.getSegmentList();
        if(segmentList==null || segmentList.size()==0) {
            return null;
        }
        Info info = new Info();
        for(int i=0; i<segmentList.size(); i++) {
            info.put(segmentList.get(i).getId(), this.decode(data, segmentList.get(i)));
        }
        return info;
    }

    private Object decode(Data data, Segment segment) throws Exception {
        logger.info("decoding segment [segment_id:{}]......", segment.getId());
        Decode decode = segment.getDecode();
        if(decode == null) {
            throw new Exception("there's not a decode element");
        }
        if(decode.isProtocol()) {
            String protocolName = decode.findProtocolName();
            Protocol subProtocol = this.papers.get(protocolName);
            if(subProtocol == null) {
                throw new Exception(String.format("protocol \"%s\" doesn't exist", protocolName));
            }
            return this.decode(data, subProtocol);
        }
        Position position = segment.getPosition();
        if(position == null) {
            throw new Exception("there's not a position element");
        }
        position.analyzeLength();
        logger.info("length: {}", position.getLength());
        if(segment.isSkip()) {
            data.skip(position.getLength());
            logger.info("skipping......");
            return null;
        }
        byte[] partBytes = data.read(position.getLength());
        String value = decode.decode(partBytes);
        logger.info("value: {}", value);
        Protocol protocol = (Protocol) segment.getProtocol();
        if(protocol.getCache().containsKey(segment.getId())) {
            protocol.getCache().put(segment.getId(), value);
        }
        return value;
    }
}