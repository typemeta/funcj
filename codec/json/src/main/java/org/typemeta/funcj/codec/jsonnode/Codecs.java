package org.typemeta.funcj.codec.jsonnode;

/**
 * Factory methods for creating CodecCore instances.
 */
public abstract class Codecs extends org.typemeta.funcj.codec.Codecs {

    /**
     * Construct and return a new instance of a {@link JsonCodecCore}.
     * @return the new {@code JsonCodecCore}
     */
    public static JsonCodecCore jsonCodec() {
        return registerAll(new JsonCodecCore());
    }
}
