package org.typemeta.funcj.codec.jsonnode;

import org.typemeta.funcj.codec.impl.CodecConfigImpl;

/**
 * Base class for {@link JsonNodeConfig} implementations.
 */
public class JsonNodeConfigImpl extends CodecConfigImpl implements JsonNodeConfig {

    public static class BuilderImpl extends CodecConfigImpl.BuilderImpl<JsonNodeConfig> {

        @Override
        public JsonNodeConfig build() {
            return new JsonNodeConfigImpl(this);
        }
    }

    public JsonNodeConfigImpl() {
    }

    public JsonNodeConfigImpl(BuilderImpl builder) {
        super(builder);
    }

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
