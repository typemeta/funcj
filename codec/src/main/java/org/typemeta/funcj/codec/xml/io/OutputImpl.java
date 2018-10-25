package org.typemeta.funcj.codec.xml.io;

import org.typemeta.funcj.codec.CodecException;
import org.typemeta.funcj.functions.SideEffectGenEx;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.math.BigDecimal;

class OutputImpl implements XmlIO.Output {

    private final XMLStreamWriter wtr;
    private final SideEffectGenEx.F<OutputImpl, XMLStreamException> closeF;

    public OutputImpl(XMLStreamWriter wtr, SideEffectGenEx.F<OutputImpl, XMLStreamException> closeF) {
        this.wtr = wtr;
        this.closeF = closeF;
    }

    public OutputImpl(XMLStreamWriter wtr) {
        this(wtr, os -> {});
    }

    @Override
    public XmlIO.Output close() {
        try {
            closeF.apply(this);
            wtr.close();
            return this;
        } catch (XMLStreamException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public XmlIO.Output startDocument() {
        try {
            wtr.writeStartDocument();
            return this;
        } catch (XMLStreamException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public XmlIO.Output endDocument() {
        try {
            wtr.writeEndDocument();
            return this;
        } catch (XMLStreamException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public XmlIO.Output startElement(String localName) {
        try {
            wtr.writeStartElement(localName);
            return this;
        } catch (XMLStreamException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public XmlIO.Output emptyElement(String localName) {
        try {
            wtr.writeEmptyElement(localName);
            return this;
        } catch (XMLStreamException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public XmlIO.Output endElement() {
        try {
            wtr.writeEndElement();
            return this;
        } catch (XMLStreamException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public XmlIO.Output attribute(String localName, String value) {
        try {
            wtr.writeAttribute(localName, value);
            return this;
        } catch (XMLStreamException ex) {
            throw new CodecException(ex);
        }
    }

    private XmlIO.Output writeCharacters(String text) {
        try {
            wtr.writeCharacters(text);
            return this;
        } catch (XMLStreamException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public XmlIO.Output writeBool(boolean value) {
        return writeCharacters(Boolean.toString(value));
    }

    @Override
    public XmlIO.Output writeStr(String value) {
        return writeCharacters(XmlUtils.escapeXml(value));
    }

    @Override
    public XmlIO.Output writeChar(char value) {
        return writeCharacters(XmlUtils.escapeXml(Character.toString(value)));
    }

    @Override
    public XmlIO.Output writeNum(byte value) {
        return writeCharacters(Byte.toString(value));
    }

    @Override
    public XmlIO.Output writeNum(short value) {
        return writeCharacters(Short.toString(value));
    }

    @Override
    public XmlIO.Output writeNum(int value) {
        return writeCharacters(Integer.toString(value));
    }

    @Override
    public XmlIO.Output writeNum(long value) {
        return writeCharacters(Long.toString(value));
    }

    @Override
    public XmlIO.Output writeNum(float value) {
        return writeCharacters(Float.toString(value));
    }

    @Override
    public XmlIO.Output writeNum(double value) {
        return writeCharacters(Double.toString(value));
    }

    @Override
    public XmlIO.Output writeNum(Number value) {
        return writeCharacters(value.toString());
    }

    @Override
    public XmlIO.Output writeNum(BigDecimal value) {
        return writeCharacters(value.toString());
    }

    @Override
    public XmlIO.Output writeNum(String value) {
        return writeCharacters(XmlUtils.escapeXml(value));
    }
}
