package org.typemeta.funcj.codec.xml;

import org.typemeta.funcj.codec.CodecCoreIntl;
import org.typemeta.funcj.codec.xml.io.XmlIO;
import org.w3c.dom.Element;

import java.io.*;

/**
 * Interface for classes which implement an encoding into XML,
 * via the {@link Element} representation for XML values.
 */
public interface XmlCodecCore extends CodecCoreIntl<XmlIO.Input, XmlIO.Output> {
    <T> XmlIO.Output encode(Class<T> type, T val, Writer wtr, String rootElemName);

    <T> T decode(Class<T> type, Reader rdr, String rootElemName);
}
