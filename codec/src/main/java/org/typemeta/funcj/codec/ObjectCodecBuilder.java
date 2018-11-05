package org.typemeta.funcj.codec;

import org.typemeta.funcj.functions.Functions;

import java.util.*;

/**
 * Provides a builder interface for building an object {@link Codec},
 * by specifying the means by which the fields that comprise the object
 * are extracted, and the mechanism used to construct an instance of the object.
 * @param <T>       raw type to be encoded/decoded
 * @param <IN>      the encoded input type
 * @param <OUT>     the encoded output type
 */
@SuppressWarnings("unchecked")
public class ObjectCodecBuilder<T, IN, OUT> {
    public static class FieldCodec<T, IN, OUT> {
        protected final Functions.F2<T, OUT, OUT> encoder;
        protected final Functions.F<IN, Object> decoder;

        public <FT> FieldCodec(Functions.F<T, FT> getter, Codec<FT, IN, OUT> codec) {
            encoder = (val, out) -> codec.encodeWithCheck(getter.apply(val), out);
            decoder = codec::decodeWithCheck;
        }

        public OUT encodeField(T val, OUT out) {
            return encoder.apply(val, out);
        }

        public Object decodeField(IN in)  {
            return decoder.apply(in);
        }
    }

    private final CodecCoreInternal<IN, OUT> core;
    private final Class<T> type;

    protected final Map<String, FieldCodec<T, IN, OUT>> fields = new LinkedHashMap<>();

    public ObjectCodecBuilder(CodecCoreInternal<IN, OUT> core, Class<T> type) {
        this.core = core;
        this.type = type;
    }

    protected Codec<T, IN, OUT> registration(Codec<T, IN, OUT> codec) {
        return codec;
    }

    private <X> Codec<X, IN, OUT> getCodec(Class<X> clazz) {
        return core.getCodec(clazz);
    }

    <A> _1<A> field(String name, Functions.F<T, A> getter, Codec<A, IN, OUT> codec) {
        fields.put(name, new FieldCodec<>(getter, codec));
        return new _1<A>();
    }

    <A> _1<A> field(String name, Functions.F<T, A> getter, Class<A> clazz) {
        return field(name, getter, getCodec(clazz));
    }

    class _1<A> {
        public Codec<T, IN, OUT> map(Functions.F<A, T> ctor) {
            return registration(
                    core.createObjectCodec(
                            type,
                            fields,
                            arr -> ctor.apply((A)arr[0])));
        }

        <B> _2<B> field(String name, Functions.F<T, B> getter, Codec<B, IN, OUT> codec) {
            fields.put(name, new FieldCodec<>(getter, codec));
            return new _2<B>();
        }

        <B> _2<B> field(String name, Functions.F<T, B> getter, Class<B> clazz) {
            return field(name, getter, getCodec(clazz));
        }

        class _2<B> {
            public Codec<T, IN, OUT> map(Functions.F2<A, B, T> ctor) {
                return registration(
                        core.createObjectCodec(
                                type,
                                fields,
                                arr -> ctor.apply((A)arr[0], (B)arr[1])));
            }

            <C> _3<C> field(String name, Functions.F<T, C> getter, Codec<C, IN, OUT> codec) {
                fields.put(name, new FieldCodec<>(getter, codec));
                return new _3<C>();
            }

            <C> _3<C> field(String name, Functions.F<T, C> getter, Class<C> clazz) {
                return field(name, getter, getCodec(clazz));
            }

            class _3<C> {
                public Codec<T, IN, OUT> map(Functions.F3<A, B, C, T> ctor) {
                    return registration(
                            core.createObjectCodec(
                                    type,
                                    fields,
                                    arr -> ctor.apply((A)arr[0], (B)arr[1], (C)arr[2])));
                }

                <D> _4<D> field(String name, Functions.F<T, D> getter, Codec<D, IN, OUT> codec) {
                    fields.put(name, new FieldCodec<>(getter, codec));
                    return new _4<D>();
                }

                <D> _4<D> field(String name, Functions.F<T, D> getter, Class<D> clazz) {
                    return field(name, getter, getCodec(clazz));
                }

                class _4<D> {
                    public Codec<T, IN, OUT> map(Functions.F4<A, B, C, D, T> ctor) {
                        return registration(
                                core.createObjectCodec(
                                        type,
                                        fields,
                                        arr -> ctor.apply((A)arr[0], (B)arr[1], (C)arr[2], (D)arr[3])));
                    }

                    <N> _N field(String name, Functions.F<T, N> getter, Codec<N, IN, OUT> codec) {
                        fields.put(name, new FieldCodec<>(getter, codec));
                        return new _N();
                    }

                    <N> _N field(String name, Functions.F<T, N> getter, Class<N> clazz) {
                        return field(name, getter, getCodec(clazz));
                    }

                    class _N {
                        public Codec<T, IN, OUT> map(ArgArrayTypeCtor<T> ctor) {
                            return registration(core.createObjectCodec(type, fields, ctor));
                        }

                        <N> _N field(String name, Functions.F<T, N> getter, Codec<N, IN, OUT> codec) {
                            fields.put(name, new FieldCodec<>(getter, codec));
                            return new _N();
                        }

                        <N> _N field(String name, Functions.F<T, N> getter, Class<N> clazz) {
                            return field(name, getter, getCodec(clazz));
                        }
                    }
                }
            }
        }
    }
}
