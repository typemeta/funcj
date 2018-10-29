package org.typemeta.funcj.codec.json;

import org.typemeta.funcj.codec.CodecConfig;

public class JsonCodecConfig extends CodecConfig {

    public String typeFieldName() {
        return "@type";
    }

    public String keyFieldName() {
        return "@key";
    }

    public String valueFieldName() {
        return "@value";
    }
}
