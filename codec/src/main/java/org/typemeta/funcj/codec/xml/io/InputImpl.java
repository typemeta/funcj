package org.typemeta.funcj.codec.xml.io;

import org.typemeta.funcj.codec.CodecException;
import org.typemeta.funcj.codec.xml.XmlTypes;

import javax.xml.stream.*;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Map;
import java.util.TreeMap;

public class InputImpl implements XmlTypes.InStream {
    public static XmlTypes.InStream inputOf(XMLStreamReader reader) {
        if (reader.getProperty(XMLInputFactory.IS_COALESCING) != Boolean.TRUE) {
            throw new CodecException("XMLStreamReader must have the '" + XMLInputFactory.IS_COALESCING + "' " +
                    " property set to true");
        } else {
            return new InputImpl(reader);
        }
    }

    public static XmlTypes.InStream inputOf(Reader reader, String rootElemName) {
        try {
            final XMLInputFactory xmlInFact = XMLInputFactory.newFactory();
            xmlInFact.setProperty(XMLInputFactory.IS_COALESCING, true);
            final XMLStreamReader xrdr = xmlInFact.createXMLStreamReader(reader);
            return inputOf(xrdr, rootElemName);
        } catch (XMLStreamException ex) {
            throw new CodecException(ex);
        }
    }

    public static XmlTypes.InStream inputOf(InputStream os, String rootElemName) {
        try {
            final XMLInputFactory xmlInFact = XMLInputFactory.newFactory();
            xmlInFact.setProperty(XMLInputFactory.IS_COALESCING, true);
            final XMLStreamReader xrdr = xmlInFact.createXMLStreamReader(os);
            return inputOf(xrdr, rootElemName);
        } catch (XMLStreamException ex) {
            throw new CodecException(ex);
        }
    }

    public static XmlTypes.InStream inputOf(XMLStreamReader xrdr, String rootElemName) {
        final XmlTypes.InStream in = inputOf(xrdr);
        in.startDocument();
        in.startElement(rootElemName);
        return in;
    }

    protected static class AttributeMapImpl implements AttributeMap {
        protected final Map<String, String> attrMap = new TreeMap<>();

        void load(XMLStreamReader rdr) {
            attrMap.clear();
            final int numAtrrs = rdr.getAttributeCount();
            if (numAtrrs > 0) {
                for (int i = 0; i < numAtrrs; ++i) {
                    final String name = rdr.getAttributeLocalName(i);
                    final String value = rdr.getAttributeValue(i);
                    attrMap.put(name, value);
                }
            }
        }

        void clear() {
            attrMap.clear();
        }

        public boolean hasName(String name) {
            return attrMap.containsKey(name);
        }

        public String getValue(String name) {
            return attrMap.get(name);
        }

        public boolean nameHasValue(String name, String value) {
            return attrMap.containsKey(name) &&
                    attrMap.get(name).equals(value);
        }
    }
    private final XMLStreamReader rdr;
    private final AttributeMapImpl attrMap = new AttributeMapImpl();

    public InputImpl(XMLStreamReader rdr) {
        this.rdr = rdr;
    }

