package org.typemeta.funcj.codec.xml.io;

import org.typemeta.funcj.codec.CodecException;

import javax.xml.stream.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;

class InputImpl implements XmlIO.Input {

    private final XMLStreamReader rdr;
    private final AttributeMap attrMap = new AttributeMap();

    public InputImpl(XMLStreamReader rdr) {
        this.rdr = rdr;
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
    public void close() {
        try {
            rdr.close();
        } catch (XMLStreamException ex) {
            throw new CodecException(ex);
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
    public boolean readBool() {
        return Boolean.parseBoolean(readText());
    }

    @Override
    public String readStr() {
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
    public double readDbl() {
        return Double.parseDouble(readText());
    }

    @Override
    public BigDecimal readBigDec() {
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
}
