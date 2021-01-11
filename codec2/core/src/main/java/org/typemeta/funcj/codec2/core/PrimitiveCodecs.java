package org.typemeta.funcj.codec2.core;

public abstract class PrimitiveCodecs {
    private PrimitiveCodecs() {}

    public interface BooleanCodec<IN, OUT> extends FinalCodec<Boolean, IN, OUT> {
        @Override
        default Class<Boolean> type() {
            return Boolean.class;
        }

        @Override
        default OUT encodeImpl(EncoderCore<OUT> core, Context ctx, Boolean value, OUT out) {
            return encodeBool(core, ctx, value, out);
        }

        @Override
        default Boolean decodeImpl(DecoderCore<IN> core, Context ctx, IN in) {
            return decodeBool(core, ctx, in);
        }

        OUT encodeBool(EncoderCore<OUT> core, Context ctx, boolean value, OUT out);

        boolean decodeBool(DecoderCore<IN> core, Context ctx, IN in);
    }

    public interface IntegerCodec<IN, OUT> extends FinalCodec<Integer, IN, OUT> {
        @Override
        default Class<Integer> type() {
            return Integer.class;
        }

        @Override
        default OUT encodeImpl(EncoderCore<OUT> core, Context ctx, Integer value, OUT out) {
            return encodeInt(core, ctx, value, out);
        }

        @Override
        default Integer decodeImpl(DecoderCore<IN> core, Context ctx, IN in) {
            return decodeInt(core, ctx, in);
        }

        OUT encodeInt(EncoderCore<OUT> core, Context ctx, int value, OUT out);

        int decodeInt(DecoderCore<IN> core, Context ctx, IN in);
    }

    public interface StringCodec<IN, OUT> extends FinalCodec<String, IN, OUT> {
        @Override
        default Class<String> type() {
            return String.class;
        }
    }
}
