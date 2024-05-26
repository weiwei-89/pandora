package org.edward.pandora.turbosnail.decoder;

import org.edward.pandora.turbosnail.Papers;
import org.edward.pandora.turbosnail.decoder.model.Data;
import org.edward.pandora.turbosnail.decoder.model.Info;
import org.edward.pandora.common.util.DataUtil;
import org.edward.pandora.turbosnail.decoder.model.Value;
import org.edward.pandora.turbosnail.xml.model.*;
import org.edward.pandora.turbosnail.xml.model.property.Property;
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
        Protocol protocol = this.papers.get(this.protocolId);
        if(protocol == null) {
            throw new Exception("protocol \""+this.protocolId+"\" doesn't exist");
        }
        this.decode(new Data(bytes), protocol, info);
        logger.info("done");
        return info;
    }

    private void decode(Data data, Protocol protocol, Info info) throws Exception {
        List<Segment> segmentList = protocol.getSegmentList();
        if(segmentList==null || segmentList.size()==0) {
            return;
        }
        for(int i=0; i<segmentList.size(); i++) {
            this.decode(data, protocol, segmentList.get(i), info);
        }
    }

    private void decode(Data data, Protocol protocol, Segment segment, Info info) throws Exception {
        logger.info("decoding segment[segment_id:{}]......", segment.getId());
        Decode decode = segment.getDecode();
        if(decode == null) {
            throw new Exception("there's not a decode element");
        }
        if(decode.isProtocol()) {
            String protocolName = null;
            if(Property.isReference(decode.getValue())) {
                String protocolReference = Property.extractReference(decode.getValue());
                if(protocol.getCache().containsKey(protocolReference)) {
                    protocolName = protocol.getCache().get(protocolReference);
                }
            } else {
                protocolName = decode.getValue();
            }
            Protocol subProtocol = this.papers.get(protocolName);
            if(subProtocol == null) {
                throw new Exception("protocol \""+protocolName+"\" doesn't exist");
            }
            Info infoForProtocol = new Info();
            this.decode(data, subProtocol, infoForProtocol);
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
            logger.info("skipping......");
            return;
        }
        byte[] partBytes = data.read(position.getLength());
        Decode.Type type = decode.getType();
        if(type == null) {
            throw new Exception("decode type is not specified");
        }
        String value0 = decode(partBytes, type);
        logger.info("value0 = {}", value0);
        String value = null;
        Decode.Process process = decode.getProcess();
        if(process == null) {
            value = value0;
        } else {
            Value value1 = new Value();
            this.process(value0, segment, decode, process, value1);
            value = value1.getContent();
        }
        info.put(segment.getId(), value);
        if(protocol.getCache().containsKey(segment.getId())) {
            protocol.getCache().put(segment.getId(), value);
        }
    }

    private String decode(byte[] bytes, Decode.Type type) throws Exception {
        if(type == Decode.Type.HEX) {
            return DataUtil.toHexString(bytes);
        } else if(type == Decode.Type.ASCII) {
            return DataUtil.toAsciiString(bytes);
        } else if(type == Decode.Type.INT) {
            return String.valueOf(DataUtil.toInt(bytes[0]));
        } else if(type == Decode.Type.INT_BE) {
            return String.valueOf(DataUtil.toIntForBigEndian(bytes));
        } else if(type == Decode.Type.INT_LE) {
            return String.valueOf(DataUtil.toIntForLittleEndian(bytes));
        } else if(type == Decode.Type.SHORT) {
            return String.valueOf(DataUtil.toShortForBigEndian(bytes));
        } else if(type == Decode.Type.SHORT_BE) {
            return String.valueOf(DataUtil.toShortForBigEndian(bytes));
        } else if(type == Decode.Type.SHORT_LE) {
            return String.valueOf(DataUtil.toShortForLittleEndian(bytes));
        } else if(type == Decode.Type.LONG) {
            return String.valueOf(DataUtil.toLongForBigEndian(bytes));
        } else if(type == Decode.Type.LONG_BE) {
            return String.valueOf(DataUtil.toLongForBigEndian(bytes));
        } else if(type == Decode.Type.LONG_LE) {
            return String.valueOf(DataUtil.toLongForLittleEndian(bytes));
        } else if(type == Decode.Type.DOUBLE) {
            return String.valueOf(DataUtil.toDoubleForBigEndian(bytes));
        } else if(type == Decode.Type.DOUBLE_BE) {
            return String.valueOf(DataUtil.toDoubleForBigEndian(bytes));
        } else if(type == Decode.Type.DOUBLE_LE) {
            return String.valueOf(DataUtil.toDoubleForLittleEndian(bytes));
        }
        throw new Exception("decode type is unsupported");
    }

    private void process(String value, Segment segment, Decode decode, Decode.Process process, Value out) throws Exception {
        if(process == Decode.Process.EQUATION) {
            if(value.equals(decode.getValue())) {
                out.setContent(value);
                return;
            }
            throw new Exception("\"value\" is not equal to \""+decode.getValue()+"\"");
        } else if(process == Decode.Process.OPTION) {
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
                    out.setContent(option.getValue());
                    return;
                }
            }
            throw new Exception("there is not a matched element");
        }
        throw new Exception("process type is unsupported");
    }
}