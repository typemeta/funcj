package org.typemeta.funcj.codec;

import org.typemeta.funcj.codec.bytes.ArgMapTypeCtor;
import org.typemeta.funcj.functions.Functions.F;

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
public class ObjectCodecBuilderWithArgMap<
        T,
        IN,
        OUT,
        CFG extends CodecConfig
        > extends ObjectCodecBuilder {

    private final CodecCoreEx<IN, OUT, CFG> core;
    private final Class<T> clazz;

    protected final Map<String, FieldCodec<T, IN, OUT, CFG>> fields = new LinkedHashMap<>();

    public ObjectCodecBuilderWithArgMap(CodecCoreEx<IN, OUT, CFG> core, Class<T> clazz) {
        this.core = core;
        this.clazz = clazz;
    }

    protected Codec<T, IN, OUT, CFG> registration(Codec<T, IN, OUT, CFG> codec) {
        return codec;
    }

    private <X> Codec<X, IN, OUT, CFG> getCodec(Class<X> clazz) {
        return core.getCodec(clazz);
    }

    public <A> ObjectCodecBuilderWithArgMap<T, IN, OUT, CFG> field(String name, F<T, A> getter, Codec<A, IN, OUT, CFG> codec) {
        fields.put(name, new FieldCodec<>(getter, codec));
        return this;
    }

    public <A> ObjectCodecBuilderWithArgMap<T, IN, OUT, CFG> field(String name, F<T, A> getter, Class<A> clazz) {
        return field(name, getter, getCodec(clazz));
    }

    public <A> ObjectCodecBuilderWithArgMap<T, IN, OUT, CFG> field(
            String name, F<T, A> getter,
            Class<A> clazz,
            Class<?> elemClass) {
        return field(name, getter, (Codec<A, IN, OUT, CFG>)core.getCollCodec((Class)clazz, core.getCodec(elemClass)));
    }

    public Codec<T, IN, OUT, CFG> construct(ArgMapTypeCtor<T> ctor) {
        return registration(
                core.createObjectCodecWithArgMap(clazz, fields, ctor)
        );
    }
}
