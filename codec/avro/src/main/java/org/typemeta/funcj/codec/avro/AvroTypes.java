package org.typemeta.funcj.codec.avro;

import org.apache.avro.Schema;
import org.typemeta.funcj.codec.CodecConfig;
import org.typemeta.funcj.codec.stream.StreamCodecFormat;

import java.io.*;
import java.math.BigInteger;

public abstract class AvroTypes {

    /**
     * Interface for classes which provide configuration information
     * for {@link AvroCodecCore} implementations.
     */
    public interface Config extends CodecConfig {
    }

    public static class WithSchema {
        public static WithSchema of(Object value, Schema schema) {
            return new WithSchema(value, schema);
        }

        private final Object value;
        private final Schema schema;

        public WithSchema(Object value, Schema schema) {
            this.value = value;
            this.schema = schema;
        }

        public <T> T value() {
            return (T)value;
        }

        public Schema schema() {
            return schema;
        }
    }
}
