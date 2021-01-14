package org.typemeta.funcj.codec.stream;

import org.typemeta.funcj.codec.*;

/**
 * Interface for classes that encapsulates the logic for encoding a value of type {@code T}
 * into an encoded value and vice versa.
 * @param <T>       the raw type to be encoded/decoded
 * @param <IN>      the encoded input type
 * @param <OUT>     the encoded output type
 */
public interface StreamCodec<
        T,
        IN extends StreamCodecFormat.Input<IN>,
        OUT extends StreamCodecFormat.Output<OUT>,
        CFG extends CodecConfig
        > extends Codec<T, IN, OUT, CFG> {
}
