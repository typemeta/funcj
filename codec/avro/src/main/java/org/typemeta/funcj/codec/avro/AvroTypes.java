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

    public static class WithSchema<T> {
        public static <T> WithSchema<T> of(T value, Schema schema) {
            return new WithSchema<>(value, schema);
        }

        public final T value;
        public final Schema schema;

        public WithSchema(T value, Schema schema) {
            this.value = value;
            this.schema = schema;
        }
    }
}
