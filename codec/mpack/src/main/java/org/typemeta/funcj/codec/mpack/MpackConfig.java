package org.typemeta.funcj.codec.mpack;

import org.typemeta.funcj.codec.impl.CodecConfigImpl;
import org.typemeta.funcj.codec.utils.NotSupportedException;

/**
 * Base class for {@link MpackTypes.Config} implementations.
 */
public class MpackConfig extends CodecConfigImpl implements MpackTypes.Config {

    public static class Builder extends AbstractBuilder<Builder, MpackTypes.Config> {

        @Override
        public MpackTypes.Config build() {
            return new MpackConfig(this);
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

    public static MpackConfig.Builder builder() {
        return new MpackConfig.Builder();
    }

    public MpackConfig() {
    }

    public MpackConfig(Builder builder) {
        super(builder);
    }
}
