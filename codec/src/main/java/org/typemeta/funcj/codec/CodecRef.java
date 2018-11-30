package org.typemeta.funcj.codec;

import org.typemeta.funcj.codec.CodecFormat.Input;
import org.typemeta.funcj.codec.CodecFormat.Output;
import org.typemeta.funcj.functions.Functions;

import java.util.Objects;

/**
 * A reference to a {@link Codec}.
 * {@code CodecRef} implements the {@link Codec} interface.
 * Used when looking up a codec for a recursive type.
 * @param <T>       the raw type to be encoded/decoded
 * @param <IN>      the encoded input type
 * @param <OUT>     the encoded output type
 */
public class CodecRef<
        T,
        IN extends Input<IN>,
        OUT extends Output<OUT>,
        CFG extends CodecConfig
        > implements Codec<T, IN, OUT, CFG> {

    private enum Uninitialised implements Codec {
        INSTANCE;

        @Override
        public Class<Object> type() {
            throw error();
        }

        @Override
        public Output encode(
                CodecCoreEx core,
                Object value,
                Output output) {
            throw error();
        }

        @Override
        public Object decode(
                CodecCoreEx core,
                Input input) {
            throw error();
        }

        private static RuntimeException error() {
            return new RuntimeException("Uninitialised lazy Codec reference");
        }

        static Codec of() {
            return INSTANCE;
        }
    }

    private Codec<T, IN, OUT, CFG> impl;

    @SuppressWarnings("unchecked")
    CodecRef() {
        this.impl = Uninitialised.of();
    }

    /**
     * Initialise this reference.
     * @param impl      the codec
     * @return          this codec
     */
    public Codec<T, IN, OUT, CFG> set(Codec<T, IN, OUT, CFG> impl) {
        if (this.impl != Uninitialised.INSTANCE) {
            throw new IllegalStateException("CodecRef is already initialised");
        } else {
            this.impl = Objects.requireNonNull(impl);
            return this;
        }
    }

    public synchronized Codec<T, IN, OUT, CFG> setIfUninitialised(Functions.F0<Codec<T, IN, OUT, CFG>> implSupp) {
        if (this.impl == Uninitialised.INSTANCE) {
            this.impl = Objects.requireNonNull(implSupp.apply());
        }
        return impl;
    }

    public Codec<T, IN, OUT, CFG> get() {
        return impl;
    }


    @Override
    public Class<T> type() {
        return impl.type();
    }

    @Override
    public OUT encode(CodecCoreEx<IN, OUT, CFG> core, T value, OUT out) {
        return impl.encode(core, value, out);
    }

    @Override
    public T decode(CodecCoreEx<IN, OUT, CFG> core, IN in) {
        return impl.decode(core, in);
    }
}
