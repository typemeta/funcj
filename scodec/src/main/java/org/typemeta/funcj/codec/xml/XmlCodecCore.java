package org.typemeta.funcj.codec.xml;

import org.typemeta.funcj.codec.CodecCoreIntl;
import org.w3c.dom.Element;

/**
 * Interface for classes which implement an encoding into XML,
 * via the {@link Element} representation for XML values.
 */
public interface XmlCodecCore extends CodecCoreIntl<Element, Element> {
}
