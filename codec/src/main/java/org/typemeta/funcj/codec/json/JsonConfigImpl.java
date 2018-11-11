package org.typemeta.funcj.codec.json;

import org.typemeta.funcj.codec.CodecConfigImpl;

/**
 * Base class for {@link JsonCodec.Config} implementations.
 */
public class JsonConfigImpl extends CodecConfigImpl implements JsonCodec.Config {

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
