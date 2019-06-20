package org.typemeta.funcj.codec.bytes;

import org.typemeta.funcj.codec.impl.CodecConfigImpl;
import org.typemeta.funcj.codec.utils.NotSupportedException;

/**
 * Base class for {@link ByteTypes.Config} implementations.
 */
public class ByteConfigImpl extends CodecConfigImpl implements ByteTypes.Config {

    public static class BuilderImpl extends AbstractBuilder<ByteTypes.Config> {

        @Override
        public ByteTypes.Config build() {
            return new ByteConfigImpl(this);
        }

        @Override
        public void dynamicTypeTags(boolean enable) {
            throw new NotSupportedException();
        }

        @Override
        public void failOnNoTypeConstructor(boolean enable) {
            throw new NotSupportedException();
        }

        @Override
        public void failOnUnrecognisedFields(boolean enable) {
            throw new NotSupportedException();
        }
    }

    public ByteConfigImpl() {
    }

    public ByteConfigImpl(BuilderImpl builder) {
        super(builder);
    }
}
