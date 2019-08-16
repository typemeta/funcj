package org.typemeta.funcj.codec.avro.schema;

import static org.typemeta.funcj.codec.avro.AvroTypes.Config;

import org.typemeta.funcj.codec.avro.*;
import org.typemeta.funcj.codec.avro.AvroTypes.WithSchema;
import org.typemeta.funcj.codec.impl.*;

public class AvroSchemaCodecCore
        extends CodecCoreDelegate<WithSchema, Object, Config> {
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
