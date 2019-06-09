package org.typemeta.funcj.codec.jsonnode;

import org.typemeta.funcj.codec.impl.CodecConfigImpl;

/**
 * Base class for {@link JsonNodeConfig} implementations.
 */
public class JsonNodeConfigImpl extends CodecConfigImpl implements JsonNodeConfig {

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
