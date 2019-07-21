package org.typemeta.funcj.codec.jsonnode;

import org.typemeta.funcj.codec.impl.CodecConfigImpl;

/**
 * Base class for {@link JsonNodeTypes.Config} implementations.
 */
public class JsonNodeConfig extends CodecConfigImpl implements JsonNodeTypes.Config {

    public static class Builder extends AbstractBuilder<Builder, JsonNodeTypes.Config> {
        @Override
        public JsonNodeTypes.Config build() {
            return new JsonNodeConfig(this);
        }
    }

    public static JsonNodeConfig.Builder builder() {
        return new JsonNodeConfig.Builder();
    }

    public JsonNodeConfig() {
    }

    public JsonNodeConfig(Builder builder) {
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
