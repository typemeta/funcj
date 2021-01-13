package org.typemeta.funcj.codec2.json;

import org.typemeta.funcj.codec2.core.*;
import org.typemeta.funcj.codec2.core.PrimitiveCodecs.BooleanCodec;
import org.typemeta.funcj.codec2.core.PrimitiveCodecs.IntegerCodec;
import org.typemeta.funcj.codec2.core.fields.FieldCodec;
import org.typemeta.funcj.codec2.core.utils.CodecException;
import org.typemeta.funcj.codec2.json.JsonTypes.InStream;
import org.typemeta.funcj.codec2.json.JsonTypes.OutStream;
import org.typemeta.funcj.json.parser.JsonEvent;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class JsonCodecFormat implements StreamCodecFormat<InStream, OutStream> {
    private static final JsonCodecFormat INSTANCE = new JsonCodecFormat();

    public static JsonCodecFormat instance () {
        return INSTANCE;
    }

    private final BooleanCodec<InStream, OutStream> BOOLEAN_CODEC = new BooleanCodec<InStream, OutStream>() {
        @Override
        public OutStream encodeBool(EncoderCore<OutStream> core, Context ctx, boolean value, OutStream os) {
            return (OutStream)os.writeBoolean(value);
        }

        @Override
        public boolean decodeBool(DecoderCore<InStream> core, Context ctx, InStream is) {
            return is.readBoolean();
        }
    };

    private final Codec<boolean[], InStream, OutStream> BOOLEAN_ARRAY_CODEC = new FinalCodec<boolean[], InStream, OutStream>() {
        @Override
        public Class<boolean[]> type() {
            return boolean[].class;
        }

        @Override
        public OutStream encodeImpl(EncoderCore<OutStream> core, Context ctx, boolean[] value, OutStream os) {
            os.startArray();
            for (boolean val : value) {
                booleanCodec().encodeBool(core, ctx, val, os);
            }
            return os.endArray();
        }

        @Override
        public boolean[] decodeImpl(DecoderCore<InStream> core, Context ctx, InStream is) {
            boolean[] arr = new boolean[core.config().defaultArraySize()];
            is.startArray();
            int i = 0;
            while (is.notEOF() && is.currentEventType() != JsonEvent.Type.ARRAY_END) {
                if (i == arr.length) {
                    arr = Arrays.copyOf(arr, core.config().newArraySize(arr.length));
                }
                arr[i++] = booleanCodec().decodeBool(core, ctx, is);
            }
            is.endArray();
            return Arrays.copyOf(arr, i);
        }
    };

    private final IntegerCodec<InStream, OutStream> INTEGER_CODEC = new IntegerCodec<InStream, OutStream>() {
        @Override
        public OutStream encodeInt(EncoderCore<OutStream> core, Context ctx, int value, OutStream os) {
            return os.writeInt(value);
        }

        @Override
        public int decodeInt(DecoderCore<InStream> core, Context ctx, InStream is) {
            return is.readInt();
        }
    };

    private final Codec<int[], InStream, OutStream> INTEGER_ARRAY_CODEC = new FinalCodec<int[], InStream, OutStream>() {
        @Override
        public Class<int[]> type() {
            return int[].class;
        }

        @Override
        public OutStream encodeImpl(EncoderCore<OutStream> core, Context ctx, int[] value, OutStream os) {
            final IntegerCodec<InStream, OutStream> elemCodec = integerCodec();

            os.startArray();
            for (int val : value) {
                elemCodec.encodeInt(core, ctx, val, os);
            }
            return os.endArray();
        }

        @Override
        public int[] decodeImpl(DecoderCore<InStream> core, Context ctx, InStream is) {
            final IntegerCodec<InStream, OutStream> elemCodec = integerCodec();

            int[] arr = new int[core.config().defaultArraySize()];
            is.startArray();
            int i = 0;
            while (is.notEOF() && is.currentEventType() != JsonEvent.Type.ARRAY_END) {
                if (i == arr.length) {
                    arr = Arrays.copyOf(arr, core.config().newArraySize(arr.length));
                }
                arr[i++] = elemCodec.decodeInt(core, ctx, is);
            }
            is.endArray();

            if (i == arr.length) {
                return arr;
            } else {
                return Arrays.copyOf(arr, i);
            }
        }
    };

    private final Codec<String, InStream, OutStream> STRING_CODEC = new FinalCodec<String, InStream, OutStream>() {
        @Override
        public Class<String> type() {
            return String.class;
        }

        @Override
        public OutStream encodeImpl(EncoderCore<OutStream> core, Context ctx, String value, OutStream os) {
            return os.writeString(value);
        }

        @Override
        public String decodeImpl(DecoderCore<InStream> core, Context ctx, InStream is) {
            return is.readString();
        }
    };

    @Override
    public <T> EncodeResult<OutStream> encodeNull(EncoderCore<OutStream> core, Context ctx, T val, OutStream os) {
        if (val == null) {
            return new EncodeResult<>(true, os.writeNull());
        } else {
            return new EncodeResult<>(false, os);
        }
    }

    @Override
    public boolean decodeNull(DecoderCore<InStream> core, Context ctx, InStream is) {
        if (is.currentEventType().equals(JsonEvent.Type.NULL)) {
            is.readNull();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public BooleanCodec<InStream, OutStream> booleanCodec() {
        return BOOLEAN_CODEC;
    }

    @Override
    public Codec<boolean[], InStream, OutStream> booleanArrayCodec() {
        return BOOLEAN_ARRAY_CODEC;
    }

    @Override
    public IntegerCodec<InStream, OutStream> integerCodec() {
        return INTEGER_CODEC;
    }

    @Override
    public Codec<int[], InStream, OutStream> integerArrayCodec() {
        return INTEGER_ARRAY_CODEC;
    }

    @Override
    public Codec<String, InStream, OutStream> stringCodec() {
        return STRING_CODEC;
    }

    @Override
    public <T> Codec<T, InStream, OutStream> objectCodec(Class<T> type, Map<String, FieldCodec<?, InStream, OutStream>> fieldCodecs, ObjectCreator<T> ctor) {
        if (Modifier.isFinal(type.getModifiers())) {
            return new FinalCodec<T, InStream, OutStream>() {
                @Override
                public Class<T> type() {
                    return type;
                }

                @Override
                public OutStream encodeImpl(EncoderCore<OutStream> core, Context ctx, T value, OutStream os) {
                    os.startObject();

                    fieldCodecs.forEach((name, field) -> {
                        os.writeField(name);
                        ((FieldCodec<T, InStream, OutStream>)field).encodeField(core, ctx, value, os);
                    });

                    return os.endObject();
                }

                @Override
                public T decodeImpl(DecoderCore<InStream> core, Context ctx, InStream is) {
                    is.startObject();

                    final Set<String> expNames = fieldCodecs.keySet();
                    final Set<String> actNames = new HashSet<>();
                    final T value = ctor.create();

                    while (is.notEOF() && is.currentEventType() != JsonEvent.Type.OBJECT_END) {
                        final String name = is.readFieldName();
                        if (!expNames.contains(name)) {
                            if (core.config().failOnUnrecognisedFields()) {
                                throw new CodecException(
                                        "Field name '" + name + "' unexpected for type " + type +
                                                " at location " + is.location());
                            } else {
                                is.skipNode();
                            }
                        } else if (actNames.contains(name)) {
                            throw new CodecException(
                                    "Duplicate field name '" + name + "' for type " + type +
                                            " at location " + is.location());
                        } else {
                            actNames.add(name);
                            ((FieldCodec<T, InStream, OutStream>)fieldCodecs.get(name)).decodeField(core, ctx, value, is);
                        }
                    }

                    core.config().checkFields(type, expNames, actNames);

                    is.endObject();

                    return value;
                }
            };
        } else {
            return new NonFinalCodec<T, InStream, OutStream>() {

                @Override
                public Class<T> type() {
                    return type;
                }

                @Override
                public OutStream encodeImpl(EncoderCore<OutStream> core, Context ctx, T value, OutStream os) {
                    os.startObject();

                    fieldCodecs.forEach((name, field) -> {
                        os.writeField(name);
                        ((FieldCodec<T, InStream, OutStream>)field).encodeField(core, ctx, value, os);
                    });

                    return os.endObject();
                }

                @Override
                public T decodeImpl(DecoderCore<InStream> core, Context ctx, InStream is) {
                    is.startObject();

                    final Set<String> expNames = fieldCodecs.keySet();
                    final Set<String> actNames = new HashSet<>();
                    final T value = ctor.create();

                    while (is.notEOF() && is.currentEventType() != JsonEvent.Type.OBJECT_END) {
                        final String name = is.readFieldName();
                        if (!expNames.contains(name)) {
                            if (core.config().failOnUnrecognisedFields()) {
                                throw new CodecException(
                                        "Field name '" + name + "' unexpected for type " + type +
                                                " at location " + is.location());
                            } else {
                                is.skipNode();
                            }
                        } else if (actNames.contains(name)) {
                            throw new CodecException(
                                    "Duplicate field name '" + name + "' for type " + type +
                                            " at location " + is.location());
                        } else {
                            actNames.add(name);
                            ((FieldCodec<T, InStream, OutStream>)fieldCodecs.get(name)).decodeField(core, ctx, value, is);
                        }
                    }

                    core.config().checkFields(type, expNames, actNames);

                    is.endObject();

                    return value;
                }
            };
        }
    }

    @Override
    public <T> Codec<T[], InStream, OutStream> objectArrayCodec(Class<T[]> arrType, Class<T> elemType, Codec<T, InStream, OutStream> elemCodec) {
        return null;
    }

    @Override
    public <T> EncodeResult<OutStream> encodeDynamic(EncoderCore<OutStream> core, Context ctx, T value, OutStream os, Encoder<T, OutStream> encoder) {
        final Class<T> dynType = (Class<T>) value.getClass();
        if (core.config().dynamicTypeMatch(encoder.type(), dynType)) {
            return new EncodeResult<>(false, os);
        } else if (!core.config().dynamicTypeTags()) {
            final Encoder<T, OutStream> dynCodec = core.getEncoder(dynType);
            dynCodec.encode(core, ctx, value, os);
            return new EncodeResult<>(true, os);
        } else {
            final String typeFieldName = core.config().get(JsonConfig.TYPE_FIELD_NAME);
            final String valueFieldName = core.config().get(JsonConfig.VALUE_FIELD_NAME);

            final Encoder<T, OutStream> dynCodec = core.getEncoder(dynType);

            os.startObject();

            {
                os.writeField(typeFieldName).writeString(core.config().classToName(dynType))
                        .writeField(valueFieldName);

                dynCodec.encode(core, ctx, value, os);
            }

            os.endObject();

            return new EncodeResult<>(true, os);
        }
    }

    @Override
    public <T> T decodeDynamic(DecoderCore<InStream> core, Context ctx, InStream is) {
        if (core.config().dynamicTypeTags() &&
                is.notEOF() &&
                is.currentEventType() == JsonEvent.Type.OBJECT_START
        ) {
            final String typeFieldName = core.config().get(JsonConfig.TYPE_FIELD_NAME);
            final String valueFieldName = core.config().get(JsonConfig.VALUE_FIELD_NAME);

            final JsonEvent.FieldName typeField = new JsonEvent.FieldName(typeFieldName);

            final JsonEvent next = is.event(1);
            if (next.equals(typeField)) {
                is.startObject();

                final T val;
                {
                    is.readFieldName(typeFieldName);
                    final String typeName = is.readString();

                    final String field2 = is.readFieldName();
                    if (!field2.equals(valueFieldName)) {
                        throw new CodecException("Was expecting field '" + valueFieldName + "' but got '" + field2 + "'");
                    }

                    val = core.getDecoder(core.config().<T>nameToClass(typeName)).decode(core, ctx, is);
                }

                is.endObject();

                return val;
            }
        }

        // null indicates a dynamic encoded value was not found.
        return null;
    }
}
