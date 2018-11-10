package org.typemeta.funcj.codec.bytes;

import org.typemeta.funcj.codec.Codecs;

public class ByteCodecs {
    public static ByteCodecCore registerAll(ByteCodecCore core) {
        return Codecs.registerAll(core);
    }
}
