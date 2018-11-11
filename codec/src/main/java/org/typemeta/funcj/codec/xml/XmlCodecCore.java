package org.typemeta.funcj.codec.xml;

import org.typemeta.funcj.codec.*;
import org.typemeta.funcj.codec.xml.XmlCodec.*;

import java.io.*;

/**
 * Interface for classes which implement an encoding via XML.
 */
public class XmlCodecCore extends CodecCoreDelegate<Input, Output, Config> {
    public XmlCodecCore(XmlCodecFormat format) {
        super(new CodecCoreImpl<>(format));
    }

    public XmlCodecCore(Config config) {
        this(new XmlCodecFormat(config));
    }

    public XmlCodecCore() {
        this(new XmlConfigImpl());
    }

    /**
     * Encode the supplied value into XML and write the results to the {@link Writer} object.
     * The static type determines whether type information is written to recover the value's
     * dynamic type.
     * @param type      the static type of the value
     * @param value     the value to be encoded
     * @param writer    the output stream to which the XML is written
     * @param rootElemName the name of the root element under which the output data is written
     * @param <T>       the static type of the value
     * @return          the output stream
     */
    public <T> Output encode(Class<? super T> type, T value, Writer writer, String rootElemName) {
        final Output out = encode(type, value, XmlCodec.outputOf(writer, rootElemName));
        return out.close();
    }

    /**
     * Encode the supplied value into XML and write the results to the {@link Writer} object.
     * @param value     the value to be encoded
     * @param writer    the output stream to which the XML is written
     * @param rootElemName the name of the root element under which the output data is written
     * @param <T>       the static type of the value
     * @return          the output stream
     */
    public <T> Output encode(T value, Writer writer, String rootElemName) {
        return encode(Object.class, value, writer, rootElemName);
    }

    /**
     * Decode a value by reading JSON from the supplied {@link Reader} object.
     * @param type      the static type of the value to be decoded.
     * @param reader    the input stream from which JSON is read
     * @param rootElemName the name of the root element under which the output data is written
     * @param <T>       the static type of the value
     * @return          the decoded value
     */
    public <T> T decode(Class<? super T> type, Reader reader, String rootElemName) {
        return decode(type, XmlCodec.inputOf(reader, rootElemName));
    }

    /**
     * Decode a value by reading JSON from the supplied {@link Reader} object.
     * @param reader    the input stream from which JSON is read
     * @param rootElemName the name of the root element under which the output data is written
     * @param <T>       the static type of the value
     * @return          the decoded value
     */
    public <T> T decode(Reader reader, String rootElemName) {
        return decode(Object.class, reader, rootElemName);
    }
}
