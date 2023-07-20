package org.edward.pandora.turbosnail.decoder;

import org.edward.pandora.turbosnail.Papers;
import org.edward.pandora.turbosnail.decoder.model.Data;
import org.edward.pandora.turbosnail.decoder.model.Info;
import org.edward.pandora.common.util.DataUtil;
import org.edward.pandora.turbosnail.xml.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ProtocolDecoder {
    private static final Logger logger = LoggerFactory.getLogger(ProtocolDecoder.class);

    private final Papers papers;
    private final String protocolId;

    public ProtocolDecoder(Papers papers, String protocolId) {
        this.papers = papers;
        this.protocolId = protocolId;
    }

    public Info decode(byte[] bytes) throws Exception {
        logger.info("decoding protocol[protocol_id:{}]......", this.protocolId);
        Info info = new Info();
        this.decode(new Data(bytes), this.papers.get(this.protocolId), info);
        logger.info("done");
        return info;
    }

    private void decode(Data data, Protocol protocol, Info info) throws Exception {
        List<Segment> segmentList = protocol.getSegmentList();
        if(segmentList==null || segmentList.size()==0) {
            return;
        }
        for(int i=0; i<segmentList.size(); i++) {
            this.decode(data, segmentList.get(i), info);
        }
    }

    private void decode(Data data, Segment segment, Info info) throws Exception {
        logger.info("decoding segment[segment_id:{}]......", segment.getId());
        Decode.Type decodeType = Decode.Type.HEX;
        Decode decode = segment.getDecode();
        if(decode != null) {
            decodeType = decode.getType();
        }
        if(decodeType == Decode.Type.PROTOCOL) {
            Info infoForProtocol = new Info();
            this.decode(data, this.papers.get(decode.getProtocolId()), infoForProtocol);
            info.put(segment.getId(), infoForProtocol);
            return;
        }
        Position position = segment.getPosition();
        if(position == null) {
            throw new Exception("there's not a position element");
        }
        if(position.getLength() < 0) {
            throw new Exception("\"length\" is less than 0");
        }
        if(segment.isSkip()) {
            data.skip(position.getLength());
            return;
        }
        byte[] partBytes = data.read(position.getLength());
        // TODO 不要使用这么多的if-else，搞个接口或者设计模式重构一下
        if(decodeType == Decode.Type.EQUATION) {
            String hexValue = DataUtil.toHexString(partBytes);
            if(!hexValue.equalsIgnoreCase(segment.getValue())) {
                throw new Exception("\"value\" is not equal to \""+segment.getValue()+"\"");
            }
            info.put(segment.getId(), hexValue);
        } else if(decodeType == Decode.Type.HEX) {
            info.put(segment.getId(), DataUtil.toHexString(partBytes));
        } else if(decodeType == Decode.Type.ASCII) {
            info.put(segment.getId(), DataUtil.toAsciiString(partBytes));
        } else if(decodeType == Decode.Type.INT) {
            info.put(segment.getId(), DataUtil.toIntForLittleEndian(partBytes));
        } else if(decodeType == Decode.Type.INT_BE) {
            info.put(segment.getId(), DataUtil.toIntForBigEndian(partBytes));
        } else if(decodeType == Decode.Type.INT_LE) {
            info.put(segment.getId(), DataUtil.toIntForLittleEndian(partBytes));
        } else if(decodeType == Decode.Type.OPTION) {
            Options options = decode.getOptions();
            if(options == null) {
                throw new Exception("there's not an options element");
            }
            List<Option> optionList = decode.getOptionList();
            if(optionList==null || optionList.size()==0) {
                throw new Exception("there are no any option elements");
            }
            for(int i=0; i<optionList.size(); i++) {
                Option option = optionList.get(i);
            }
        }
    }
}