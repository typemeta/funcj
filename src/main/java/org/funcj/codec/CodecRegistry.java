package org.funcj.codec;

public interface CodecRegistry<E> {

    Codec.BooleanCodec<E> booleanCodec();

    default <T> Codec<T, E> objectCodec(ClassDef.Registry registry, Class<T> clazz) {
        return objectCodec(registry.get(clazz));
    }

    <T> Codec<T, E> objectCodec(ClassDef classDef);
}
