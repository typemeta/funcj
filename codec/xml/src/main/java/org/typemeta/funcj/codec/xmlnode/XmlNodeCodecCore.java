package org.typemeta.funcj.codec.xmlnode;

import org.typemeta.funcj.codec.CodecStrAPI;
import org.typemeta.funcj.codec.impl.*;
import org.typemeta.funcj.codec.utils.CodecException;
import org.typemeta.funcj.codec.xmlnode.XmlNodeTypes.Config;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;
import java.io.*;

/**
 * Interface for classes which implement an encoding via XML.
 */
public class XmlNodeCodecCore
        extends CodecCoreDelegate<Element, Element, Config>
        implements CodecStrAPI.IO {

    public static final DocumentBuilder docBuilder;

    static {
        try {
            docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            throw new RuntimeException(ex);
        }
    }

    public XmlNodeCodecCore(XmlNodeCodecFormat format) {
        super(new CodecCoreImpl<>(format));
    }

    public XmlNodeCodecCore(Config config) {
        this(new XmlNodeCodecFormat(config));
    }

    public XmlNodeCodecCore() {
        this(new XmlNodeConfig());
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
     * @return          the writer
     */
    public <T> Writer encode(Class<? super T> type, T value, Writer writer, String rootElemName) {
        final Document doc = docBuilder.newDocument();
        final Element out = doc.createElement(rootElemName);
        encodeImpl(type, value, out);
        return XmlUtils.write(doc, writer, true);
    }

    /**
     * Encode the given value into XML and write the results to the {@link Writer} object.
     * The static type determines whether type information is written to recover the value's
     * dynamic type.
     * @param type      the static type of the value
     * @param value     the value to be encoded
     * @param writer    the writer to which the XML is written
     * @param <T>       the static type of the value
     * @return          the writer
     */
    public <T> Writer encode(Class<? super T> type, T value, Writer writer) {
        final Document doc = docBuilder.newDocument();
        final Element out = doc.createElement(config().defaultRootElemName(type));
        encodeImpl(type, value, out);
        return XmlUtils.write(doc, writer, true);
    }

    /**
     * Encode the given value into XML and write the results to the {@link Writer} object.
     * The static type determines whether type information is written to recover the value's
     * dynamic type.
     * @param type      the static type of the value
     * @param value     the value to be encoded
     * @param os        the output stream to which the XML is written
     * @param rootElemName the name of the root element under which the output data is written
     * @param <T>       the static type of the value
     * @return          the output stream
     */
    public <T> OutputStream encode(Class<? super T> type, T value, OutputStream os, String rootElemName) {
        final Document doc = docBuilder.newDocument();
        final Element out = doc.createElement(rootElemName);
        encodeImpl(type, value, out);
        return XmlUtils.write(doc, os, true);
    }

    /**
     * Encode the given value into XML and write the results to the {@link Writer} object.
     * The static type determines whether type information is written to recover the value's
     * dynamic type.
     * @param type      the static type of the value
     * @param value     the value to be encoded
     * @param os        the output stream to which the XML is written
     * @param <T>       the static type of the value
     * @return          the output stream
     */
    @Override
    public <T> OutputStream encode(Class<? super T> type, T value, OutputStream os) {
        return encode(type, value, os, config().defaultRootElemName(type));
    }


    /**
     * Encode the given value into XML and write the results to the {@link Writer} object.
     * @param value     the value to be encoded
     * @param writer    the writer to which the XML is written
     * @param rootElemName the name of the root element under which the output data is written
     * @param <T>       the static type of the value
     * @return          the writer
     */
    public <T> Writer encode(T value, Writer writer, String rootElemName) {
        return encode(Object.class, value, writer, rootElemName);
    }

    /**
     * Decode a value by reading XML from the given {@link Reader} object.
     * @param type      the static type of the value to be decoded
     * @param is        the reader from which JSON is read
     * @param rootElemName the name of the root element under which the output data is written
     * @param <T>       the static type of the value
     * @return          the decoded value
     */
    public <T> T decode(Class<? super T> type, InputStream is, String rootElemName) {
        try {
            final Document doc = docBuilder.parse(is);
            final Element elem = doc.getDocumentElement();
            if (elem.getNodeName().equals(rootElemName)) {
                return decodeImpl(type, elem);
            } else {
                throw new CodecException(
                        "Root expected to have name " + rootElemName +
                                " but was " + elem.getNodeName()
                );
            }
        } catch (SAXException|IOException ex) {
            throw new CodecException(ex);
        }
    }

    /**
     * Decode a value by reading JSON from the given {@link Reader} object.
     * @param is        the reader from which JSON is read
     * @param rootElemName the name of the root element under which the output data is written
     * @param <T>       the static type of the value
     * @return          the decoded value
     */
    public <T> T decode(InputStream is, String rootElemName) {
        return decode(Object.class, is, rootElemName);
    }

    /**
     * Decode a value by reading JSON from the given {@link Reader} object.
     * @param type      the static type of the value to be decoded
     * @param is        the reader from which JSON is read
     * @param <T>       the static type of the value
     * @return          the decoded value
     */
    @Override
    public <T> T decode(Class<? super T> type, InputStream is) {
        return decode(type, is, config().defaultRootElemName(type));
    }
}
