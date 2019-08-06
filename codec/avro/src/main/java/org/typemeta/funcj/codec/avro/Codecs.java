package org.typemeta.funcj.codec.avro;

import org.typemeta.funcj.codec.CodecConfig;

/**
 * Factory methods for creating CodecCore instances.
 */
public abstract class Codecs extends org.typemeta.funcj.codec.Codecs {

    /**
     * Construct and return a new instance of a {@link AvroCodecCore}.
     * @return      the Avro codec
     */
    public static AvroCodecCore avroCodec() {
        return avroCodec(new AvroConfig.Builder());
    }

    public static AvroCodecCore avroCodec(CodecConfig.Builder<?, AvroTypes.Config> cfgBldr) {
        return registerAll(cfgBldr, AvroCodecCore::new);
    }
}
