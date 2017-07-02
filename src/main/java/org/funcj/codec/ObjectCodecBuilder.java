package org.funcj.codec;

import org.funcj.util.Functions.*;

import java.lang.reflect.Modifier;
import java.util.*;

public class ObjectCodecBuilder<T, E> {
    public static class FieldCodec<T, E> {
        protected final F2<T, E, E> encoder;
        protected final F<E, Object> decoder;

        <FT> FieldCodec(F<T, FT> getter, Codec<FT, E> codec) {
            encoder = (val, enc) -> codec.encode(getter.apply(val), enc);
            decoder = codec::decode;
        }

        public E encodeField(T val, E enc) {
            return encoder.apply(val, enc);
        }

        public Object decodeField(E enc) {
            return decoder.apply(enc);
        }
    }

    public static <T, E> ObjectCodecBuilder<T, E> of(Class<T> clazz, CodecCore<E> core) {
        return new ObjectCodecBuilder<T, E>(core);
    }

    private <X> Codec<X, E> makeDynSafe(Codec<X, E> codec, Class<X> stcType) {
        if (Modifier.isFinal(stcType.getModifiers())) {
            return codec;
        } else {
            return core.dynamicCodec(codec, stcType);
        }
    }

    private <X> Codec<X, E> getNullSafeCodec(Class<X> stcType) {
        return core.makeNullSafeCodec(core.dynamicCodec(core.getNullUnsafeCodec(stcType), stcType));
    }

    private <X> Codec<X, E> getNullUnsafeCodec(Class<X> stcType) {
        return core.dynamicCodec(core.getNullUnsafeCodec(stcType), stcType);
    }

    private final CodecCore<E> core;

    public final Map<String, FieldCodec<T, E>> fields = new LinkedHashMap<>();

    public ObjectCodecBuilder(CodecCore<E> core) {
        this.core = core;
    }

    <A> _1<A> field(String name, F<T, A> getter, Codec<A, E> codec) {
        fields.put(name, new FieldCodec<T, E>(getter, codec));
        return new _1<A>();
    }

    <A> _1<A> fieldN(String name, F<T, A> getter, Class<A> clazz) {
        return field(name, getter, getNullSafeCodec(clazz));
    }

    <A> _1<A> field(String name, F<T, A> getter, Class<A> clazz) {
        return field(name, getter, getNullUnsafeCodec(clazz));
    }

    class _1<A> {
        public Codec<T, E> map(F<A, T> ctor) {
            return core.createObjectCodec(
                    fields,
                    arr -> ctor.apply((A)arr[0]));
        }

        <B> _2<B> field(String name, F<T, B> getter, Codec<B, E> codec) {
            fields.put(name, new FieldCodec<T, E>(getter, codec));
            return new _2<B>();
        }

        <B> _2<B> fieldN(String name, F<T, B> getter, Class<B> clazz) {
            return field(name, getter, getNullSafeCodec(clazz));

        }

        <B> _2<B> field(String name, F<T, B> getter, Class<B> clazz) {
            return field(name, getter, getNullUnsafeCodec(clazz));
        }

        class _2<B> {
            public Codec<T, E> map(F2<A, B, T> ctor) {
                return core.createObjectCodec(
                        fields,
                        arr -> ctor.apply((A)arr[0], (B)arr[1]));
            }

            <C> _3<C> field(String name, F<T, C> getter, Codec<C, E> codec) {
                fields.put(name, new FieldCodec<T, E>(getter, codec));
                return new _3<C>();
            }

            <C> _3<C> fieldN(String name, F<T, C> getter, Class<C> clazz) {
                return field(name, getter, getNullSafeCodec(clazz));
            }

            <C> _3<C> field(String name, F<T, C> getter, Class<C> clazz) {
                return field(name, getter, getNullUnsafeCodec(clazz));
            }

            class _3<C> {
                public Codec<T, E> map(F3<A, B, C, T> ctor) {
                    return core.createObjectCodec(
                            fields,
                            arr -> ctor.apply((A)arr[0], (B)arr[1], (C)arr[2]));
                }

                <D> _4<D> field(String name, F<T, D> getter, Codec<D, E> codec) {
                    fields.put(name, new FieldCodec<T, E>(getter, codec));
                    return new _4<D>();
                }

                <D> _4<D> fieldN(String name, F<T, D> getter, Class<D> clazz) {
                    return field(name, getter, getNullSafeCodec(clazz));
                }

                <D> _4<D> field(String name, F<T, D> getter, Class<D> clazz) {
                    return field(name, getter, getNullUnsafeCodec(clazz));
                }

                class _4<D> {
                    public Codec<T, E> map(F4<A, B, C, D, T> ctor) {
                        return core.createObjectCodec(
                                fields,
                                arr -> ctor.apply((A)arr[0], (B)arr[1], (C)arr[2], (D)arr[3]));
                    }

                    <N> _N field(String name, F<T, N> getter, Codec<N, E> codec) {
                        fields.put(name, new FieldCodec<T, E>(getter, codec));
                        return new _N();
                    }

                    <N> _N fieldN(String name, F<T, N> getter, Class<N> clazz) {
                        return field(name, getter, getNullSafeCodec(clazz));
                    }

                    <N> _N field(String name, F<T, N> getter, Class<N> clazz) {
                        return field(name, getter, getNullUnsafeCodec(clazz));
                    }

                    class _N {
                        public Codec<T, E> map(F<Object[], T> ctor) {
                            return core.createObjectCodec(fields, ctor);
                        }

                        <N> _N field(String name, F<T, N> getter, Codec<N, E> codec) {
                            fields.put(name, new FieldCodec<T, E>(getter, codec));
                            return new _N();
                        }

                        <N> _N fieldN(String name, F<T, N> getter, Class<N> clazz) {
                            return field(name, getter, getNullSafeCodec(clazz));
                        }

                        <N> _N field(String name, F<T, N> getter, Class<N> clazz) {
                            return field(name, getter, getNullUnsafeCodec(clazz));
                        }
                    }
                }
            }
        }
    }
}
