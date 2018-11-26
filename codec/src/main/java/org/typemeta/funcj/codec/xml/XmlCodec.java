package org.typemeta.funcj.codec.xml;

import org.typemeta.funcj.codec.CodecConfig;
import org.typemeta.funcj.codec.xml.io.*;

import javax.xml.stream.*;
import java.io.*;
import java.math.BigDecimal;

public class XmlCodec {

    /**
     * Interface for classes which provide configuration information
     * for {@link XmlCodecCore} implementations.
     */
    public interface Config extends CodecConfig {
        String entryElemName();

        String typeAttrName();

        String keyElemName();

        String valueElemName();

        String nullAttrName();

        String nullAttrVal();
    }

    public static Input inputOf(XMLStreamReader reader) {
        return InputImpl.inputOf(reader);
    }

    public static Input inputOf(Reader reader, String rootElemName) {
        return InputImpl.inputOf(reader, rootElemName);
    }

    public static Input inputOf(InputStream is, String rootElemName) {
        return InputImpl.inputOf(is, rootElemName);
    }

    public static Output outputOf(XMLStreamWriter writer) {
        return OutputImpl.outputOf(writer);
    }

    public static Output outputOf(Writer writer, String rootElemName) {
        return OutputImpl.outputOf(writer, rootElemName);
    }

    public static Output outputOf(OutputStream os, String rootElemName) {
        return OutputImpl.outputOf(os, rootElemName);
    }

    public interface Input {
        enum Type {
            START_DOCUMENT,
            END_DOCUMENT,
            START_ELEMENT,
            END_ELEMENT,
            CHARACTERS,
            OTHER
        }

        interface AttributeMap {
            boolean hasName(String name) ;

            String getValue(String name);

            boolean nameHasValue(String name, String value);
        }

        void close();

        String location();

        boolean hasNext();
        Type next();

        Type type();


        void startDocument();
        void endDocument();

        String startElement();
        void startElement(String name);
        AttributeMap attributeMap();
        void endElement();

        boolean readBoolean();

        char readChar();

        byte readByte();
        short readShort();
        int readInt();
        long readLong();
        float readFloat();
        double readDouble();
        BigDecimal readBigDecimal();
        Number readNumber();
        String readStringNumber();

        String readString();
    }

    public interface Output {
        Output close();

        Output startDocument();
        Output endDocument();

        Output startElement(String localName);
        Output emptyElement(String localName);
        Output attribute(String localName, String value);
        Output endElement();

        Output writeBoolean(boolean value);

        Output writeChar(char value);

        Output writeByte(byte value);
        Output writeShort(short value);
        Output writeInt(int value);
        Output writeLong(long value);
        Output writeFloat(float value);
        Output writeDouble(double value);
        Output writeNumber(Number value);
        Output writeBigDecimal(BigDecimal value);
        Output writeStringNumber(String value);

        Output writeString(String value);
    }
}
