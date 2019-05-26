package org.typemeta.funcj.codec.json;

import org.typemeta.funcj.codec.*;
import org.typemeta.funcj.codec.bytes.ByteCodecCore;
import org.typemeta.funcj.codec.utils.*;
import org.typemeta.funcj.codec.xml.XmlCodecCore;
import org.typemeta.funcj.functions.Functions.F;

import java.math.*;
import java.time.*;
import java.util.*;

/**
 * Factory methods for creating CodecCore instances.
 */
public abstract class Codecs extends org.typemeta.funcj.codec.Codecs {

    /**
     * Construct and return a new instance of a {@link JsonCodecCore}.
     * @return the new {@code JsonCodecCore}
     */
    public static JsonCodecCore jsonCodec() {
        final JsonCodecCore core = registerAll(new JsonCodecCore());
        return JsValueCodec.registerAll(core);
    }
}
