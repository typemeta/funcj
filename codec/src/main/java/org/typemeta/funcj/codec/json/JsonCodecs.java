package org.typemeta.funcj.codec.json;

import org.typemeta.funcj.codec.*;

public class JsonCodecs {
    public static JsonCodecCore registerAll(JsonCodecCore core) {
        return Codecs.registerAll(core);
    }
}
