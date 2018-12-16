package org.typemeta.funcj.codec;

import org.typemeta.funcj.functions.Functions;

public class ObjectCodecBuilder {
    public static class FieldCodec<
            T,
            IN,
            OUT,
            CFG extends CodecConfig
            > {
        protected final Functions.F3<CodecCoreEx<IN, OUT, CFG>, T, OUT, OUT> encoder;
        protected final Functions.F2<CodecCoreEx<IN, OUT, CFG>, IN, Object> decoder;

        public <FT> FieldCodec(
                Functions.F<T, FT> getter,
                Codec<FT, IN, OUT, CFG> codec) {
            encoder = (core, val, out) -> codec.encodeWithCheck(core, getter.apply(val), out);
            decoder = codec::decodeWithCheck;
        }

        public OUT encodeField(CodecCoreEx<IN, OUT, CFG> core, T val, OUT out) {
            return encoder.apply(core, val, out);
        }

        public Object decodeField(CodecCoreEx<IN, OUT, CFG> core, IN in)  {
            return decoder.apply(core, in);
        }
    }
}
