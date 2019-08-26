package org.typemeta.funcj.codec.avro;

import org.apache.avro.Schema;
import org.apache.avro.file.*;
import org.apache.avro.generic.*;
import org.typemeta.funcj.codec.*;
import org.typemeta.funcj.codec.Codec;
import org.typemeta.funcj.codec.avro.AvroTypes.*;
import org.typemeta.funcj.codec.avro.schema.AvroSchemaCodecCore;
import org.typemeta.funcj.codec.impl.*;
import org.typemeta.funcj.functions.Functions;
import org.typemeta.funcj.util.Exceptions;

import java.io.*;

/**
 * Interface for classes which implement an encoding via JSON.
 */
public class AvroCodecCore
        extends CodecCoreDelegate<WithSchema, Object, Config>
        implements CodecStrAPI.IO {

    public AvroCodecCore(AvroCodecFormat format) {
        super(new CodecCoreImpl<>(format));
    }

    public AvroCodecCore(Config config) {
        this(new AvroCodecFormat(config));
    }

    public AvroCodecCore() {
        this(new AvroConfig());
    }

    @Override
    public <T> void registerCodec(
            Class<? extends T> clazz,
            Codec<T, WithSchema, Object, Config> codec
    ) {
        super.registerCodec(clazz, codec);
    }

    @Override
    public <T> ObjectCodecBuilderWithArgArray<T, WithSchema, Object, Config> registerCodecWithArgArray(Class<T> clazz) {
        return new ObjectCodecBuilderWithArgArray<T, WithSchema, Object, Config>(delegate, clazz) {
            @Override
            protected Codec<T, WithSchema, Object, Config> registration(Codec<T, WithSchema, Object, Config> codec) {
                registerCodec(clazz, codec);
                return codec;
            }
        };
    }

    @Override
    public <T> ObjectCodecBuilderWithArgMap<T, WithSchema, Object, Config> registerCodecWithArgMap(Class<T> clazz) {
        return new ObjectCodecBuilderWithArgMap<T, WithSchema, Object, Config>(delegate, clazz) {
            @Override
            protected Codec<T, WithSchema, Object, Config> registration(Codec<T, WithSchema, Object, Config> codec) {
                registerCodec(clazz, codec);
                return codec;
            }
        };
    }

    @Override
    public <T> void registerStringProxyCodec(
            Class<T> clazz,
            Functions.F<T, String> encode,
            Functions.F<String, T> decode
    ) {
        super.registerStringProxyCodec(clazz, encode, decode);
    }

    @Override
    public <T> void registerNoArgsCtor(
            Class<? extends T> clazz,
            NoArgsTypeCtor<T> typeCtor
    ) {
        super.registerNoArgsCtor(clazz, typeCtor);
    }

    @Override
    public <T> void registerArgArrayCtor(
            Class<? extends T> clazz,
            ArgArrayTypeCtor<T> typeCtor
    ) {
        super.registerArgArrayCtor(clazz, typeCtor);
    }

    @Override
    public <T> void registerArgMapTypeCtor(
            Class<? extends T> clazz,
            ArgMapTypeCtor<T> typeCtor
    ) {
        super.registerArgMapTypeCtor(clazz, typeCtor);
    }

    public <T> DataFileWriter<GenericRecord> encode(
            Class<? super T> clazz,
            Schema schema,
            T value,
            DataFileWriter<GenericRecord> writer
    ) throws IOException {
        final GenericRecord genRec = (GenericRecord)encodeImpl(clazz, value, schema);
        writer.append(genRec);
        return writer;
    }

    public <T> DataFileWriter<GenericRecord> encode(
            Class<? super T> clazz,
            Schema schema,
            T value,
            OutputStream os
    ) {
        return Exceptions.wrap(() -> {
            final DataFileWriter<GenericRecord> writer =
                    new DataFileWriter<GenericRecord>(new GenericDatumWriter<GenericRecord>())
                            .create(schema, os);
            return encode(clazz, schema, value, writer);
        });
    }

    public <T> T decode(Class<? super T> clazz, DataFileStream<GenericRecord> dfs) {
        final GenericRecord genRec = dfs.next();
        return decodeImpl(clazz, WithSchema.of(genRec, genRec.getSchema()));
    }

    @Override
    public <T> OutputStream encode(
            Class<? super T> clazz,
            T value,
            OutputStream os
    ) {
        throw null;
//        try (DataFileWriter<GenericRecord> dfw = encode(clazz, schema, value, os)) {
//            return os;
//        } catch (IOException ex) {
//            throw new CodecException(ex);
//        }
    }

    @Override
    public <T> T decode(Class<? super T> clazz, InputStream is) {
        return Exceptions.wrap(() -> {
            try (final DataFileStream<GenericRecord> dfs =
                         new DataFileStream<>(is, new GenericDatumReader<>())) {
                return decode(clazz, dfs);
            }
        });
    }
}