    @Override
    public void close() {
        try {
            rdr.close();
        } catch (XMLStreamException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public String location() {
        return rdr.getLocation().toString();
    }

    @Override
    public boolean hasNext() {
        try {
            return rdr.hasNext();
        } catch (XMLStreamException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public Type next() {
        if (rdr.getEventType() == XMLStreamConstants.START_ELEMENT) {
            attrMap.load(rdr);
        } else if (rdr.getEventType() == XMLStreamConstants.END_ELEMENT) {
            attrMap.clear();
        }
        try {
            for(boolean step = true; step; ) {
                switch (rdr.next()) {
                    case XMLStreamConstants.CHARACTERS:
                    case XMLStreamConstants.CDATA:
                        if (!rdr.isWhiteSpace()) {
                            step = false;
                        }
                        break;
                    case XMLStreamConstants.SPACE:
                    case XMLStreamConstants.PROCESSING_INSTRUCTION:
                    case XMLStreamConstants.COMMENT:
                        break;
                    default:
                        step = false;
                }
            }

        } catch (XMLStreamException ex) {
            throw new CodecException(ex);
        }
        return type();
    }

    @Override
    public Type type() {
        switch (rdr.getEventType()) {
            case XMLStreamConstants.START_DOCUMENT:
                return Type.START_DOCUMENT;
            case XMLStreamConstants.END_DOCUMENT:
                return Type.END_DOCUMENT;
            case XMLStreamConstants.START_ELEMENT:
                return Type.START_ELEMENT;
            case XMLStreamConstants.END_ELEMENT:
                return Type.END_ELEMENT;
            case XMLStreamConstants.CHARACTERS:
                return Type.CHARACTERS;
            default:
                return Type.OTHER;
        }
    }

    @Override
    public void startDocument() {
        if (rdr.getEventType() == XMLStreamConstants.START_DOCUMENT) {
            next();
        } else {
            throw new CodecException("StartDocument expected at " + rdr.getLocation());
        }
    }

    @Override
    public void endDocument() {
        if (rdr.getEventType() == XMLStreamConstants.END_DOCUMENT) {
            next();
        } else {
            throw new CodecException("EndDocument expected at " + rdr.getLocation());
        }
    }

    @Override
    public String startElement() {
        if (rdr.getEventType() == XMLStreamConstants.START_ELEMENT) {
            final String name = rdr.getLocalName();
            next();
            return name;
        } else {
            throw new CodecException("StartElement expected at " + rdr.getLocation());
        }
    }

    @Override
    public void startElement(String name) {
        if (rdr.getEventType() == XMLStreamConstants.START_ELEMENT) {
            final String actName = rdr.getLocalName();
            if (!name.equals(actName)) {
                throw new CodecException("StartElement with name '" + name + "' expected at " + rdr.getLocation());
            }
            next();
        } else {
            throw new CodecException("StartElement expected at " + rdr.getLocation());
        }
    }

    @Override
    public AttributeMap attributeMap() {
        return attrMap;
    }

    @Override
    public void endElement() {
        if (rdr.getEventType() == XMLStreamConstants.END_ELEMENT) {
            next();
        } else {
            throw new CodecException("EndElement expected at " + rdr.getLocation());
        }
    }

    private String readText() {
        if (rdr.getEventType() == XMLStreamConstants.CHARACTERS) {
            final String text = rdr.getText();
            next();
            return text;
        } else {
            throw new CodecException("CharacterData expected at " + rdr.getLocation());
        }
    }


    @Override
    public boolean readBoolean() {
        return Boolean.parseBoolean(readText());
    }

    @Override
    public String readString() {
        return readText();
    }

    @Override
    public char readChar() {
        return readText().charAt(0);
    }

    @Override
    public byte readByte() {
        return Byte.parseByte(readText());
    }

    @Override
    public short readShort() {
        return Short.parseShort(readText());
    }

    @Override
    public int readInt() {
        return Integer.parseInt(readText());
    }

    @Override
    public long readLong() {
        return Long.parseLong(readText());
    }

    @Override
    public float readFloat() {
        return Float.parseFloat(readText());
    }

    @Override
    public double readDouble() {
        return Double.parseDouble(readText());
    }

    @Override
    public BigDecimal readBigDecimal() {
        return new BigDecimal(readText());
    }

    @Override
    public Number readNumber() {
        final String text = readText();
        try {
            return NumberFormat.getInstance().parse(text);
        } catch (ParseException ex) {
            final String excerpt = text.length() > 16 ? text.substring(0, 16) + "..." : text;
            throw new CodecException("Number token '" + excerpt + "' is not a valid number");
        }
    }

    @Override
    public String readStringNumber() {
        return readText();
    }
}
