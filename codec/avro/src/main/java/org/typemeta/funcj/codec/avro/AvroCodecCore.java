package org.typemeta.funcj.codec.avro;

import org.apache.avro.Schema;
import org.apache.avro.file.*;
import org.apache.avro.generic.*;
import org.typemeta.funcj.codec.CodecStrAPI;
import org.typemeta.funcj.codec.avro.AvroTypes.*;
import org.typemeta.funcj.codec.impl.*;
import org.typemeta.funcj.codec.utils.CodecException;
import org.typemeta.funcj.control.Either;

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
    ) throws IOException {
        final DataFileWriter<GenericRecord> writer =
                new DataFileWriter<GenericRecord>(new GenericDatumWriter<GenericRecord>(schema))
                        .create(schema, os);
        return encode(clazz, schema, value, writer);
    }

    public <T> T decode(Class<? super T> clazz, DataFileStream<GenericRecord> dfs) {
        final GenericRecord genRec = dfs.next();
        return decodeImpl(clazz, WithSchema.of(genRec, genRec.getSchema()));
    }

    public <T> T decode(Class<? super T> clazz, Schema schema, InputStream is) throws IOException {
        try (final DataFileStream<GenericRecord> dfs =
                new DataFileStream<>(is, new GenericDatumReader<>(schema))) {
            return decode(clazz, dfs);
        }
    }

    @Override
    public <T> OutputStream encode(
            Class<? super T> clazz,
            T value,
            OutputStream os
    ) {
        final Schema schema = Codecs.avroSchemaCodec().encodeImpl(clazz, value, Either.left("/")).right();
        System.out.println(schema);
        try (DataFileWriter<GenericRecord> dfw = encode(clazz, schema, value, os)) {
            return os;
        } catch (IOException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public <T> T decode(Class<? super T> clazz, InputStream is) {
        final Schema schema = GenerateSchema.apply(clazz);
        try {
            return decode(clazz, schema, is);
        } catch (IOException ex) {
            throw new CodecException(ex);
        }
    }
}
