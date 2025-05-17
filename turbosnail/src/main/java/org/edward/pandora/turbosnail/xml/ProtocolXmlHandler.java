package org.edward.pandora.turbosnail.xml;

import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.JexlExpression;
import org.apache.commons.lang3.StringUtils;
import org.edward.pandora.turbosnail.xml.model.property.Property;
import org.edward.pandora.turbosnail.xml.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;

public class ProtocolXmlHandler extends DefaultHandler {
    private static final Logger logger = LoggerFactory.getLogger(ProtocolXmlHandler.class);
    private static final JexlEngine jexlEngine;

    static {
        jexlEngine = new JexlBuilder().create();
    }

    private final Protocol protocol;

    public ProtocolXmlHandler() {
        this.protocol = new Protocol();
    }

    public Protocol getProtocol() {
        return this.protocol;
    }

    private Segment currentSegment;
    private Position currentPosition;
    private Decode currentDecode;
    private Options currentOptions;
    private Option currentOption;
    private Multi currentMulti;

    @Override
    public void startDocument() throws SAXException {
        logger.info(">> reading protocol file");
    }

    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {
        if("protocol".equalsIgnoreCase(qName)) {
            String id = attributes.getValue("id");
            logger.info("reading protocol element [id:{}]", id);
            this.protocol.setId(id);
            this.protocol.setName(attributes.getValue("name"));
            this.protocol.setDescription(attributes.getValue("description"));
            this.protocol.setElementList(new ArrayList<>());
        } else if("segment".equalsIgnoreCase(qName)) {
            String id = attributes.getValue("id");
            logger.info("reading segment element [id:{}]", id);
            this.currentSegment = new Segment();
            this.currentSegment.setProtocol(this.protocol);
            this.currentSegment.setParent(this.protocol);
            this.currentSegment.setId(id);
            this.currentSegment.setName(attributes.getValue("name"));
            this.currentSegment.setDescription(attributes.getValue("description"));
            this.currentSegment.setSkip(Boolean.parseBoolean(attributes.getValue("skip")));
            this.currentSegment.setMulti(Boolean.parseBoolean(attributes.getValue("multi")));
            String count = attributes.getValue("count");
            if(StringUtils.isNotBlank(count)) {
                this.currentSegment.setCount(Integer.parseInt(count));
            }
        } else if("position".equalsIgnoreCase(qName)) {
            String id = attributes.getValue("id");
            logger.info("reading position element [id:{}]", id);
            this.currentPosition = new Position();
            this.currentPosition.setProtocol(this.protocol);
            this.currentPosition.setParent(this.currentSegment);
            this.currentPosition.setId(id);
            this.currentPosition.setName(attributes.getValue("name"));
            this.currentPosition.setDescription(attributes.getValue("description"));
            String length = attributes.getValue("length");
            if(Property.isFormula(length)) {
                this.currentPosition.setVariableLength(true);
                String lengthFormula = Property.extractFormula(length);
                this.currentPosition.setLengthFormula(lengthFormula);
                String[] operators = Property.extractOperators(lengthFormula);
                if(operators==null || operators.length==0) {
                    throw new SAXException("there are no operators in formula");
                }
                this.currentPosition.setOperators(operators);
                for(String operator : operators) {
                    this.protocol.getCacheSet().add(this.currentPosition.convertUniqueCode(operator));
                }
                JexlExpression jexlExpression = jexlEngine.createExpression(lengthFormula);
                this.currentPosition.setJexlExpression(jexlExpression);
            } else {
                this.currentPosition.setLength(Integer.parseInt(length));
            }
        } else if("decode".equalsIgnoreCase(qName)) {
            String id = attributes.getValue("id");
            logger.info("reading decode element [id:{}]", id);
            this.currentDecode = new Decode();
            this.currentDecode.setProtocol(this.protocol);
            this.currentDecode.setParent(this.currentSegment);
            this.currentDecode.setId(id);
            this.currentDecode.setName(attributes.getValue("name"));
            this.currentDecode.setDescription(attributes.getValue("description"));
            this.currentDecode.setType(Decode.Type.get(attributes.getValue("type")));
            this.currentDecode.setProcess(Decode.Process.get(attributes.getValue("process")));
            String value = attributes.getValue("value");
            this.currentDecode.setValue(value);
            if(StringUtils.isNotBlank(value) && Property.isReference(value)) {
                String valueReference = Property.extractReference(value);
                this.protocol.getCacheSet().add(this.currentDecode.convertUniqueCode(valueReference));
            }
            this.currentDecode.setProtocol(Boolean.parseBoolean(attributes.getValue("protocol")));
            this.currentDecode.setIgnoreCase(Boolean.parseBoolean(attributes.getValue("ignore_case")));
        } else if("options".equalsIgnoreCase(qName)) {
            String id = attributes.getValue("id");
            logger.info("reading options element [id:{}]", id);
            this.currentOptions = new Options();
            this.currentOptions.setProtocol(this.protocol);
            this.currentOptions.setParent(this.currentSegment);
            this.currentOptions.setId(id);
            this.currentOptions.setName(attributes.getValue("name"));
            this.currentOptions.setDescription(attributes.getValue("description"));
            this.currentSegment.setOptionList(new ArrayList<>());
        } else if("option".equalsIgnoreCase(qName)) {
            String id = attributes.getValue("id");
            logger.info("reading option element [id:{}]", id);
            this.currentOption = new Option();
            this.currentOption.setProtocol(this.protocol);
            this.currentOption.setParent(this.currentOptions);
            this.currentOption.setId(id);
            this.currentOption.setName(attributes.getValue("name"));
            this.currentOption.setDescription(attributes.getValue("description"));
            this.currentOption.setCode(attributes.getValue("code"));
            this.currentOption.setValue(attributes.getValue("value"));
            boolean range = Boolean.parseBoolean(attributes.getValue("range"));
            this.currentOption.setRange(range);
            if(range) {
                this.currentOption.setMin(Integer.parseInt(attributes.getValue("min"), 16));
                this.currentOption.setMax(Integer.parseInt(attributes.getValue("max"), 16));
            }
            this.currentOption.setUnit(attributes.getValue("unit"));
        } else if("multi".equalsIgnoreCase(qName)) {
            String id = attributes.getValue("id");
            logger.info("reading multi element [id:{}]", id);
            this.currentMulti = new Multi();
            this.currentMulti.setProtocol(this.protocol);
            this.currentMulti.setParent(this.protocol);
            this.currentMulti.setId(id);
            this.currentMulti.setName(attributes.getValue("name"));
            this.currentMulti.setDescription(attributes.getValue("description"));
            this.currentMulti.setSegmentList(new ArrayList<>());
            String count = attributes.getValue("count");
            if(StringUtils.isNotBlank(count)) {
                this.currentMulti.setCount(Integer.parseInt(count));
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if("segment".equalsIgnoreCase(qName)) {
            if(this.protocol == null) {
                throw new SAXException(String.format("there's not a protocol element" +
                        " as its parent [segment:%s]", qName));
            }
            if(this.currentMulti == null) {
                this.protocol.getElementList().add(this.currentSegment);
            } else {
                this.currentMulti.getSegmentList().add(this.currentSegment);
            }
            this.currentSegment = null;
        } else if("position".equalsIgnoreCase(qName)) {
            if(this.currentSegment == null) {
                throw new SAXException(String.format("there's not a segment element" +
                        " as its parent [position:%s]", qName));
            }
            this.currentSegment.setPosition(this.currentPosition);
            this.currentPosition = null;
        } else if("decode".equalsIgnoreCase(qName)) {
            if(this.currentSegment == null) {
                throw new SAXException(String.format("there's not a segment element" +
                        " as its parent [decode:%s]", qName));
            }
            this.currentSegment.setDecode(this.currentDecode);
            this.currentDecode = null;
        } else if("options".equalsIgnoreCase(qName)) {
            if(this.currentSegment == null) {
                throw new SAXException(String.format("there's not a segment element" +
                        " as its parent [options:%s]", qName));
            }
            this.currentSegment.setOptions(this.currentOptions);
            this.currentOptions = null;
        } else if("option".equalsIgnoreCase(qName)) {
            if(this.currentSegment.getOptionList() == null) {
                throw new SAXException(String.format("there's not an options element" +
                        " as its parent [option:%s]", qName));
            }
            this.currentSegment.getOptionList().add(this.currentOption);
            this.currentOption = null;
        } else if("multi".equalsIgnoreCase(qName)) {
            if(this.protocol == null) {
                throw new SAXException(String.format("there's not a protocol element" +
                        " as its parent [multi:%s]", qName));
            }
            this.protocol.getElementList().add(this.currentMulti);
            this.currentMulti = null;
        }
        logger.info("done");
    }

    @Override
    public void endDocument() throws SAXException {
        logger.info(">> done");
    }
}