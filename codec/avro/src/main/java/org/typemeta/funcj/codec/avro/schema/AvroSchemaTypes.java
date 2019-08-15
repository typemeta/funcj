package org.typemeta.funcj.codec.avro.schema;

import org.typemeta.funcj.codec.CodecConfig;
import org.typemeta.funcj.codec.utils.CodecException;

public abstract class AvroSchemaTypes {
    protected static CodecException notImplemented() {
        return new CodecException("Not implemented");
    }
}
