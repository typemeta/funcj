package org.typemeta.funcj.codec.json;

import org.typemeta.funcj.codec.impl.CodecConfigImpl;

/**
 * Base class for {@link JsonTypes.Config} implementations.
 */
public class JsonConfigImpl extends CodecConfigImpl implements JsonTypes.Config {

    public static class BuilderImpl extends CodecConfigImpl.BuilderImpl<JsonTypes.Config> {

        @Override
        public JsonTypes.Config build() {
            return new JsonConfigImpl(this);
        }
    }

    public JsonConfigImpl() {
    }

    public JsonConfigImpl(BuilderImpl builder) {
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
