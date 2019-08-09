package org.typemeta.funcj.codec.avro.schema;

import org.typemeta.funcj.codec.CodecConfig;
import org.typemeta.funcj.codec.utils.CodecException;

public abstract class AvroSchemaTypes {

    protected static CodecException notImplemented() {
        return new CodecException("Not implemented");
    }

    /**
     * Interface for classes which provide configuration information
     * for {@link AvroCodecCore} implementations.
     */
    public interface Config extends CodecConfig {
    }
}
