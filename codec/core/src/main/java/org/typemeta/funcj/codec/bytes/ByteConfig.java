package org.typemeta.funcj.codec.bytes;

import org.typemeta.funcj.codec.impl.CodecConfigImpl;
import org.typemeta.funcj.codec.utils.NotSupportedException;

/**
 * Base class for {@link ByteTypes.Config} implementations.
 */
public class ByteConfig extends CodecConfigImpl implements ByteTypes.Config {

    public static class Builder extends AbstractBuilder<Builder, ByteTypes.Config> {

        @Override
        public ByteTypes.Config build() {
            return new ByteConfig(this);
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

    public static ByteConfig.Builder builder() {
        return new ByteConfig.Builder();
    }

    public ByteConfig() {
    }

    public ByteConfig(Builder builder) {
        super(builder);
    }
}
