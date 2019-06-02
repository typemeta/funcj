package org.typemeta.funcj.codec.xmlnode;

import org.typemeta.funcj.codec.CodecConfig;

/**
 * Interface for classes which provide configuration information
 * for {@link XmlNodeCodecCore} implementations.
 */
public interface XmlNodeConfig extends CodecConfig {
    String entryElemName();

    String typeAttrName();

    String keyElemName();

    String valueElemName();

    String nullAttrName();

    String nullAttrVal();
}
