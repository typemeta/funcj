package org.typemeta.funcj.codec2.core;

import org.typemeta.funcj.codec2.core.fields.FieldReflCodecGenerator;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class CodecCoreImpl<IN, OUT> implements CodecCore<IN, OUT> {
    protected final CodecFormat<IN, OUT> format;
    protected final CodecConfig config;
    protected final CodecGenerator<IN, OUT> codecGenerator;

    protected final ConcurrentMap<Class<?>, Codec<?, IN, OUT>> codecMap;

    protected CodecCoreImpl(
            CodecFormat<IN, OUT> format,
            CodecConfig config,
            CodecGenerator<IN, OUT> codecGenerator,
            ConcurrentMap<Class<?>, Codec<?, IN, OUT>> codecMap
    ) {
        this.format = format;
        this.config = config;
        this.codecGenerator = codecGenerator;
        this.codecMap = codecMap;
    }

    protected CodecCoreImpl(
            CodecFormat<IN, OUT> format,
            CodecConfig config,
            CodecGenerator<IN, OUT> codecGenerator
    ) {
        this(format, config, codecGenerator, new ConcurrentHashMap<>());
    }

    protected CodecCoreImpl(
            CodecFormat<IN, OUT> format,
            CodecConfig config
    ) {
        this(format, config, new FieldReflCodecGenerator<>(), new ConcurrentHashMap<>());
    }

    @Override
    public CodecFormat<IN, OUT> format() {
        return null;
    }

    @Override
    public CodecConfig config() {
        return null;
    }

    @Override
    public <T> Decoder<T, IN> getDecoder(Class<T> type) {
        return getCodec(type);
    }

    @Override
    public <T> Encoder<T, OUT> getEncoder(Class<T> type) {
        return getCodec(type);
    }

    @Override
    public <T> Codec<T, IN, OUT> getCodec(Class<T> type) {
        // First attempt, without locking.
        if (codecMap.containsKey(type)) {
            return (Codec<T, IN, OUT>)codecMap.get(type);
        } else {
            final CodecRef<T, IN, OUT> codecRef;
            // Lock the map and try again.
            synchronized(codecMap) {
                if (codecMap.containsKey(type)) {
                    return (Codec<T, IN, OUT>)codecMap.get(type);
                } else {
                    // Ok, it's definitely not there, so add a CodecRef
                    // (in case the class has a recursive self-reference).
                    codecRef = new CodecRef<>();
                    codecMap.put(type, codecRef);
                }
            }

            // Initialise the CodecRef, and overwrite the registry entry with the real Codec.
            codecMap.put(type, codecRef.setIfUninitialised(() -> codecGenerator.generate(this, type)));

            return (Codec<T, IN, OUT>)codecMap.get(type);
        }
    }
}
