package org.edward.pandora.turbosnail.decoder;

import org.edward.pandora.turbosnail.Papers;
import org.edward.pandora.turbosnail.decoder.model.Data;
import org.edward.pandora.turbosnail.decoder.model.Info;
import org.edward.pandora.turbosnail.xml.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ProtocolDecoder {
    private static final Logger logger = LoggerFactory.getLogger(ProtocolDecoder.class);

    private final Papers papers;

    public ProtocolDecoder(Papers papers) {
        this.papers = papers;
    }

    public Info decode(byte[] bytes) throws Exception {
        logger.info("main protocol: {}", this.papers.getProtocolId());
        Protocol mainProtocol = this.papers.get(this.papers.getProtocolId());
        if(mainProtocol == null) {
            throw new Exception(String.format("main protocol \"%s\" doesn't exist", this.papers.getProtocolId()));
        }
        Info info = this.decode(new Data(bytes), mainProtocol);
        logger.info("done");
        return info;
    }

    private Info decode(Data data, Protocol protocol) throws Exception {
        logger.info("decoding protocol [protocol_id:{}]......", protocol.getId());
        List<Element> elementList = protocol.getElementList();
        if(elementList==null || elementList.size()==0) {
            return null;
        }
        Info info = new Info();
        for(int i=0; i<elementList.size(); i++) {
            Element element = elementList.get(i);
            if(element instanceof Segment) {
                Segment segment = (Segment) element;
                info.put(segment.getId(), this.decode(data, segment));
            } else if(element instanceof Multi) {
                Multi multi = (Multi) element;
                List<Segment> segmentList = multi.getSegmentList();
                if(segmentList==null || segmentList.size()==0) {
                    continue;
                }
                int count = multi.getCount();
                if(count > 0) {
                    List<Object> subInfoList = new ArrayList<>(count);
                    for(int c=0; c<count; c++) {
                        Info subInfo = new Info(segmentList.size());
                        for(int s=0; s<segmentList.size(); s++) {
                            Segment segment = segmentList.get(s);
                            subInfo.put(segment.getId(), this.decode(data, segment));
                        }
                        subInfoList.add(subInfo);
                    }
                    info.put(multi.getId(), subInfoList);
                } else {
                    List<Object> subInfoList = new ArrayList<>();
                    while(data.readable()) {
                        try {
                            Info subInfo = new Info(segmentList.size());
                            for(int s=0; s<segmentList.size(); s++) {
                                Segment segment = segmentList.get(s);
                                subInfo.put(segment.getId(), this.decode(data, segment));
                            }
                            subInfoList.add(subInfo);
                        } catch(Exception e) {
                            logger.error("decoding error", e);
                            break;
                        }
                    }
                    info.put(multi.getId(), subInfoList);
                }
            } else {
                logger.warn("skipping unknown element \"{}\"", element.getId());
                continue;
            }
        }
        return info;
    }

    private Object decode(Data data, Segment segment) throws Exception {
        logger.info("decoding segment [segment_id:{}]......", segment.getId());
        if(segment.isMulti()) {
            Segment segmentReplica = new Segment();
            segment.copy(segmentReplica);
            segmentReplica.setMulti(false);
            int count = segment.getCount();
            if(count > 0) {
                List<Object> infoList = new ArrayList<>(count);
                for(int c=0; c<count; c++) {
                    infoList.add(this.decode(data, segmentReplica));
                }
                return infoList;
            } else {
                List<Object> infoList = new ArrayList<>();
                while(data.readable()) {
                    try {
                        infoList.add(this.decode(data, segmentReplica));
                    } catch(Exception e) {
                        logger.error("decoding error", e);
                        break;
                    }
                }
                return infoList;
            }
        }
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
        String segmentCode = Segment.generateUniqueCode(segment);
        if(protocol.getCacheSet().contains(segmentCode)) {
            protocol.getCache().put(segmentCode, value);
        }
        return value;
    }
}