package org.typemeta.funcj.codec;

import java.util.Optional;

public class OptionalCodec<IN, OUT, CFG extends CodecConfig>
        implements Codec.FinalCodec<Optional<?>, IN, OUT, CFG> {
    private static class Optional2<T> {
        public final T value;

        public Optional2(Optional<T> opt) {
            this.value = opt.orElse(null);
        }

        private Optional2() {
            this.value = null;
        }

        public Optional<T> asOptional() {
            return Optional.ofNullable(value);
        }
    }

    @Override
    public Class<Optional<?>> type() {
        return (Class)Optional.class;
    }

    @Override
    public OUT encode(CodecCoreEx<IN, OUT, CFG> core, Optional<?> optVal, OUT out) {
        final Optional2<?> opt2 = new Optional2<>(optVal);
        return core.encodeImpl(Optional2.class, opt2, out);
    }

    @Override
    public Optional<?> decode(CodecCoreEx<IN, OUT, CFG> core, IN in) {
        final Optional2<?> opt2 = core.decodeImpl(Optional2.class, in);
        return opt2.asOptional();
    }
}
