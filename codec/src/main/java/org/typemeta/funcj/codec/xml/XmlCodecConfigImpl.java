package org.typemeta.funcj.codec.xml;

import org.typemeta.funcj.codec.CodecConfigImpl;

/**
 * Base class for {@link XmlCodecConfig} implementations.
 */
public class XmlCodecConfigImpl extends CodecConfigImpl implements XmlCodecConfig {

    @Override
    public String entryElemName() {
        return "_";
    }

    @Override
    public String typeAttrName() {
        return "type";
    }

    @Override
    public String keyAttrName() {
        return "key";
    }

    @Override
    public String keyElemName() {
        return "key";
    }

    @Override
    public String valueElemName() {
        return "value";
    }

    @Override
    public String nullAttrName() {
        return "null";
    }

    @Override
    public String nullAttrVal() {
        return "true";
    }
}
