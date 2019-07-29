package org.typemeta.funcj.codec.avro;

import org.typemeta.funcj.codec.impl.CodecConfigImpl;
import org.typemeta.funcj.codec.utils.NotSupportedException;

/**
 * Base class for {@link AvroTypes.Config} implementations.
 */
public class AvroConfig extends CodecConfigImpl implements AvroTypes.Config {

    public static class Builder extends AbstractBuilder<Builder, AvroTypes.Config> {

        @Override
        public AvroTypes.Config build() {
            return new AvroConfig(this);
        }

        @Override
        public Builder dynamicTypeTags(boolean enable) {
            throw new NotSupportedException();
        }

        @Override
        public Builder failOnNoTypeConstructor(boolean enable) {
            throw new NotSupportedException();
        }

        @Override
        public Builder failOnUnrecognisedFields(boolean enable) {
            throw new NotSupportedException();
        }
    }

    public static AvroConfig.Builder builder() {
        return new AvroConfig.Builder();
    }

    public AvroConfig() {
    }

    public AvroConfig(Builder builder) {
        super(builder);
    }
}
