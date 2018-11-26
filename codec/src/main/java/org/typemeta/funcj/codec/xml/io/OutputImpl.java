package org.typemeta.funcj.codec.xml.io;

import org.typemeta.funcj.codec.CodecException;
import org.typemeta.funcj.codec.xml.XmlCodec;
import org.typemeta.funcj.functions.SideEffectGenEx;

import javax.xml.stream.*;
import java.io.OutputStream;
import java.io.Writer;
import java.math.BigDecimal;

public class OutputImpl implements XmlCodec.Output {

    public static XmlCodec.Output outputOf(XMLStreamWriter writer) {
        if (writer.getProperty("escapeCharacters") != Boolean.TRUE) {
            throw new CodecException("XMLStreamWriter must have the 'escapeCharacters' property set to true");
        } else {
            return new OutputImpl(writer);
        }
    }

    public static XmlCodec.Output outputOf(Writer writer, String rootElemName) {
        try {
            final XMLOutputFactory xmlOutFact = XMLOutputFactory.newInstance();
            xmlOutFact.setProperty("escapeCharacters", false);
            final XMLStreamWriter xwtr = xmlOutFact.createXMLStreamWriter(writer);
            return outputOf(xwtr, rootElemName);
        } catch (XMLStreamException ex) {
            throw new CodecException(ex);
        }
    }

    public static XmlCodec.Output outputOf(OutputStream os, String rootElemName) {
        try {
            final XMLOutputFactory xmlOutFact = XMLOutputFactory.newInstance();
            xmlOutFact.setProperty("escapeCharacters", false);
            final XMLStreamWriter xwtr = xmlOutFact.createXMLStreamWriter(os);
            return outputOf(xwtr, rootElemName);
        } catch (XMLStreamException ex) {
            throw new CodecException(ex);
        }
    }

    public static XmlCodec.Output outputOf(XMLStreamWriter xwtr, String rootElemName) throws XMLStreamException {
        xwtr.writeStartDocument();
        xwtr.writeStartElement(rootElemName);

        return new OutputImpl(xwtr, out -> {
            xwtr.writeEndElement();
            xwtr.writeEndDocument();
        });
    }

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
    public XmlCodec.Output close() {
        try {
            closeF.apply(this);
            wtr.close();
            return this;
        } catch (XMLStreamException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public XmlCodec.Output startDocument() {
        try {
            wtr.writeStartDocument();
            return this;
        } catch (XMLStreamException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public XmlCodec.Output endDocument() {
        try {
            wtr.writeEndDocument();
            return this;
        } catch (XMLStreamException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public XmlCodec.Output startElement(String localName) {
        try {
            wtr.writeStartElement(localName);
            return this;
        } catch (XMLStreamException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public XmlCodec.Output emptyElement(String localName) {
        try {
            wtr.writeEmptyElement(localName);
            return this;
        } catch (XMLStreamException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public XmlCodec.Output endElement() {
        try {
            wtr.writeEndElement();
            return this;
        } catch (XMLStreamException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public XmlCodec.Output attribute(String localName, String value) {
        try {
            wtr.writeAttribute(localName, value);
            return this;
        } catch (XMLStreamException ex) {
            throw new CodecException(ex);
        }
    }

    private XmlCodec.Output writeCharacters(String text) {
        try {
            wtr.writeCharacters(text);
            return this;
        } catch (XMLStreamException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public XmlCodec.Output writeBoolean(boolean value) {
        return writeCharacters(Boolean.toString(value));
    }

    @Override
    public XmlCodec.Output writeString(String value) {
        return writeCharacters(XmlUtils.escapeXml(value));
    }

    @Override
    public XmlCodec.Output writeChar(char value) {
        return writeCharacters(XmlUtils.escapeXml(Character.toString(value)));
    }

    @Override
    public XmlCodec.Output writeByte(byte value) {
        return writeCharacters(Byte.toString(value));
    }

    @Override
    public XmlCodec.Output writeShort(short value) {
        return writeCharacters(Short.toString(value));
    }

    @Override
    public XmlCodec.Output writeInt(int value) {
        return writeCharacters(Integer.toString(value));
    }

    @Override
    public XmlCodec.Output writeLong(long value) {
        return writeCharacters(Long.toString(value));
    }

    @Override
    public XmlCodec.Output writeFloat(float value) {
        return writeCharacters(Float.toString(value));
    }

    @Override
    public XmlCodec.Output writeDouble(double value) {
        return writeCharacters(Double.toString(value));
    }

    @Override
    public XmlCodec.Output writeNumber(Number value) {
        return writeCharacters(value.toString());
    }

    @Override
    public XmlCodec.Output writeBigDecimal(BigDecimal value) {
        return writeCharacters(value.toString());
    }

    @Override
    public XmlCodec.Output writeStringNumber(String value) {
        return writeCharacters(XmlUtils.escapeXml(value));
    }
}
