package org.typemeta.funcj.codec;

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
public class ObjectCodecBuilderWithArgArray<
        T,
        IN ,
        OUT,
        CFG extends CodecConfig
        > extends ObjectCodecBuilder {

    private final CodecCoreEx<IN, OUT, CFG> core;
    private final Class<T> clazz;

    protected final Map<String, FieldCodec<T, IN, OUT, CFG>> fields = new LinkedHashMap<>();

    public ObjectCodecBuilderWithArgArray(CodecCoreEx<IN, OUT, CFG> core, Class<T> clazz) {
        this.core = core;
        this.clazz = clazz;
    }

    protected Codec<T, IN, OUT, CFG> registration(Codec<T, IN, OUT, CFG> codec) {
        return codec;
    }

    private <X> Codec<X, IN, OUT, CFG> getCodec(Class<X> clazz) {
        return core.getCodec(clazz);
    }

    public <A> _1<A> field(String name, F<T, A> getter, Codec<A, IN, OUT, CFG> codec) {
        fields.put(name, new FieldCodec<>(getter, codec));
        return new _1<A>();
    }

    public <A> _1<A> field(String name, F<T, A> getter, Class<A> clazz) {
        return field(name, getter, getCodec(clazz));
    }

    public <A> _1<A> field (
            String name,
            F<T, A> getter,
            Class<A> clazz,
            Class<?> elemClass) {
        return field(
                name,
                getter,
                (Codec<A, IN, OUT, CFG>)core.getCollCodec((Class)clazz, core.getCodec(elemClass))
        );
    }

    public class _1<A> {
        public void construct(F<A, T> ctor) {
            registration(
                    core.createObjectCodecWithArgArray(
                            clazz,
                            fields,
                            arr -> ctor.apply((A)arr[0])));
        }

        public <B> _2<B> field(String name, F<T, B> getter, Codec<B, IN, OUT, CFG> codec) {
            fields.put(name, new FieldCodec<>(getter, codec));
            return new _2<B>();
        }

        public <B> _2<B> field(String name, F<T, B> getter, Class<B> clazz) {
            return field(name, getter, getCodec(clazz));
        }

        public <B> _2<B> field(
                String name,
                F<T, B> getter,
                Class<B> clazz,
                Class<?> elemClass) {
            return field(
                    name,
                    getter,
                    (Codec<B, IN, OUT, CFG>)core.getCollCodec((Class)clazz, core.getCodec(elemClass))
            );
        }

        public class _2<B> {
            public void construct(F2<A, B, T> ctor) {
                registration(
                        core.createObjectCodecWithArgArray(
                                clazz,
                                fields,
                                arr -> ctor.apply((A)arr[0], (B)arr[1])));
            }

            public <C> _3<C> field(String name, F<T, C> getter, Codec<C, IN, OUT, CFG> codec) {
                fields.put(name, new FieldCodec<>(getter, codec));
                return new _3<C>();
            }

            public <C> _3<C> field(String name, F<T, C> getter, Class<C> clazz) {
                return field(name, getter, getCodec(clazz));
            }

            public <C> _3<C> field(
                    String name,
                    F<T, C> getter,
                    Class<C> clazz,
                    Class<?> elemClass) {
                return field(
                        name,
                        getter,
                        (Codec<C, IN, OUT, CFG>)core.getCollCodec((Class)clazz, core.getCodec(elemClass))
                );
            }

            public class _3<C> {
                public void construct(F3<A, B, C, T> ctor) {
                    registration(
                            core.createObjectCodecWithArgArray(
                                    clazz,
                                    fields,
                                    arr -> ctor.apply((A)arr[0], (B)arr[1], (C)arr[2])));
                }

                public <D> _4<D> field(String name, F<T, D> getter, Codec<D, IN, OUT, CFG> codec) {
                    fields.put(name, new FieldCodec<>(getter, codec));
                    return new _4<D>();
                }

                public <D> _4<D> field(String name, F<T, D> getter, Class<D> clazz) {
                    return field(name, getter, getCodec(clazz));
                }

                public <D> _4<D> field(
                        String name,
                        F<T, D> getter,
                        Class<D> clazz,
                        Class<?> elemClass) {
                    return field(
                            name,
                            getter,
                            (Codec<D, IN, OUT, CFG>)core.getCollCodec((Class)clazz, core.getCodec(elemClass))
                    );
                }

                public class _4<D> {
                    public void construct(Functions.F4<A, B, C, D, T> ctor) {
                        registration(
                                core.createObjectCodecWithArgArray(
                                        clazz,
                                        fields,
                                        arr -> ctor.apply((A)arr[0], (B)arr[1], (C)arr[2], (D)arr[3])));
                    }

                    public <N> _N field(String name, F<T, N> getter, Codec<N, IN, OUT, CFG> codec) {
                        fields.put(name, new FieldCodec<>(getter, codec));
                        return new _N();
                    }

                    public <N> _N field(String name, F<T, N> getter, Class<N> clazz) {
                        return field(name, getter, getCodec(clazz));
                    }

                    public <N> _N field(
                            String name,
                            F<T, N> getter,
                            Class<N> clazz,
                            Class<?> elemClass) {
                        return field(
                                name,
                                getter,
                                (Codec<N, IN, OUT, CFG>)core.getCollCodec((Class)clazz, core.getCodec(elemClass))
                        );
                    }

                    public class _N {
                        public void construct(ArgArrayTypeCtor<T> ctor) {
                            registration(core.createObjectCodecWithArgArray(clazz, fields, ctor));
                        }

                        public <N> _N field(String name, F<T, N> getter, Codec<N, IN, OUT, CFG> codec) {
                            fields.put(name, new FieldCodec<>(getter, codec));
                            return new _N();
                        }

                        public <N> _N field(String name, F<T, N> getter, Class<N> clazz) {
                            return field(name, getter, getCodec(clazz));
                        }

                        public <N> _N field(
                                String name,
                                F<T, N> getter,
                                Class<N> clazz,
                                Class<?> elemClass) {
                            return field(
                                    name,
                                    getter,
                                    (Codec<N, IN, OUT, CFG>)core.getCollCodec((Class)clazz, core.getCodec(elemClass))
                            );
                        }
                    }
                }
            }
        }
    }
}
