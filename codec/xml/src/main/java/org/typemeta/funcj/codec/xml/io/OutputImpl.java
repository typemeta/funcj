package org.typemeta.funcj.codec.xml.io;

import org.typemeta.funcj.codec.utils.CodecException;
import org.typemeta.funcj.codec.xml.XmlTypes;
import org.typemeta.funcj.functions.SideEffectGenEx;

import javax.xml.stream.*;
import java.io.OutputStream;
import java.io.Writer;
import java.math.BigDecimal;

public class OutputImpl implements XmlTypes.OutStream {

    public static XmlTypes.OutStream outputOf(XMLStreamWriter writer) {
        if (writer.getProperty("escapeCharacters") != Boolean.FALSE) {
            throw new CodecException("XMLStreamWriter must have the 'escapeCharacters' property set to false");
        } else {
            return new OutputImpl(writer);
        }
    }

    public static OutputImpl outputOf(Writer writer, String rootElemName) {
        try {
            final XMLOutputFactory xmlOutFact = XMLOutputFactory.newInstance();
            xmlOutFact.setProperty("escapeCharacters", true);
            final XMLStreamWriter xwtr = xmlOutFact.createXMLStreamWriter(writer);
            return outputOf(xwtr, rootElemName);
        } catch (XMLStreamException ex) {
            throw new CodecException(ex);
        }
    }

    public static OutputImpl outputOf(OutputStream os, String rootElemName) {
        try {
            final XMLOutputFactory xmlOutFact = XMLOutputFactory.newInstance();
            xmlOutFact.setProperty("escapeCharacters", true);
            final XMLStreamWriter xwtr = xmlOutFact.createXMLStreamWriter(os);
            return outputOf(xwtr, rootElemName);
        } catch (XMLStreamException ex) {
            throw new CodecException(ex);
        }
    }

    public static OutputImpl outputOf(XMLStreamWriter xwtr, String rootElemName) throws XMLStreamException {
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
    public void close() {
        try {
            closeF.apply(this);
            wtr.close();
        } catch (XMLStreamException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public OutputImpl startDocument() {
        try {
            wtr.writeStartDocument();
            return this;
        } catch (XMLStreamException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public OutputImpl endDocument() {
        try {
            wtr.writeEndDocument();
            return this;
        } catch (XMLStreamException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public OutputImpl startElement(String localName) {
        try {
            wtr.writeStartElement(localName);
            return this;
        } catch (XMLStreamException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public OutputImpl emptyElement(String localName) {
        try {
            wtr.writeEmptyElement(localName);
            return this;
        } catch (XMLStreamException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public OutputImpl endElement() {
        try {
            wtr.writeEndElement();
            return this;
        } catch (XMLStreamException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public OutputImpl attribute(String localName, String value) {
        try {
            wtr.writeAttribute(localName, value);
            return this;
        } catch (XMLStreamException ex) {
            throw new CodecException(ex);
        }
    }

    private OutputImpl writeCharacters(String text) {
        try {
            wtr.writeCharacters(text);
            return this;
        } catch (XMLStreamException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public OutputImpl writeBoolean(boolean value) {
        return writeCharacters(Boolean.toString(value));
    }

    @Override
    public OutputImpl writeString(String value) {
        return writeCharacters(XmlUtils.escapeTextChar(value));
    }

    @Override
    public OutputImpl writeChar(char value) {
        return writeCharacters(XmlUtils.escapeTextChar(Character.toString(value)));
    }

    @Override
    public OutputImpl writeByte(byte value) {
        return writeCharacters(Byte.toString(value));
    }

    @Override
    public OutputImpl writeShort(short value) {
        return writeCharacters(Short.toString(value));
    }

    @Override
    public OutputImpl writeInt(int value) {
        return writeCharacters(Integer.toString(value));
    }

    @Override
    public OutputImpl writeLong(long value) {
        return writeCharacters(Long.toString(value));
    }

    @Override
    public OutputImpl writeFloat(float value) {
        return writeCharacters(Float.toString(value));
    }

    @Override
    public OutputImpl writeDouble(double value) {
        return writeCharacters(Double.toString(value));
    }

    @Override
    public OutputImpl writeNumber(Number value) {
        return writeCharacters(value.toString());
    }

    @Override
    public OutputImpl writeBigDecimal(BigDecimal value) {
        return writeCharacters(value.toString());
    }

    @Override
    public OutputImpl writeStringNumber(String value) {
        return writeCharacters(XmlUtils.escapeTextChar(value));
    }
}
