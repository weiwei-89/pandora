package org.edward.pandora.turbosnail.xml.model;

import org.edward.pandora.common.util.DataUtil;
import org.edward.pandora.turbosnail.decoder.model.Value;
import org.edward.pandora.turbosnail.xml.model.property.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Decode extends Element {
    private static final Logger logger = LoggerFactory.getLogger(Decode.class);

    private Type type;
    private Process process;
    private String value;
    private boolean protocol = false;

    public Type getType() {
        return this.type;
    }
    public void setType(Type type) {
        this.type = type;
    }
    public Process getProcess() {
        return process;
    }
    public void setProcess(Process process) {
        this.process = process;
    }
    public String getValue() {
        return this.value;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public boolean isProtocol() {
        return protocol;
    }
    public void setProtocol(boolean protocol) {
        this.protocol = protocol;
    }

    public enum Type {
        HEX,
        ASCII,
        INT,
        INT_BE,
        INT_LE,
        SHORT,
        SHORT_BE,
        SHORT_LE,
        LONG,
        LONG_BE,
        LONG_LE,
        DOUBLE,
        DOUBLE_BE,
        DOUBLE_LE;

        public static Type get(String type) {
            for(Type type0 : Type.values()) {
                if(type0.name().equalsIgnoreCase(type)) {
                    return type0;
                }
            }
            return null;
        }
    }

    public enum Process {
        EQUATION,
        OPTION;

        public static Process get(String process) {
            for(Process process0 : Process.values()) {
                if(process0.name().equalsIgnoreCase(process)) {
                    return process0;
                }
            }
            return null;
        }
    }

    public String decode(byte[] bytes) throws Exception {
        String value0 = this.decode0(bytes);
        logger.info("value0: {}", value0);
        if(this.process == null) {
            return value0;
        } else {
            Value value = new Value();
            this.process(value0, value);
            return value.getContent();
        }
    }

    private String decode0(byte[] bytes) throws Exception {
        if(this.type == Type.HEX) {
            return DataUtil.toHexString(bytes);
        } else if(this.type == Type.ASCII) {
            return DataUtil.toAsciiString(bytes);
        } else if(this.type == Type.INT) {
            return String.valueOf(DataUtil.toInt(bytes[0]));
        } else if(this.type == Type.INT_BE) {
            return String.valueOf(DataUtil.toIntForBigEndian(bytes));
        } else if(this.type == Type.INT_LE) {
            return String.valueOf(DataUtil.toIntForLittleEndian(bytes));
        } else if(this.type == Type.SHORT) {
            return String.valueOf(DataUtil.toShortForBigEndian(bytes));
        } else if(this.type == Type.SHORT_BE) {
            return String.valueOf(DataUtil.toShortForBigEndian(bytes));
        } else if(this.type == Type.SHORT_LE) {
            return String.valueOf(DataUtil.toShortForLittleEndian(bytes));
        } else if(this.type == Type.LONG) {
            return String.valueOf(DataUtil.toLongForBigEndian(bytes));
        } else if(this.type == Type.LONG_BE) {
            return String.valueOf(DataUtil.toLongForBigEndian(bytes));
        } else if(this.type == Type.LONG_LE) {
            return String.valueOf(DataUtil.toLongForLittleEndian(bytes));
        } else if(this.type == Type.DOUBLE) {
            return String.valueOf(DataUtil.toDoubleForBigEndian(bytes));
        } else if(this.type == Type.DOUBLE_BE) {
            return String.valueOf(DataUtil.toDoubleForBigEndian(bytes));
        } else if(this.type == Type.DOUBLE_LE) {
            return String.valueOf(DataUtil.toDoubleForLittleEndian(bytes));
        } else if(this.type == null) {
            throw new Exception("decode type is not specified");
        }
        throw new Exception(String.format("decode type \"%s\" is unsupported", this.type.name()));
    }

    private void process(String value0, Value out) throws Exception {
        if(this.process == Decode.Process.EQUATION) {
            if(value0.equals(this.value)) {
                out.setContent(value0);
                return;
            }
            throw new Exception(String.format("\"value\" is not equal to \"%s\"", this.value));
        } else if(this.process == Decode.Process.OPTION) {
            Segment segment = (Segment) this.getParent();
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
                if(value0.equals(option.getCode())) {
                    out.setContent(option.getValue());
                    return;
                }
            }
            throw new Exception("there is not a matched element");
        }
        throw new Exception("process type is not supported");
    }

    public String findProtocolName() throws Exception {
        if(Property.isReference(this.value)) {
            Protocol protocol = (Protocol) this.getProtocol();
            String protocolReference = Property.extractReference(this.value);
            if(protocol.getCache().containsKey(protocolReference)) {
                return protocol.getCache().get(protocolReference);
            }
            throw new Exception(String.format("finding protocol name failed [protocol:%s]", protocolReference));
        } else {
            return this.value;
        }
    }
}