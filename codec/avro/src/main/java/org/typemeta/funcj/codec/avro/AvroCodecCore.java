package org.typemeta.funcj.codec.avro;

import org.apache.avro.Schema;
import org.apache.avro.file.*;
import org.apache.avro.generic.GenericRecord;
import org.typemeta.funcj.codec.*;
import org.typemeta.funcj.codec.avro.AvroTypes.*;
import org.typemeta.funcj.codec.impl.*;

import java.io.*;

/**
 * Interface for classes which implement an encoding via JSON.
 */
public class AvroCodecCore
        extends CodecCoreDelegate<WithSchema, Object, Config>
        implements CodecAPI {

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

    public <T> T decode(Schema schema, DataFileReader<GenericRecord> reader) {
        final GenericRecord genRec = reader.next();
        return decodeImpl(Object.class, WithSchema.of(genRec, schema));
    }
}
