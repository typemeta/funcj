package org.typemeta.funcj.codec.avro.schema;

import org.typemeta.funcj.codec.avro.*;
import org.typemeta.funcj.codec.avro.AvroTypes.WithSchema;

import static java.util.stream.Collectors.toList;
import static org.typemeta.funcj.codec.avro.AvroTypes.Config;

public class AvroSchemaCodecCore
        extends CodecCoreDelegate<WithSchema, Object, Config> {

    public AvroSchemaCodecCore(AvroSchemaCodecFormat format) {
        super(new AvcroSchemaCodecCoreImpl(format));
    }

    public AvroSchemaCodecCore(Config config) {
        this(new AvroSchemaCodecFormat(config));
    }

    public AvroSchemaCodecCore() {
        this(new AvroConfig());
    }

    protected static class AvcroSchemaCodecCoreImpl extends CodecCoreImpl<WithSchema, Object, Config> {

        public AvcroSchemaCodecCoreImpl(CodecFormat<WithSchema, Object, Config> format) {
            super(format);
        }

        @Override
        public <T> Codec<T, WithSchema, Object, Config> createObjectCodec(
                Class<T> clazz,
                Map<String, FieldCodec<WithSchema, Object, Config>> fieldCodecs,
                NoArgsTypeCtor<T> ctor
        ) {
            final class BuilderImpl implements ObjectMeta.Builder<T> {
                final T val;

                BuilderImpl() {
                    this.val = ctor.construct();
                }

                @Override
                public T construct() {
                    return val;
                }
            }

            final List<ObjectMeta.Field<T, WithSchema, Object, BuilderImpl>> fieldMetas =
                    fieldCodecs.entrySet().stream()
                            .map(en -> {
                                final String name = en.getKey();
                                final FieldCodec<WithSchema, Object, Config> codec = en.getValue();
                                return new ObjectMeta.Field<T, WithSchema, Object, BuilderImpl>() {
                                    @Override
                                    public String name() {
                                        return name;
                                    }

                                    @Override
                                    public Object encodeField(T val, Object out) {
                                        return codec.encodeField(AvcroSchemaCodecCoreImpl.this, val, out + "." + name);
                                    }

                                    @Override
                                    public BuilderImpl decodeField(BuilderImpl acc, WithSchema in) {
                                        throw AvroSchemaTypes.notImplemented();
                                    }
                                };
                            }).collect(toList());

            return format().createObjectCodec(
                    clazz,
                    new ObjectMeta<T, WithSchema, Object, BuilderImpl>() {
                        @Override
                        public Iterator<Field<T, WithSchema, Object, BuilderImpl>> iterator() {
                            return fieldMetas.iterator();
                        }

                        @Override
                        public BuilderImpl startDecode() {
                            return new BuilderImpl();
                        }
                    }
            );
        }
    }
}
