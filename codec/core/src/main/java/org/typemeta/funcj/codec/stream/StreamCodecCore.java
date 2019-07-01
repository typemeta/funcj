package org.typemeta.funcj.codec.stream;

import org.typemeta.funcj.codec.CodecConfig;
import org.typemeta.funcj.codec.CodecCore;

/**
 * Interface for classes which provide an encoding of values of any type,
 * into a specific target type.
 * @param <IN>      the encoded input type
 * @param <OUT>     the encoded output type
 * @param <CFG>     the config type
 */
public interface StreamCodecCore<
        IN extends StreamCodecFormat.Input<IN>,
        OUT extends StreamCodecFormat.Output<OUT>,
        CFG extends CodecConfig
        > extends CodecCore<IN, OUT, CFG> {
}
