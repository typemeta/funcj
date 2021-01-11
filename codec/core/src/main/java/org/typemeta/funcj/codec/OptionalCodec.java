package org.typemeta.funcj.codec;

import java.util.Optional;

public class OptionalCodec<IN, OUT, CFG extends CodecConfig>
        implements Codec.FinalCodec<Optional, IN, OUT, CFG> {
    @Override
    public Class<Optional> type() {
        return Optional.class;
    }

    @Override
    public OUT encode(CodecCoreEx<IN, OUT, CFG> core, Optional optVal, OUT out) {
        final OUT resOut = core.format().booleanCodec().encode(core, optVal.isPresent(), out);
        if (optVal.isPresent()) {
            final Object value = optVal.get();
            return core.encodeDynamicType()
            return core.getCodec((Class<T>)value.getClass()).encode(core, value, resOut);
        } else {
            return resOut;
        }
    }

    @Override
    public Optional decode(CodecCoreEx<IN, OUT, CFG> core, IN in) {
        if (core.format().booleanCodec().decode(core, in)) {
            return
        }
    }
}
