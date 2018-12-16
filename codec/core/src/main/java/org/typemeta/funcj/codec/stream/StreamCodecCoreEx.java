package org.typemeta.funcj.codec.stream;

import org.typemeta.funcj.codec.CodecConfig;


/**
 * Extended internal interface for {@link StreamCodecCore} implementations.
 * @param <IN>      the encoded input type
 * @param <OUT>     the encoded output type
 * @param <CFG>     the config type
 */
public interface StreamCodecCoreEx<
        IN extends StreamCodecFormat.Input<IN>,
        OUT extends StreamCodecFormat.Output<OUT>,
        CFG extends CodecConfig
        > extends StreamCodecCore<IN, OUT, CFG> {

    StreamCodecFormat<IN, OUT, CFG> format();
}
