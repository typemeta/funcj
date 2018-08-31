package org.typemeta.funcj.codec;

import org.typemeta.funcj.functions.*;

import java.lang.reflect.Modifier;
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

        <FT> FieldCodec(Functions.F<T, FT> getter, Codec<FT, IN, OUT> codec) {
            encoder = (val, out) -> codec.encode(getter.apply(val), out);
            decoder = codec::decode;
        }

        public OUT encodeField(T val, OUT out) {
            return encoder.apply(val, out);
        }

        public Object decodeField(IN in)  {
            return decoder.apply(in);
        }
    }

    private final CodecCoreIntl<IN, OUT> core;

    protected final Map<String, FieldCodec<T, IN, OUT>> fields = new LinkedHashMap<>();

    public ObjectCodecBuilder(CodecCoreIntl<IN, OUT> core) {
        this.core = core;
    }

    protected Codec<T, IN, OUT> registration(Codec<T, IN, OUT> codec) {
        return codec;
    }

    private <X> Codec<X, IN, OUT> makeDynSafe(Codec<X, IN, OUT> codec, Class<X> stcType) {
        if (Modifier.isFinal(stcType.getModifiers())) {
            return codec;
        } else {
            return core.dynamicCodec(codec, stcType);
        }
    }

    private <X> Codec<X, IN, OUT> getNullSafeCodec(Class<X> stcType) {
        return core.makeNullSafeCodec(core.dynamicCodec(core.getNullUnsafeCodec(stcType), stcType));
    }

    private <X> Codec<X, IN, OUT> getNullUnsafeCodec(Class<X> stcType) {
        return core.dynamicCodec(core.getNullUnsafeCodec(stcType), stcType);
    }

    <A> _1<A> field(String name, Functions.F<T, A> getter, Codec<A, IN, OUT> codec) {
        fields.put(name, new FieldCodec<T, IN, OUT>(getter, codec));
        return new _1<A>();
    }

    <A> _1<A> nullField(String name, Functions.F<T, A> getter, Class<A> clazz) {
        return field(name, getter, getNullSafeCodec(clazz));
    }

    <A> _1<A> field(String name, Functions.F<T, A> getter, Class<A> clazz) {
        return field(name, getter, getNullUnsafeCodec(clazz));
    }

    class _1<A> {
        public Codec<T, IN, OUT> map(Functions.F<A, T> ctor) {
            return registration(
                    core.createObjectCodec(
                            fields,
                            arr -> ctor.apply((A)arr[0])));
        }

        <B> _2<B> field(String name, Functions.F<T, B> getter, Codec<B, IN, OUT> codec) {
            fields.put(name, new FieldCodec<T, IN, OUT>(getter, codec));
            return new _2<B>();
        }

        <B> _2<B> nullField(String name, Functions.F<T, B> getter, Class<B> clazz) {
            return field(name, getter, getNullSafeCodec(clazz));

        }

        <B> _2<B> field(String name, Functions.F<T, B> getter, Class<B> clazz) {
            return field(name, getter, getNullUnsafeCodec(clazz));
        }

        class _2<B> {
            public Codec<T, IN, OUT> map(Functions.F2<A, B, T> ctor) {
                return registration(
                        core.createObjectCodec(
                                fields,
                                arr -> ctor.apply((A)arr[0], (B)arr[1])));
            }

            <C> _3<C> field(String name, Functions.F<T, C> getter, Codec<C, IN, OUT> codec) {
                fields.put(name, new FieldCodec<T, IN, OUT>(getter, codec));
                return new _3<C>();
            }

            <C> _3<C> nullField(String name, Functions.F<T, C> getter, Class<C> clazz) {
                return field(name, getter, getNullSafeCodec(clazz));
            }

            <C> _3<C> field(String name, Functions.F<T, C> getter, Class<C> clazz) {
                return field(name, getter, getNullUnsafeCodec(clazz));
            }

            class _3<C> {
                public Codec<T, IN, OUT> map(Functions.F3<A, B, C, T> ctor) {
                    return registration(
                            core.createObjectCodec(
                                    fields,
                                    arr -> ctor.apply((A)arr[0], (B)arr[1], (C)arr[2])));
                }

                <D> _4<D> field(String name, Functions.F<T, D> getter, Codec<D, IN, OUT> codec) {
                    fields.put(name, new FieldCodec<T, IN, OUT>(getter, codec));
                    return new _4<D>();
                }

                <D> _4<D> nullField(String name, Functions.F<T, D> getter, Class<D> clazz) {
                    return field(name, getter, getNullSafeCodec(clazz));
                }

                <D> _4<D> field(String name, Functions.F<T, D> getter, Class<D> clazz) {
                    return field(name, getter, getNullUnsafeCodec(clazz));
                }

                class _4<D> {
                    public Codec<T, IN, OUT> map(Functions.F4<A, B, C, D, T> ctor) {
                        return registration(
                                core.createObjectCodec(
                                        fields,
                                        arr -> ctor.apply((A)arr[0], (B)arr[1], (C)arr[2], (D)arr[3])));
                    }

                    <N> _N field(String name, Functions.F<T, N> getter, Codec<N, IN, OUT> codec) {
                        fields.put(name, new FieldCodec<T, IN, OUT>(getter, codec));
                        return new _N();
                    }

                    <N> _N nullField(String name, Functions.F<T, N> getter, Class<N> clazz) {
                        return field(name, getter, getNullSafeCodec(clazz));
                    }

                    <N> _N field(String name, Functions.F<T, N> getter, Class<N> clazz) {
                        return field(name, getter, getNullUnsafeCodec(clazz));
                    }

                    class _N {
                        public Codec<T, IN, OUT> map(Functions.F<Object[], T> ctor) {
                            return registration(core.createObjectCodec(fields, ctor));
                        }

                        <N> _N field(String name, Functions.F<T, N> getter, Codec<N, IN, OUT> codec) {
                            fields.put(name, new FieldCodec<T, IN, OUT>(getter, codec));
                            return new _N();
                        }

                        <N> _N nullField(String name, Functions.F<T, N> getter, Class<N> clazz) {
                            return field(name, getter, getNullSafeCodec(clazz));
                        }

                        <N> _N field(String name, Functions.F<T, N> getter, Class<N> clazz) {
                            return field(name, getter, getNullUnsafeCodec(clazz));
                        }
                    }
                }
            }
        }
    }
}
