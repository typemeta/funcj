package org.typemeta.funcj.codec;

import org.typemeta.funcj.codec.bytes.ArgMapTypeCtor;
import org.typemeta.funcj.functions.Functions;

import java.util.*;

public class CodecCoreDelegate<IN, OUT, CFG extends CodecConfig>
        implements CodecCore<IN, OUT, CFG> {

    protected final CodecCoreEx<IN, OUT, CFG> delegate;

    public CodecCoreDelegate(CodecCoreEx<IN, OUT, CFG> delegate) {
        this.delegate = delegate;
    }

    @Override
    public CFG config() {
        return delegate.config();
    }

    @Override
    public CodecCoreEx<IN, OUT, CFG> getCodecCoreEx() {
        return delegate;
    }

    @Override
    public <T> void registerCodec(Class<? extends T> clazz, Codec<T, IN, OUT, CFG> codec) {
        delegate.registerCodec(clazz, codec);
    }

    @Override
    public <T> ObjectCodecBuilderWithArgArray<T, IN, OUT, CFG> registerCodecWithArgArray(Class<T> clazz) {
        return delegate.registerCodecWithArgArray(clazz);
    }

    @Override
    public <T> ObjectCodecBuilderWithArgMap<T, IN, OUT, CFG> registerCodecWithArgMap(Class<T> clazz) {
        return delegate.registerCodecWithArgMap(clazz);
    }

    @Override
    public <T> void registerStringProxyCodec(Class<T> clazz, Functions.F<T, String> encode, Functions.F<String, T> decode) {
        delegate.registerStringProxyCodec(clazz, encode, decode);
    }

    @Override
    public <T> void registerNoArgsCtor(Class<? extends T> clazz, NoArgsTypeCtor<T> typeCtor) {
        delegate.registerNoArgsCtor(clazz, typeCtor);
    }

    @Override
    public <T> void registerArgArrayCtor(Class<? extends T> clazz, ArgArrayTypeCtor<T> typeCtor) {
        delegate.registerArgArrayCtor(clazz, typeCtor);
    }

    @Override
    public <T> void registerArgMapTypeCtor(Class<? extends T> clazz, ArgMapTypeCtor<T> typeCtor) {
        delegate.registerArgMapTypeCtor(clazz, typeCtor);
    }

    @Override
    public <T> OUT encode(Class<? super T> clazz, T val, OUT out) {
        return delegate.encode(clazz, val, out);
    }

    @Override
    public <T> T decode(Class<? super T> clazz, IN in) {
        return delegate.decode(clazz, in);
    }

    @Override
    public <T> Codec<T, IN, OUT, CFG> getCodec(Class<T> clazz) {
        return delegate.getCodec(clazz);
    }

    @Override
    public <T> Codec<Collection<T>, IN, OUT, CFG> getCollCodec(
            Class<Collection<T>> collType,
            Class<T> elemType) {
        return delegate.getCollCodec(collType, elemType);
    }

    @Override
    public <K, V> Codec<Map<K, V>, IN, OUT, CFG> getMapCodec(
            Class<Map<K, V>> mapType,
            Class<K> keyType,
            Class<V> valType) {
        return delegate.getMapCodec(mapType, keyType, valType);
    }
}
