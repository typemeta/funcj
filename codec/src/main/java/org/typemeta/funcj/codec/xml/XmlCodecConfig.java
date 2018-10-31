package org.typemeta.funcj.codec.xml;

import org.typemeta.funcj.codec.CodecConfig;

/**
 * Interface for classes which provide configuration information
 * for {@link XmlCodecCore} implementations.
 */
public interface XmlCodecConfig extends CodecConfig {
    String entryElemName();

    String typeAttrName();

    String keyAttrName();

    String keyElemName();

    String valueElemName();

    String nullAttrName();

    String nullAttrVal();
}
