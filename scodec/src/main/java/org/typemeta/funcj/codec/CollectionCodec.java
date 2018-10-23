package org.typemeta.funcj.codec;

import java.util.*;

public abstract class CollectionCodec<T, IN, OUT> extends Codec.Base<Collection<T>, IN, OUT> {

    protected final Codec<T, IN, OUT> elemCodec;

    protected CollectionCodec(CodecCoreIntl<IN, OUT> core, Codec<T, IN, OUT> elemCodec) {
        super(core);
        this.elemCodec = elemCodec;
    }

    private Codec<Collection<T>, IN, OUT> getCodec(Class<Collection<T>> type) {
        return  core().collCodec(type, elemCodec);
    }

    @Override
    public OUT encodeWithCheck(Collection<T> val, OUT out) {
        if (core().encodeNull(val, out)) {
            return out;
        } else {
            if (!core().encodeDynamicType(this, val, out, this::getCodec)) {
                return encode(val, out);
            } else {
                return out;
            }
        }
    }

    @Override
    public Collection<T> decodeWithCheck(IN in) {
        if (core().decodeNull(in)) {
            return null;
        } else {
            final Collection<T> val = core().decodeDynamicType(
                    in,
                    type -> core().collCodec(
                            core().nameToClass(type),
                            elemCodec
                    ).decode(in)
            );
            if (val != null) {
                return val;
            } else {
                return decode(in);
            }
        }
    }
}
