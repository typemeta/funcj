package org.typemeta.funcj.codec.xml;

import org.typemeta.funcj.codec.*;
import org.typemeta.funcj.codec.impl.CodecCoreImpl;
import org.typemeta.funcj.codec.xml.XmlTypes.*;

import java.io.*;

/**
 * Interface for classes which implement an encoding via XML.
 */
public class XmlCodecCore
        extends CodecCoreDelegate<InStream, OutStream, Config>
        implements CodecAPI.IO {

    public XmlCodecCore(XmlCodecFormat format) {
        super(new CodecCoreImpl<>(format));
    }

    public XmlCodecCore(Config config) {
        this(new XmlCodecFormat(config));
    }

    public XmlCodecCore() {
        this(new XmlCodecFormat());
    }

    /**
     * Encode the given value into XML and write the results to the {@link Writer} object.
     * The static type determines whether type information is written to recover the value's
     * dynamic type.
     * @param type      the static type of the value
     * @param value     the value to encode
     * @param os        the output stream to which the XML is written
     * @param <T>       the static type of the value
     * @return          the output stream
     */
    @Override
    public <T> OutputStream encode(Class<? super T> type, T value, OutputStream os) {
        try(final OutStream out = XmlTypes.outputOf(os, config().defaultRootElemName(type))) {
            encodeImpl(type, value, out);
            return os;
        }
    }

    /**
     * Encode the given value into XML and write the results to the {@link Writer} object.
     * The static type determines whether type information is written to recover the value's
     * dynamic type.
     * @param type      the static type of the value
     * @param value     the value to be encoded
     * @param writer    the writer to which the XML is written
     * @param rootElemName the name of the root element under which the output data is written
     * @param <T>       the static type of the value
     */
    public <T> void encode(Class<? super T> type, T value, Writer writer, String rootElemName) {
        try(final OutStream out = XmlTypes.outputOf(writer, rootElemName)) {
            encodeImpl(type, value, out);
        }
    }

    /**
     * Encode the given value into XML and write the results to the {@link Writer} object.
     * The static type determines whether type information is written to recover the value's
     * dynamic type.
     * @param type      the static type of the value
     * @param value     the value to be encoded
     * @param writer    the writer to which the XML is written
     * @param <T>       the static type of the value
     */
    public <T> void encode(Class<? super T> type, T value, Writer writer) {
        encode(type, value, writer, config().defaultRootElemName(type));
    }

    /**
     * Encode the given value into XML and write the results to the {@link Writer} object.
     * @param value     the value to be encoded
     * @param writer    the writer to which the XML is written
     * @param rootElemName the name of the root element under which the output data is written
     * @param <T>       the static type of the value
     */
    public <T> void encode(T value, Writer writer, String rootElemName) {
        encode(Object.class, value, writer, rootElemName);
    }

    /**
     * Encode the given value into XML and write the results to the {@link Writer} object.
     * @param value     the value to be encoded
     * @param writer    the writer to which the XML is written
     * @param <T>       the static type of the value
     */
    public <T> void encode(T value, Writer writer) {
        encode(Object.class, value, writer, config().defaultRootElemName());
    }

    /**
     * Encode the given value into XML and write the results to the {@link InputStream} object.
     * @param type      the static type of the value to be encoded.
     * @param is        the input stream from which XML is read
     * @param <T>       the static type of the value
     * @return          the decoded value
     */
    @Override
    public <T> T decode(Class<? super T> type, InputStream is) {
        try(final InStream in = XmlTypes.inputOf(is, config().defaultRootElemName(type))) {
            return decodeImpl(type, in);
        }
    }

    /**
     * Decode a value by reading XML from the given {@link Reader} object.
     * @param type      the static type of the value to be decoded.
     * @param reader    the reader from which the XML is read
     * @param rootElemName the name of the root element under which the output data is written
     * @param <T>       the static type of the value
     * @return          the decoded value
     */
    public <T> T decode(Class<? super T> type, Reader reader, String rootElemName) {
        try (final InStream in = XmlTypes.inputOf(reader, rootElemName)) {
            return decodeImpl(type, in);
        }
    }

    /**
     * Decode a value by reading XML from the given {@link Reader} object.
     * @param type      the static type of the value to be decoded.
     * @param reader    the input stream from which XML is read
     * @param <T>       the static type of the value
     * @return          the decoded value
     */
    public <T> T decode(Class<? super T> type, Reader reader) {
        return decode(type, reader, config().defaultRootElemName(type));
    }

    /**
     * Decode a value by reading XML from the given {@link Reader} object.
     * @param reader    the reader from which XML is read
     * @param rootElemName the name of the root element under which the output data is written
     * @param <T>       the static type of the value
     * @return          the decoded value
     */
    public <T> T decode(Reader reader, String rootElemName) {
        return decode(Object.class, reader, rootElemName);
    }

    /**
     * Decode a value by reading XML from the given {@link Reader} object.
     * @param reader    the reader from which XML is read
     * @param <T>       the static type of the value
     * @return          the decoded value
     */
    public <T> T decode(Reader reader) {
        return decode(reader, config().defaultRootElemName());
    }
}
