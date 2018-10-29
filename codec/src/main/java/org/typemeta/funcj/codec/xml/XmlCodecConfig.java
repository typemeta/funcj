package org.typemeta.funcj.codec.xml;

import org.typemeta.funcj.codec.CodecConfig;

public class XmlCodecConfig extends CodecConfig {

    public String entryElemName() {
        return "_";
    }

    public String typeAttrName() {
        return "type";
    }

    public String keyAttrName() {
        return "key";
    }

    public String keyElemName() {
        return "key";
    }

    public String valueElemName() {
        return "value";
    }

    public String nullAttrName() {
        return "null";
    }

    public String nullAttrVal() {
        return "true";
    }
}
