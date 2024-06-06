package org.edward.pandora.turbosnail.xml;

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

    @Override
    public void startDocument() throws SAXException {
        logger.info("reading protocol file......");
    }

    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {
        if("protocol".equalsIgnoreCase(qName)) {
            String id = attributes.getValue("id");
            logger.info("reading protocol element[id:{}]......", id);
            this.protocol.setId(id);
            this.protocol.setName(attributes.getValue("name"));
            this.protocol.setDescription(attributes.getValue("description"));
            this.protocol.setSegmentList(new ArrayList<>());
        } else if("segment".equalsIgnoreCase(qName)) {
            String id = attributes.getValue("id");
            logger.info("reading segment element[id:{}]......", id);
            this.currentSegment = new Segment();
            this.currentSegment.setId(id);
            this.currentSegment.setName(attributes.getValue("name"));
            this.currentSegment.setDescription(attributes.getValue("description"));
            this.currentSegment.setProtocol(this.protocol);
            this.currentSegment.setSkip(Boolean.parseBoolean(attributes.getValue("skip")));
        } else if("position".equalsIgnoreCase(qName)) {
            String id = attributes.getValue("id");
            logger.info("reading position element[id:{}]......", id);
            this.currentPosition = new Position();
            this.currentPosition.setId(id);
            this.currentPosition.setName(attributes.getValue("name"));
            this.currentPosition.setDescription(attributes.getValue("description"));
            String length = attributes.getValue("length");
            if(Property.isFormula(length)) {
                this.currentPosition.setVariableLength(true);
                String lengthFormula = Property.extractFormula(length);
                this.currentPosition.setLengthFormula(lengthFormula);
                String[] operators = Property.extractOperators(lengthFormula);
                for(String operator : operators) {
                    this.protocol.getCache().put(operator, "");
                }
            } else {
                this.currentPosition.setLength(Integer.parseInt(length));
            }
        } else if("decode".equalsIgnoreCase(qName)) {
            String id = attributes.getValue("id");
            logger.info("reading decode element[id:{}]......", id);
            this.currentDecode = new Decode();
            this.currentDecode.setId(id);
            this.currentDecode.setName(attributes.getValue("name"));
            this.currentDecode.setDescription(attributes.getValue("description"));
            this.currentDecode.setType(Decode.Type.get(attributes.getValue("type")));
            this.currentDecode.setProcess(Decode.Process.get(attributes.getValue("process")));
            String value = attributes.getValue("value");
            this.currentDecode.setValue(value);
            if(StringUtils.isNotBlank(value) && Property.isReference(value)) {
                String valueReference = Property.extractReference(value);
                this.protocol.getCache().put(valueReference, "");
            }
            this.currentDecode.setProtocol(Boolean.parseBoolean(attributes.getValue("protocol")));
        } else if("options".equalsIgnoreCase(qName)) {
            String id = attributes.getValue("id");
            logger.info("reading options element[id:{}]......", id);
            this.currentOptions = new Options();
            this.currentOptions.setId(id);
            this.currentOptions.setName(attributes.getValue("name"));
            this.currentOptions.setDescription(attributes.getValue("description"));
            this.currentSegment.setOptionList(new ArrayList<>());
        } else if("option".equalsIgnoreCase(qName)) {
            String id = attributes.getValue("id");
            logger.info("reading option element[id:{}]......", id);
            this.currentOption = new Option();
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
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if("segment".equalsIgnoreCase(qName)) {
            if(this.protocol == null) {
                throw new SAXException("there's not a protocol element as its parent[segment:"+qName+"]");
            }
            this.protocol.getSegmentList().add(this.currentSegment);
        } else if("position".equalsIgnoreCase(qName)) {
            if(this.currentSegment == null) {
                throw new SAXException("there's not a segment element as its parent[position:"+qName+"]");
            }
            this.currentSegment.setPosition(this.currentPosition);
        } else if("decode".equalsIgnoreCase(qName)) {
            if(this.currentSegment == null) {
                throw new SAXException("there's not a segment element as its parent[decode:"+qName+"]");
            }
            this.currentSegment.setDecode(this.currentDecode);
        } else if("options".equalsIgnoreCase(qName)) {
            if(this.currentSegment == null) {
                throw new SAXException("there's not a segment element as its parent[options:"+qName+"]");
            }
            this.currentSegment.setOptions(this.currentOptions);
        } else if("option".equalsIgnoreCase(qName)) {
            if(this.currentSegment.getOptionList() == null) {
                throw new SAXException("there's not an options element as its parent[option:"+qName+"]");
            }
            this.currentSegment.getOptionList().add(this.currentOption);
        }
        logger.info("done");
    }

    @Override
    public void endDocument() throws SAXException {
        logger.info("done");
    }
}