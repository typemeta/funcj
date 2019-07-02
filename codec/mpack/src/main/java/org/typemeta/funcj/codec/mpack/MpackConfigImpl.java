package org.typemeta.funcj.codec.mpack;

import org.typemeta.funcj.codec.impl.CodecConfigImpl;
import org.typemeta.funcj.codec.utils.NotSupportedException;

/**
 * Base class for {@link MpackTypes.Config} implementations.
 */
public class MpackConfigImpl extends CodecConfigImpl implements MpackTypes.Config {

    public static class BuilderImpl extends AbstractBuilder<BuilderImpl, MpackTypes.Config> {

        @Override
        public MpackTypes.Config build() {
            return new MpackConfigImpl(this);
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

    public MpackConfigImpl() {
    }

    public MpackConfigImpl(BuilderImpl builder) {
        super(builder);
    }
}
