package org.typemeta.funcj.codec.xml;

import org.typemeta.funcj.codec.CodecCoreInternal;
import org.typemeta.funcj.codec.xml.io.XmlIO;
import org.typemeta.funcj.codec.xml.io.XmlIO.*;
import org.w3c.dom.Element;

import java.io.*;

/**
 * Interface for classes which implement an encoding via XML.
 */
public interface XmlCodecCore extends CodecCoreInternal<Input, Output> {
    <T> Output encode(Class<T> type, T val, Writer wtr, String rootElemName);

    <T> T decode(Class<T> type, Reader rdr, String rootElemName);
}
