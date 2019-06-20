package org.typemeta.funcj.codec.mpack;

import org.typemeta.funcj.codec.impl.CodecConfigImpl;
import org.typemeta.funcj.codec.utils.NotSupportedException;

/**
 * Base class for {@link MpackTypes.Config} implementations.
 */
public class MpackConfigImpl extends CodecConfigImpl implements MpackTypes.Config {

    public static class BuilderImpl extends AbstractBuilder<MpackTypes.Config> {

        @Override
        public MpackTypes.Config build() {
            return new MpackConfigImpl(this);
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

    public MpackConfigImpl() {
    }

    public MpackConfigImpl(BuilderImpl builder) {
        super(builder);
    }
}
