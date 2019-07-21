package org.typemeta.funcj.codec.xmlnode;

import org.typemeta.funcj.codec.CodecConfig;

public abstract class XmlNodeTypes {
    /**
     * Interface for classes which provide configuration information
     * for {@link XmlNodeCodecCore} implementations.
     */
    public interface Config extends CodecConfig {
        String entryElemName();

        String typeAttrName();

        String keyElemName();

        String valueElemName();

        String nullAttrName();

        String nullAttrVal();

        String defaultRootElemName(Class<?> type);

        String defaultRootElemName();
    }
}
