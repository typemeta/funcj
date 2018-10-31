package org.typemeta.funcj.codec.json;

import org.typemeta.funcj.codec.CodecConfigImpl;

/**
 * Base class for {@link JsonCodecConfig} implementations.
 */
public class JsonCodecConfigImpl extends CodecConfigImpl implements JsonCodecConfig {

    @Override
    public String typeFieldName() {
        return "@type";
    }

    @Override
    public String keyFieldName() {
        return "@key";
    }

    @Override
    public String valueFieldName() {
        return "@value";
    }
}
