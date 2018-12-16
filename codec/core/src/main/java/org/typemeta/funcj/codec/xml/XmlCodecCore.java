package org.typemeta.funcj.codec.xml;

import org.typemeta.funcj.codec.*;
import org.typemeta.funcj.codec.stream.StreamCodecCoreDelegate;
import org.typemeta.funcj.codec.xml.XmlTypes.*;

import java.io.*;

/**
 * Interface for classes which implement an encoding via XML.
 */
public class XmlCodecCore
        extends StreamCodecCoreDelegate<InStream, OutStream, Config>
        implements CodecAPI {

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
     * Encode the given value into XML and write the results to the {@link Writer} object.
     * The static type determines whether type information is written to recover the value's
     * dynamic type.
     * @param type      the static type of the value
     * @param value     the value to be encoded
     * @param writer    the output stream to which the XML is written
     * @param rootElemName the name of the root element under which the output data is written
     * @param <T>       the static type of the value
     */
    public <T> void encode(Class<? super T> type, T value, Writer writer, String rootElemName) {
        try(final OutStream out = XmlTypes.outputOf(writer, rootElemName)) {
            encode(type, value, out);
        }
    }

    /**
     * Encode the given value into XML and write the results to the {@link Writer} object.
     * @param value     the value to be encoded
     * @param writer    the output stream to which the XML is written
     * @param rootElemName the name of the root element under which the output data is written
     * @param <T>       the static type of the value
     */
    public <T> void encode(T value, Writer writer, String rootElemName) {
        encode(Object.class, value, writer, rootElemName);
    }

    /**
     * Decode a value by reading JSON from the given {@link Reader} object.
     * @param type      the static type of the value to be decoded.
     * @param reader    the input stream from which JSON is read
     * @param rootElemName the name of the root element under which the output data is written
     * @param <T>       the static type of the value
     * @return          the decoded value
     */
    public <T> T decode(Class<? super T> type, Reader reader, String rootElemName) {
        try (final InStream in = XmlTypes.inputOf(reader, rootElemName)) {
            return decode(type, in);
        }
    }

    /**
     * Decode a value by reading JSON from the given {@link Reader} object.
     * @param reader    the input stream from which JSON is read
     * @param rootElemName the name of the root element under which the output data is written
     * @param <T>       the static type of the value
     * @return          the decoded value
     */
    public <T> T decode(Reader reader, String rootElemName) {
        return decode(Object.class, reader, rootElemName);
    }

    @Override
    public <T> void encode(Class<? super T> clazz, T value, Writer wtr) {
        try(final OutStream out = XmlTypes.outputOf(wtr, clazz.getSimpleName())) {
            encode(clazz, value, out);
        }
    }

    @Override
    public <T> T decode(Class<? super T> clazz, Reader rdr) {
        try(final InStream in = XmlTypes.inputOf(rdr, clazz.getSimpleName())) {
            return decode(clazz, in);
        }
    }

    @Override
    public <T> void encode(Class<? super T> clazz, T value, OutputStream os) {
        try(final OutStream out = XmlTypes.outputOf(os, clazz.getSimpleName())) {
            encode(clazz, value, out);
        }
    }

    @Override
    public <T> T decode(Class<? super T> clazz, InputStream is) {
        try(final InStream in = XmlTypes.inputOf(is, clazz.getSimpleName())) {
            return decode(clazz, in);
        }
    }
}
