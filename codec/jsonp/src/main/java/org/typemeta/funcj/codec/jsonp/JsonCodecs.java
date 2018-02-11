package org.typemeta.funcj.codec.json;

import org.typemeta.funcj.codec.*;

public class JsonCodecs {
    /**
     * Construct and return a new instance of a {@link JsonCodecCore}.
     * @return the new {@code JsonCodecCore}
     */
    public static JsonCodecCore jsonCodec() {
        final JsonCodecCoreImpl codec = new JsonCodecCoreImpl();
        return Codecs.registerAll(codec);
    }
}
