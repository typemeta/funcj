package org.typemeta.funcj.codec.jsonnode;

import org.typemeta.funcj.codec.CodecConfigImpl;

/**
 * Base class for {@link JsonNTypes.Config} implementations.
 */
public class JsonNConfigImpl extends CodecConfigImpl implements JsonNTypes.Config {

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
