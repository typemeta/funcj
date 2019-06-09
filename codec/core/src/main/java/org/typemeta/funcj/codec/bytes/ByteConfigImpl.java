package org.typemeta.funcj.codec.bytes;

import org.typemeta.funcj.codec.impl.CodecConfigImpl;
import org.typemeta.funcj.codec.utils.NotSupportedException;

/**
 * Base class for {@link ByteTypes.Config} implementations.
 */
public class ByteConfigImpl extends CodecConfigImpl implements ByteTypes.Config {
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
