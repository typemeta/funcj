package org.typemeta.funcj.codec.xml;

import org.typemeta.funcj.codec.CodecConfig;

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
