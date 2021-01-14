package org.typemeta.funcj.codec2.core;

import org.typemeta.funcj.codec2.core.PrimitiveCodecs.*;
import org.typemeta.funcj.codec2.core.fields.FieldCodec;

import java.util.Map;

public interface CodecFormat<IN, OUT> {

    class EncodeResult<OUT> {
        private final boolean encoded;
        private final OUT out;

        public EncodeResult(boolean encoded, OUT out) {
            this.encoded = encoded;
            this.out = out;
        }

        public boolean encoded() {
            return encoded;
        }

        public OUT out() {
            return out;
        }
    }

    <T> CodecFormat.EncodeResult<OUT> encodeNull(EncoderCore<OUT> core, Context ctx, T val, OUT out);

    boolean decodeNull(DecoderCore<IN> core, Context ctx, IN in);

    BooleanCodec<IN, OUT> booleanCodec();

    Codec<boolean[], IN, OUT> booleanArrayCodec();

    IntegerCodec<IN, OUT> integerCodec();

    Codec<int[], IN, OUT> integerArrayCodec();

    Codec<String, IN, OUT> stringCodec();

    <T> Codec<T, IN, OUT> objectCodec(
            Class<T> type,
            Map<String, FieldCodec<?, IN, OUT>> fieldCodecs,
            ObjectCreator<T> ctor
    );

    <T> Codec<T[], IN, OUT> objectArrayCodec(
            Class<T[]> arrType,
            Class<T> elemType,
            Codec<T, IN, OUT> elemCodec
    );

    <T> CodecFormat.EncodeResult<OUT> encodeDynamic(EncoderCore<OUT> core, Context ctx, T value, OUT out, Encoder<T, OUT> encoder);

    <T> T decodeDynamic(DecoderCore<IN> core, Context ctx, IN in);
}
