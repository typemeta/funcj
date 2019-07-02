package org.typemeta.funcj.codec.bytes;

import org.typemeta.funcj.codec.impl.CodecConfigImpl;
import org.typemeta.funcj.codec.utils.NotSupportedException;

/**
 * Base class for {@link ByteTypes.Config} implementations.
 */
public class ByteConfigImpl extends CodecConfigImpl implements ByteTypes.Config {

    public static class BuilderImpl extends AbstractBuilder<BuilderImpl, ByteTypes.Config> {

        @Override
        public ByteTypes.Config build() {
            return new ByteConfigImpl(this);
        }

        @Override
        public BuilderImpl dynamicTypeTags(boolean enable) {
            throw new NotSupportedException();
        }

        @Override
        public BuilderImpl failOnNoTypeConstructor(boolean enable) {
            throw new NotSupportedException();
        }

        @Override
        public BuilderImpl failOnUnrecognisedFields(boolean enable) {
            throw new NotSupportedException();
        }
    }

    public ByteConfigImpl() {
    }

    public ByteConfigImpl(BuilderImpl builder) {
        super(builder);
    }
}
