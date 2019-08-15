package org.typemeta.funcj.codec.avro.schema;

import org.apache.avro.Schema;
import static org.typemeta.funcj.codec.avro.AvroTypes.Config;

import org.typemeta.funcj.codec.avro.AvroConfig;
import org.typemeta.funcj.codec.impl.*;
import org.typemeta.funcj.control.Either;
import org.typemeta.funcj.data.Unit;

public class AvroSchemaCodecCore
        extends CodecCoreDelegate<Unit, Either<String, Schema>, Config> {
    public AvroSchemaCodecCore(AvroSchemaCodecFormat format) {
        super(new CodecCoreImpl<>(format));
    }

    public AvroSchemaCodecCore(Config config) {
        this(new AvroSchemaCodecFormat(config));
    }

    public AvroSchemaCodecCore() {
        this(new AvroConfig());
    }
}
