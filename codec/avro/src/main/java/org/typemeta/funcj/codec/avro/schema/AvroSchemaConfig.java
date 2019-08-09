package org.typemeta.funcj.codec.avro.schema;

import org.typemeta.funcj.codec.impl.CodecConfigImpl;
import org.typemeta.funcj.codec.utils.NotSupportedException;

/**
 * Base class for {@link AvroSchemaTypes.Config} implementations.
 */
public class AvroSchemaConfig extends CodecConfigImpl implements AvroSchemaTypes.Config {

    public static class Builder extends AbstractBuilder<Builder, AvroSchemaTypes.Config> {

        @Override
        public AvroSchemaTypes.Config build() {
            return new AvroSchemaConfig(this);
        }

        @Override
        public Builder dynamicTypeTags(boolean enable) {
            throw new NotSupportedException();
        }

        @Override
        public Builder failOnNoTypeConstructor(boolean enable) {
            throw new NotSupportedException();
        }

        @Override
        public Builder failOnUnrecognisedFields(boolean enable) {
            throw new NotSupportedException();
        }
    }

    public static AvroSchemaConfig.Builder builder() {
        return new AvroSchemaConfig.Builder();
    }

    public AvroSchemaConfig() {
    }

    public AvroSchemaConfig(Builder builder) {
        super(builder);
    }
}
