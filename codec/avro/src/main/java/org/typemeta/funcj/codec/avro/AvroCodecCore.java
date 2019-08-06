package org.typemeta.funcj.codec.avro;

import org.apache.avro.Schema;
import org.apache.avro.file.*;
import org.apache.avro.generic.*;
import org.typemeta.funcj.codec.*;
import org.typemeta.funcj.codec.avro.AvroTypes.*;
import org.typemeta.funcj.codec.impl.*;
import org.typemeta.funcj.codec.utils.CodecException;

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
            Schema schema,
            T value,
            DataFileWriter<GenericRecord> writer
    ) throws IOException {
        final GenericRecord genRec = (GenericRecord)encodeImpl(Object.class, value, schema);
        writer.append(genRec);
        return writer;
    }

    public <T> DataFileWriter<GenericRecord> encode(
            Schema schema,
            T value,
            OutputStream os
    ) throws IOException {
        final DataFileWriter<GenericRecord> writer =
                new DataFileWriter<GenericRecord>(new GenericDatumWriter<GenericRecord>(schema))
                        .create(schema, os);
        return encode(schema, value, writer);
    }

    public <T> T decode(DataFileStream<GenericRecord> dfs) {
        final GenericRecord genRec = dfs.next();
        return decodeImpl(Object.class, WithSchema.of(genRec, genRec.getSchema()));
    }

    public <T> T decode(Schema schema, InputStream is) throws IOException {
        try (final DataFileStream<GenericRecord> dfs =
                new DataFileStream<GenericRecord>(is, new GenericDatumReader<GenericRecord>(schema))) {
            return decode(dfs);
        }
    }

    @Override
    public <T> OutputStream encode(
            Class<? super T> clazz,
            T value,
            OutputStream os
    ) {
        final Schema schema = GenerateSchema.apply(clazz);
        System.out.println(schema);
        try (DataFileWriter<GenericRecord> dfw = encode(schema, value, os)) {
            return os;
        } catch (IOException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public <T> T decode(Class<? super T> clazz, InputStream is) {
        final Schema schema = GenerateSchema.apply(clazz);
        try {
            return decode(schema, is);
        } catch (IOException ex) {
            throw new CodecException(ex);
        }
    }
}
