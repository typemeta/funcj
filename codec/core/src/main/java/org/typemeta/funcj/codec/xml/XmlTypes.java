package org.typemeta.funcj.codec.xml;

import org.typemeta.funcj.codec.CodecConfig;
import org.typemeta.funcj.codec.stream.StreamCodecFormat;
import org.typemeta.funcj.codec.xml.io.*;

import javax.xml.stream.*;
import java.io.*;
import java.math.BigDecimal;

public class XmlTypes {

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

    /**
     * Interface for classes which implement an input stream of XML events
     */
    public interface InStream extends StreamCodecFormat.Input<InStream> {
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

        BigDecimal readBigDecimal();
        Number readNumber();
        String readStringNumber();
    }

    /**
     * Interface for classes which implement an output stream of XML events
     */
    public interface OutStream extends StreamCodecFormat.Output<OutStream> {
        void close();

        OutStream startDocument();
        OutStream endDocument();

        OutStream startElement(String localName);
        OutStream emptyElement(String localName);
        OutStream attribute(String localName, String value);
        OutStream endElement();

        OutStream writeNumber(Number value);
        OutStream writeBigDecimal(BigDecimal value);
        OutStream writeStringNumber(String value);
    }

    public static InStream inputOf(XMLStreamReader reader) {
        return InputImpl.inputOf(reader);
    }

    public static InStream inputOf(Reader reader, String rootElemName) {
        return InputImpl.inputOf(reader, rootElemName);
    }

    public static InStream inputOf(InputStream is, String rootElemName) {
        return InputImpl.inputOf(is, rootElemName);
    }

    public static OutStream outputOf(XMLStreamWriter writer) {
        return OutputImpl.outputOf(writer);
    }

    public static OutStream outputOf(Writer writer, String rootElemName) {
        return OutputImpl.outputOf(writer, rootElemName);
    }

    public static OutStream outputOf(OutputStream os, String rootElemName) {
        return OutputImpl.outputOf(os, rootElemName);
    }
}
