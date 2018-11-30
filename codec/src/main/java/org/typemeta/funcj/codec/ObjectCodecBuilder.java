package org.typemeta.funcj.codec;

import org.typemeta.funcj.codec.CodecFormat.Input;
import org.typemeta.funcj.codec.CodecFormat.Output;
import org.typemeta.funcj.functions.Functions;
import org.typemeta.funcj.functions.Functions.*;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Provides a builder interface for building an object {@link Codec},
 * by specifying the means by which the fields that comprise the object
 * are extracted, and the mechanism used to construct an instance of the object.
 * @param <T>       raw type to be encoded/decoded
 * @param <IN>      the encoded input type
 * @param <OUT>     the encoded output type
 * @param <CFG>     the config type
 */
@SuppressWarnings("unchecked")
public class ObjectCodecBuilder<
        T,
        IN extends Input<IN>,
        OUT extends Output<OUT>,
        CFG extends CodecConfig
        > {
    public static class FieldCodec<
            T,
            IN extends Input<IN>,
            OUT extends Output<OUT>,
            CFG extends CodecConfig
            > {
        protected final F3<CodecCoreEx<IN, OUT, CFG>, T, OUT, OUT> encoder;
        protected final F2<CodecCoreEx<IN, OUT, CFG>, IN, Object> decoder;

        public <FT> FieldCodec(
                F<T, FT> getter,
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

    private final CodecCoreEx<IN, OUT, CFG> core;
    private final Class<T> clazz;

    protected final Map<String, FieldCodec<T, IN, OUT, CFG>> fields = new LinkedHashMap<>();

    public ObjectCodecBuilder(CodecCoreEx<IN, OUT, CFG> core, Class<T> clazz) {
        this.core = core;
        this.clazz = clazz;
    }

    protected Codec<T, IN, OUT, CFG> registration(Codec<T, IN, OUT, CFG> codec) {
        return codec;
    }

    private <X> Codec<X, IN, OUT, CFG> getCodec(Class<X> clazz) {
        return core.getCodec(clazz);
    }

    <A> _1<A> field(String name, F<T, A> getter, Codec<A, IN, OUT, CFG> codec) {
        fields.put(name, new FieldCodec<>(getter, codec));
        return new _1<A>();
    }

    <A> _1<A> field(String name, F<T, A> getter, Class<A> clazz) {
        return field(name, getter, getCodec(clazz));
    }

    class _1<A> {
        public Codec<T, IN, OUT, CFG> map(F<A, T> ctor) {
            return registration(
                    core.createObjectCodec(
                            clazz,
                            fields,
                            arr -> ctor.apply((A)arr[0])));
        }

        <B> _2<B> field(String name, F<T, B> getter, Codec<B, IN, OUT, CFG> codec) {
            fields.put(name, new FieldCodec<>(getter, codec));
            return new _2<B>();
        }

        <B> _2<B> field(String name, F<T, B> getter, Class<B> clazz) {
            return field(name, getter, getCodec(clazz));
        }

        class _2<B> {
            public Codec<T, IN, OUT, CFG> map(F2<A, B, T> ctor) {
                return registration(
                        core.createObjectCodec(
                                clazz,
                                fields,
                                arr -> ctor.apply((A)arr[0], (B)arr[1])));
            }

            <C> _3<C> field(String name, F<T, C> getter, Codec<C, IN, OUT, CFG> codec) {
                fields.put(name, new FieldCodec<>(getter, codec));
                return new _3<C>();
            }

            <C> _3<C> field(String name, F<T, C> getter, Class<C> clazz) {
                return field(name, getter, getCodec(clazz));
            }

            class _3<C> {
                public Codec<T, IN, OUT, CFG> map(F3<A, B, C, T> ctor) {
                    return registration(
                            core.createObjectCodec(
                                    clazz,
                                    fields,
                                    arr -> ctor.apply((A)arr[0], (B)arr[1], (C)arr[2])));
                }

                <D> _4<D> field(String name, F<T, D> getter, Codec<D, IN, OUT, CFG> codec) {
                    fields.put(name, new FieldCodec<>(getter, codec));
                    return new _4<D>();
                }

                <D> _4<D> field(String name, F<T, D> getter, Class<D> clazz) {
                    return field(name, getter, getCodec(clazz));
                }

                class _4<D> {
                    public Codec<T, IN, OUT, CFG> map(Functions.F4<A, B, C, D, T> ctor) {
                        return registration(
                                core.createObjectCodec(
                                        clazz,
                                        fields,
                                        arr -> ctor.apply((A)arr[0], (B)arr[1], (C)arr[2], (D)arr[3])));
                    }

                    <N> _N field(String name, F<T, N> getter, Codec<N, IN, OUT, CFG> codec) {
                        fields.put(name, new FieldCodec<>(getter, codec));
                        return new _N();
                    }

                    <N> _N field(String name, F<T, N> getter, Class<N> clazz) {
                        return field(name, getter, getCodec(clazz));
                    }

                    class _N {
                        public Codec<T, IN, OUT, CFG> map(ArgArrayTypeCtor<T> ctor) {
                            return registration(core.createObjectCodec(clazz, fields, ctor));
                        }

                        <N> _N field(String name, F<T, N> getter, Codec<N, IN, OUT, CFG> codec) {
                            fields.put(name, new FieldCodec<>(getter, codec));
                            return new _N();
                        }

                        <N> _N field(String name, F<T, N> getter, Class<N> clazz) {
                            return field(name, getter, getCodec(clazz));
                        }
                    }
                }
            }
        }
    }
}
