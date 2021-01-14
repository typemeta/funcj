package org.typemeta.funcj.codec2.core.fields;

import org.typemeta.funcj.codec2.core.Context;
import org.typemeta.funcj.codec2.core.DecoderCore;
import org.typemeta.funcj.codec2.core.EncoderCore;

public interface FieldCodec<T, IN, OUT> {
    OUT encodeField(EncoderCore<OUT> core, Context ctx, T source, OUT out);

    void decodeField(DecoderCore<IN> core, Context ctx, T target, IN in);
}
