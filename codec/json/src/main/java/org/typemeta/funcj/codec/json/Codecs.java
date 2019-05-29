package org.typemeta.funcj.codec.json;

import org.typemeta.funcj.codec.jsonnode.JsonNCodecCore;

/**
 * Factory methods for creating CodecCore instances.
 */
public abstract class Codecs extends org.typemeta.funcj.codec.Codecs {

    /**
     * Construct and return a new instance of a {@link JsonCodecCore}.
     * @return      the JSON codec
     */
    public static JsonCodecCore jsonCodec() {
        final JsonCodecCore core = registerAll(new JsonCodecCore());
        return JsValueCodec.registerAll(core);
    }
    /**
     * Construct and return a new instance of a {@link JsonNCodecCore}.
     * @return      the JSON codec
     */
    public static JsonNCodecCore jsonNCodec() {
        return registerAll(new JsonNCodecCore());
    }
}
