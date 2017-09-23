package io.typemeta.funcj.codec.xml;

import io.typemeta.funcj.codec.CodecCore;
import org.w3c.dom.Element;

/**
 * Interface for classes which implement an encoding into XML,
 * via the {@link Element} representation for XML values.
 */
public interface XmlCodecCore extends CodecCore<Element> {
}
