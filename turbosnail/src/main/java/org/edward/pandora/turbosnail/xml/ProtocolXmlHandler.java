package org.edward.pandora.turbosnail.xml;

import org.edward.pandora.turbosnail.xml.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

public class ProtocolXmlHandler extends DefaultHandler {
    private static final Logger logger = LoggerFactory.getLogger(ProtocolXmlHandler.class);

    private Protocol protocol;

    private Segment currentSegment;

    private Position currentPosition;

    private Decode currentDecode;

    private Options currentOptions;

    private List<Option> currentOptionList;

    private Option currentOption;

    public Protocol getProtocol() {
        return this.protocol;
    }

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
            this.protocol = new Protocol();
            this.protocol.setId(id);
            this.protocol.setName(attributes.getValue("name"));
            this.protocol.setDescription(attributes.getValue("description"));
        } else if("segment".equalsIgnoreCase(qName)) {
            String id = attributes.getValue("id");
            logger.info("reading segment element[id:{}]......", id);
            this.currentSegment = new Segment();
            this.currentSegment.setId(id);
            this.currentSegment.setName(attributes.getValue("name"));
            this.currentSegment.setDescription(attributes.getValue("description"));
            this.currentSegment.setValue(attributes.getValue("value"));
            this.currentSegment.setSkip(Boolean.parseBoolean(attributes.getValue("skip")));
        } else if("position".equalsIgnoreCase(qName)) {
            String id = attributes.getValue("id");
            logger.info("reading position element[id:{}]......", id);
            this.currentPosition = new Position();
            this.currentPosition.setId(id);
            this.currentPosition.setName(attributes.getValue("name"));
            this.currentPosition.setDescription(attributes.getValue("description"));
            this.currentPosition.setLength(Integer.parseInt(attributes.getValue("length")));
            this.currentPosition.setVariableLength(Boolean.parseBoolean(attributes
                    .getValue("variableLength")));
            this.currentPosition.setLengthFormula(attributes.getValue("lengthFormula"));
        } else if("decode".equalsIgnoreCase(qName)) {
            String id = attributes.getValue("id");
            logger.info("reading decode element[id:{}]......", id);
            this.currentDecode = new Decode();
            this.currentDecode.setId(id);
            this.currentDecode.setName(attributes.getValue("name"));
            this.currentDecode.setDescription(attributes.getValue("description"));
            this.currentDecode.setType(Decode.Type.get(attributes.getValue("type")));
            this.currentDecode.setProtocolId(attributes.getValue("protocolId"));
            this.currentDecode.setForeignProtocolId(attributes.getValue("foreignProtocolId"));
        } else if("options".equalsIgnoreCase(qName)) {
            String id = attributes.getValue("id");
            logger.info("reading options element[id:{}]......", id);
            this.currentOptions = new Options();
            this.currentOptions.setId(id);
            this.currentOptions.setName(attributes.getValue("name"));
            this.currentOptions.setDescription(attributes.getValue("description"));
            this.currentOptions.setType(Decode.Type.get(attributes.getValue("type")));
        } else if("option".equalsIgnoreCase(qName)) {
            String id = attributes.getValue("id");
            logger.info("reading option element[id:{}]......", id);
            this.currentOption = new Option();
            this.currentOption.setId(id);
            this.currentOption.setName(attributes.getValue("name"));
            this.currentOption.setDescription(attributes.getValue("description"));
            this.currentOption.setValue(attributes.getValue("value"));
            boolean range = Boolean.parseBoolean(attributes.getValue("range"));
            this.currentOption.setRange(range);
            if(range) {
                this.currentOption.setMin(Integer.parseInt(attributes.getValue("min")));
                this.currentOption.setMax(Integer.parseInt(attributes.getValue("max")));
            }
            this.currentOption.setUnit(attributes.getValue("unit"));
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if("segment".equalsIgnoreCase(qName)) {
            if(this.protocol == null) {
                throw new SAXException("there's not a protocol element as its parent");
            }
            if(this.protocol.getSegmentList() == null) {
                this.protocol.setSegmentList(new ArrayList<>());
            }
            this.protocol.getSegmentList().add(this.currentSegment);
        } else if("position".equalsIgnoreCase(qName)) {
            if(this.currentSegment == null) {
                throw new SAXException("there's not a segment element as its parent");
            }
            this.currentSegment.setPosition(this.currentPosition);
        } else if("decode".equalsIgnoreCase(qName)) {
            if(this.currentSegment == null) {
                throw new SAXException("there's not a segment element as its parent");
            }
            this.currentSegment.setDecode(this.currentDecode);
        } else if("options".equalsIgnoreCase(qName)) {
            if(this.currentDecode == null) {
                throw new SAXException("there's not a decode element as its parent");
            }
            this.currentDecode.setOptions(this.currentOptions);
            this.currentOptionList = new ArrayList<>();
            this.currentDecode.setOptionList(this.currentOptionList);
        } else if("option".equalsIgnoreCase(qName)) {
            if(this.currentOptionList == null) {
                throw new SAXException("there's not an options element as its parent");
            }
            this.currentOptionList.add(this.currentOption);
        }
        logger.info("done");
    }

    @Override
    public void endDocument() throws SAXException {
        logger.info("done");
    }
}