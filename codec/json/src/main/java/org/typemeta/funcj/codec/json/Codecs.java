package org.typemeta.funcj.codec.json;

import org.typemeta.funcj.codec.jsonnode.JsonNodeCodecCore;

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
     * Construct and return a new instance of a {@link JsonNodeCodecCore}.
     * @return      the JSON codec
     */
    public static JsonNodeCodecCore jsonNCodec() {
        return registerAll(new JsonNodeCodecCore());
    }
}
