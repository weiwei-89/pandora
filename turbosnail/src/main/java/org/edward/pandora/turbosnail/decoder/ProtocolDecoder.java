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
        Decode decode = segment.getDecode();
        if(decode == null) {
            throw new Exception("there's not a decode element");
        }
        if(decode.isProtocol()) {
            Protocol protocol = this.papers.get(decode.getValue());
            if(protocol == null) {
                throw new Exception("protocol \""+decode.getValue()+"\" doesn't exist");
            }
            Info infoForProtocol = new Info();
            this.decode(data, protocol, infoForProtocol);
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
        String value = decode(partBytes, decode);
        if(decode.isOption()) {
            Options options = segment.getOptions();
            if(options == null) {
                throw new Exception("there's not an options element");
            }
            List<Option> optionList = segment.getOptionList();
            if(optionList==null || optionList.size()==0) {
                throw new Exception("there are no any option elements");
            }
            for(int i=0; i<optionList.size(); i++) {
                Option option = optionList.get(i);
                if(value.equals(option.getCode())) {
                    value = option.getValue();
                }
            }
        }
        info.put(segment.getId(), value);
    }

    private String decode(byte[] bytes, Decode decode) throws Exception {
        Decode.Type decodeType = decode.getType();
        if(decodeType == Decode.Type.EQUATION) {
            String hexValue = DataUtil.toHexString(bytes);
            if(!hexValue.equalsIgnoreCase(decode.getValue())) {
                throw new Exception("\"value\" is not equal to \""+decode.getValue()+"\"");
            }
            return hexValue;
        } else if(decodeType == Decode.Type.HEX) {
            return DataUtil.toHexString(bytes);
        } else if(decodeType == Decode.Type.ASCII) {
            return DataUtil.toAsciiString(bytes);
        } else if(decodeType == Decode.Type.INT) {
            return String.valueOf(DataUtil.toIntForBigEndian(bytes));
        } else if(decodeType == Decode.Type.INT_BE) {
            return String.valueOf(DataUtil.toIntForBigEndian(bytes));
        } else if(decodeType == Decode.Type.INT_LE) {
            return String.valueOf(DataUtil.toIntForLittleEndian(bytes));
        } else if(decodeType == Decode.Type.SHORT) {
            return String.valueOf(DataUtil.toShortForBigEndian(bytes));
        } else if(decodeType == Decode.Type.SHORT_BE) {
            return String.valueOf(DataUtil.toShortForBigEndian(bytes));
        } else if(decodeType == Decode.Type.SHORT_LE) {
            return String.valueOf(DataUtil.toShortForLittleEndian(bytes));
        } else if(decodeType == Decode.Type.LONG) {
            return String.valueOf(DataUtil.toLongForBigEndian(bytes));
        } else if(decodeType == Decode.Type.LONG_BE) {
            return String.valueOf(DataUtil.toLongForBigEndian(bytes));
        } else if(decodeType == Decode.Type.LONG_LE) {
            return String.valueOf(DataUtil.toLongForLittleEndian(bytes));
        } else if(decodeType == Decode.Type.DOUBLE) {
            return String.valueOf(DataUtil.toDoubleForBigEndian(bytes));
        } else if(decodeType == Decode.Type.DOUBLE_BE) {
            return String.valueOf(DataUtil.toDoubleForBigEndian(bytes));
        } else if(decodeType == Decode.Type.DOUBLE_LE) {
            return String.valueOf(DataUtil.toDoubleForLittleEndian(bytes));
        }
        throw new Exception("decode type is unsupported");
    }
}