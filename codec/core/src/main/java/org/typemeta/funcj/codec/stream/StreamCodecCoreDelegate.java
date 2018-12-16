package org.typemeta.funcj.codec.stream;

import org.typemeta.funcj.codec.*;
import org.typemeta.funcj.codec.bytes.ArgMapTypeCtor;
import org.typemeta.funcj.functions.Functions;

public class StreamCodecCoreDelegate<
        IN extends StreamCodecFormat.Input<IN>,
        OUT extends StreamCodecFormat.Output<OUT>,
        CFG extends CodecConfig
        > implements CodecCore<IN, OUT, CFG> {

    protected final CodecCore<IN, OUT, CFG> delegate;

    public StreamCodecCoreDelegate(CodecCore<IN, OUT, CFG> delegate) {
        this.delegate = delegate;
    }

    @Override
    public CFG config() {
        return delegate.config();
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
    public <T> void encode(Class<? super T> clazz, T val, OUT out) {
        delegate.encode(clazz, val, out);
    }

    @Override
    public <T> T decode(Class<? super T> clazz, IN in) {
        return delegate.decode(clazz, in);
    }
}
