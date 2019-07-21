package org.typemeta.funcj.codec.json;

import org.typemeta.funcj.codec.impl.CodecConfigImpl;

/**
 * Base class for {@link JsonTypes.Config} implementations.
 */
public class JsonConfig extends CodecConfigImpl implements JsonTypes.Config {

    public static class Builder extends AbstractBuilder<Builder, JsonTypes.Config> {
        @Override
        public JsonTypes.Config build() {
            return new JsonConfig(this);
        }
    }

    public static JsonConfig.Builder builder() {
        return new JsonConfig.Builder();
    }

    public JsonConfig() {
    }

    public JsonConfig(Builder builder) {
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
