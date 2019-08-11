package org.typemeta.funcj.codec.avro;

import org.typemeta.funcj.codec.CodecConfig;
import org.typemeta.funcj.codec.avro.schema.*;

/**
 * Factory methods for creating CodecCore instances.
 */
public abstract class Codecs extends org.typemeta.funcj.codec.Codecs {

    public static AvroSchemaCodecCore avroSchemaCodec() {
        return avroSchemaCodec(new AvroSchemaConfig.Builder());
    }

    public static AvroSchemaCodecCore avroSchemaCodec(CodecConfig.Builder<?, AvroSchemaTypes.Config> cfgBldr) {
        return registerAll(cfgBldr, AvroSchemaCodecCore::new);
    }

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
