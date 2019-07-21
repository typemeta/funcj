package org.typemeta.funcj.codec.json;

import org.typemeta.funcj.codec.*;
import org.typemeta.funcj.codec.impl.CollectionCodec;
import org.typemeta.funcj.codec.json.JsonTypes.*;
import org.typemeta.funcj.codec.stream.StreamCodecFormat;
import org.typemeta.funcj.codec.utils.CodecException;
import org.typemeta.funcj.functions.Functions;
import org.typemeta.funcj.json.parser.JsonEvent;

import java.lang.reflect.*;
import java.util.*;

import static org.typemeta.funcj.codec.utils.StreamUtils.toLinkedHashMap;

/**
 * Encoding via JSON streams.
 */
@SuppressWarnings("unchecked")
public class JsonCodecFormat implements StreamCodecFormat<InStream, OutStream, Config> {

    protected final Config config;

    public JsonCodecFormat(Config config) {
        this.config = config;
    }

    @Override
    public Config config() {
        return config;
    }

    @Override
    public <T> IsNull<OutStream> encodeNull(T val, OutStream out) {
        if (val == null) {
            out.writeNull();
            return IsNull.of(true, out);
        } else {
            return IsNull.of(false, out);
        }
    }

    @Override
    public boolean decodeNull(InStream in) {
        if (in.currentEventType().equals(JsonEvent.Type.NULL)) {
            in.readNull();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public <T> IsNull<OutStream> encodeDynamicType(
            CodecCoreEx<InStream, OutStream, Config> core,
            Codec<T, InStream, OutStream, Config> codec,
            T val,
            OutStream out,
            Functions.F<Class<T>, Codec<T, InStream, OutStream, Config>> getDynCodec
    ) {
        final Class<T> dynType = (Class<T>) val.getClass();
        if (config().dynamicTypeMatch(codec.type(), dynType)) {
            return IsNull.of(false, out);
        } else if (!config().dynamicTypeTags()) {
            final Codec<T, InStream, OutStream, Config> dynCodec = getDynCodec.apply(dynType);
            dynCodec.encode(core, val, out);
            return IsNull.of(true, out);
        } else {
            final Codec<T, InStream, OutStream, Config> dynCodec = getDynCodec.apply(dynType);
            out.startObject();

            out.writeField(config.typeFieldName())
                    .writeString(config().classToName(dynType));
            out.writeField(config.valueFieldName());
            dynCodec.encode(core, val, out);

            out.endObject();
            return IsNull.of(true, out);
        }
    }

    @Override
    public <T> T decodeDynamicType(InStream in, Functions.F2<String, InStream, T> decoder) {
        if (!config().dynamicTypeTags()) {
            return null;
        } else if (in.notEOF() && in.currentEventType() == JsonEvent.Type.OBJECT_START) {
            final String typeFieldName = config.typeFieldName();
            final JsonEvent.FieldName typeField = new JsonEvent.FieldName(typeFieldName);
            final String valueFieldName = config.valueFieldName();

            final JsonEvent next = in.event(1);
            if (next.equals(typeField)) {
                in.startObject();

                in.readFieldName(typeFieldName);
                final String typeName = in.readString();

                final String field2 = in.readFieldName();
                if (!field2.equals(valueFieldName)) {
                    throw new CodecException("Was expecting field '" + valueFieldName + "' but got '" + field2 + "'");
                }

                final T val = decoder.apply(typeName, in);

                in.endObject();

                return val;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    protected static class BooleanCodec implements Codec.BooleanCodec<InStream, OutStream, Config> {

        @Override
        public OutStream encodePrim(boolean val, OutStream out) {
            return out.writeBoolean(val);
        }

        @Override
        public boolean decodePrim(InStream in) {
            return in.readBoolean();
        }
    }

    protected final Codec.BooleanCodec<InStream, OutStream, Config> booleanCodec = new BooleanCodec();

    @Override
    public Codec.BooleanCodec<InStream, OutStream, Config> booleanCodec() {
        return booleanCodec;
    }

    protected final Codec<boolean[], InStream, OutStream, Config> booleanArrayCodec =
            new Codec<boolean[], InStream, OutStream, Config>() {

        @Override
        public Class<boolean[]> type() {
            return boolean[].class;
        }

        @Override
        public OutStream encode(CodecCoreEx<InStream, OutStream, Config> core, boolean[] value, OutStream out) {
            out.startArray();
            for (boolean val : value) {
                booleanCodec().encodePrim(val, out);
            }
            return out.endArray();
        }

        @Override
        public boolean[] decode(CodecCoreEx<InStream, OutStream, Config> core, InStream in) {
            boolean[] arr = new boolean[config.defaultArraySize()];
            in.startArray();
            int i = 0;
            while (in.notEOF() && in.currentEventType() != JsonEvent.Type.ARRAY_END) {
                if (i == arr.length) {
                    arr = Arrays.copyOf(arr, config().resizeArray(arr.length));
                }
                arr[i++] = booleanCodec().decodePrim(in);
            }
            in.endArray();
            return Arrays.copyOf(arr, i);
        }
    };

    @Override
    public Codec<boolean[], InStream, OutStream, Config> booleanArrayCodec() {
        return booleanArrayCodec;
    }

    protected static class ByteCodec implements Codec.ByteCodec<InStream, OutStream, Config> {

        @Override
        public OutStream encodePrim(byte val, OutStream out) {
            return out.writeByte(val);
        }

        @Override
        public byte decodePrim(InStream in) {
            return in.readByte();
        }
    }

    protected final Codec.ByteCodec<InStream, OutStream, Config> byteCodec = new ByteCodec();

    @Override
    public Codec.ByteCodec<InStream, OutStream, Config> byteCodec() {
        return byteCodec;
    }

    protected final Codec<byte[], InStream, OutStream, Config> byteArrayCodec =
            new Codec<byte[], InStream, OutStream, Config>() {

        @Override
        public Class<byte[]> type() {
            return byte[].class;
        }

        @Override
        public OutStream encode(CodecCoreEx<InStream, OutStream, Config> core, byte[] value, OutStream out) {
            out.startArray();
            for (byte val : value) {
                byteCodec().encodePrim(val, out);
            }
            return out.endArray();
        }

        @Override
        public byte[] decode(CodecCoreEx<InStream, OutStream, Config> core, InStream in) {
            byte[] arr = new byte[config.defaultArraySize()];
            in.startArray();
            int i = 0;
            while (in.notEOF() && in.currentEventType() != JsonEvent.Type.ARRAY_END) {
                if (i == arr.length) {
                    arr = Arrays.copyOf(arr, config().resizeArray(arr.length));
                }
                arr[i++] = byteCodec().decodePrim(in);
            }
            in.endArray();
            return Arrays.copyOf(arr, i);
        }
    };

    @Override
    public Codec<byte[], InStream, OutStream, Config> byteArrayCodec() {
        return byteArrayCodec;
    }

    protected static class CharCodec implements Codec.CharCodec<InStream, OutStream, Config> {

        @Override
        public OutStream encodePrim(char val, OutStream out) {
            return out.writeChar(val);
        }

        @Override
        public char decodePrim(InStream in ) {
            return in.readChar();
        }
    }

    protected final Codec.CharCodec<InStream, OutStream, Config> charCodec = new CharCodec();

    @Override
    public Codec.CharCodec<InStream, OutStream, Config> charCodec() {
        return charCodec;
    }

    protected final Codec<char[], InStream, OutStream, Config> charArrayCodec =
            new Codec<char[], InStream, OutStream, Config>() {

        @Override
        public Class<char[]> type() {
            return char[].class;
        }

        @Override
        public OutStream encode(CodecCoreEx<InStream, OutStream, Config> core, char[] value, OutStream out) {
            out.startArray();
            for (char val : value) {
                charCodec().encodePrim(val, out);
            }
            return out.endArray();
        }

        @Override
        public char[] decode(CodecCoreEx<InStream, OutStream, Config> core, InStream in) {
            char[] arr = new char[config.defaultArraySize()];
            in.startArray();
            int i = 0;
            while (in.notEOF() && in.currentEventType() != JsonEvent.Type.ARRAY_END) {
                if (i == arr.length) {
                    arr = Arrays.copyOf(arr, config().resizeArray(arr.length));
                }
                arr[i++] = charCodec().decodePrim(in);
            }
            in.endArray();
            return Arrays.copyOf(arr, i);
        }
    };

    @Override
    public Codec<char[], InStream, OutStream, Config> charArrayCodec() {
        return charArrayCodec;
    }

    protected static class ShortCodec implements Codec.ShortCodec<InStream, OutStream, Config> {

        @Override
        public OutStream encodePrim(short val, OutStream out) {
            return out.writeShort(val);
        }

        @Override
        public short decodePrim(InStream in ) {
            return in.readShort();
        }
    }

    protected final Codec.ShortCodec<InStream, OutStream, Config> shortCodec = new ShortCodec();

    @Override
    public Codec.ShortCodec<InStream, OutStream, Config> shortCodec() {
        return shortCodec;
    }

    protected final Codec<short[], InStream, OutStream, Config> shortArrayCodec =
            new Codec<short[], InStream, OutStream, Config>() {

        @Override
        public Class<short[]> type() {
            return short[].class;
        }

        @Override
        public OutStream encode(CodecCoreEx<InStream, OutStream, Config> core, short[] value, OutStream out) {
            out.startArray();
            for (short val : value) {
                shortCodec().encodePrim(val, out);
            }
            return out.endArray();
        }

        @Override
        public short[] decode(CodecCoreEx<InStream, OutStream, Config> core, InStream in) {
            short[] arr = new short[config.defaultArraySize()];
            in.startArray();
            int i = 0;
            while (in.notEOF() && in.currentEventType() != JsonEvent.Type.ARRAY_END) {
                if (i == arr.length) {
                    arr = Arrays.copyOf(arr, config().resizeArray(arr.length));
                }
                arr[i++] = shortCodec().decodePrim(in);
            }
            in.endArray();
            return Arrays.copyOf(arr, i);
        }
    };

    @Override
    public Codec<short[], InStream, OutStream, Config> shortArrayCodec() {
        return shortArrayCodec;
    }

    protected static class IntCodec implements Codec.IntCodec<InStream, OutStream, Config> {

        @Override
        public OutStream encodePrim(int val, OutStream out) {
            return out.writeInt(val);
        }

        @Override
        public int decodePrim(InStream in ) {
            return in.readInt();
        }
    }

    protected final Codec.IntCodec<InStream, OutStream, Config> intCodec = new IntCodec();

    @Override
    public Codec.IntCodec<InStream, OutStream, Config> intCodec() {
        return intCodec;
    }

    protected final Codec<int[], InStream, OutStream, Config> intArrayCodec =
            new Codec<int[], InStream, OutStream, Config>() {

        @Override
        public Class<int[]> type() {
            return int[].class;
        }

        @Override
        public OutStream encode(CodecCoreEx<InStream, OutStream, Config> core, int[] value, OutStream out) {
            out.startArray();
            for (int val : value) {
                intCodec().encodePrim(val, out);
            }
            return out.endArray();
        }

        @Override
        public int[] decode(CodecCoreEx<InStream, OutStream, Config> core, InStream in) {
            int[] arr = new int[config.defaultArraySize()];
            in.startArray();
            int i = 0;
            while (in.notEOF() && in.currentEventType() != JsonEvent.Type.ARRAY_END) {
                if (i == arr.length) {
                    arr = Arrays.copyOf(arr, config().resizeArray(arr.length));
                }
                arr[i++] = intCodec().decodePrim(in);
            }
            in.endArray();
            return Arrays.copyOf(arr, i);
        }
    };

    @Override
    public Codec<int[], InStream, OutStream, Config> intArrayCodec() {
        return intArrayCodec;
    }

    protected static class LongCodec implements Codec.LongCodec<InStream, OutStream, Config> {

        @Override
        public OutStream encodePrim(long val, OutStream out) {
            return out.writeLong(val);
        }

        @Override
        public long decodePrim(InStream in) {
            return in.readLong();
        }
    }

    protected final Codec.LongCodec<InStream, OutStream, Config> longCodec = new LongCodec();

    @Override
    public Codec.LongCodec<InStream, OutStream, Config> longCodec() {
        return longCodec;
    }

    protected final Codec<long[], InStream, OutStream, Config> longArrayCodec =
            new Codec<long[], InStream, OutStream, Config>() {

        @Override
        public Class<long[]> type() {
            return long[].class;
        }

        @Override
        public OutStream encode(CodecCoreEx<InStream, OutStream, Config> core, long[] value, OutStream out) {
            out.startArray();
            for (long val : value) {
                longCodec().encodePrim(val, out);
            }
            return out.endArray();
        }

        @Override
        public long[] decode(CodecCoreEx<InStream, OutStream, Config> core, InStream in) {
            long[] arr = new long[config.defaultArraySize()];
            in.startArray();
            int i = 0;
            while (in.notEOF() && in.currentEventType() != JsonEvent.Type.ARRAY_END) {
                if (i == arr.length) {
                    arr = Arrays.copyOf(arr, config().resizeArray(arr.length));
                }
                arr[i++] = longCodec().decodePrim(in);
            }
            in.endArray();
            return Arrays.copyOf(arr, i);
        }
    };

    @Override
    public Codec<long[], InStream, OutStream, Config> longArrayCodec() {
        return longArrayCodec;
    }

    protected static class FloatCodec implements Codec.FloatCodec<InStream, OutStream, Config> {

        @Override
        public OutStream encodePrim(float val, OutStream out) {
            return out.writeFloat(val);
        }

        @Override
        public float decodePrim(InStream in ) {
            return in.readFloat();
        }
    }

    protected final Codec.FloatCodec<InStream, OutStream, Config> floatCodec = new FloatCodec();

    @Override
    public Codec.FloatCodec<InStream, OutStream, Config> floatCodec() {
        return floatCodec;
    }

    protected final Codec<float[], InStream, OutStream, Config> floatArrayCodec =
            new Codec<float[], InStream, OutStream, Config>() {

        @Override
        public Class<float[]> type() {
            return float[].class;
        }

        @Override
        public OutStream encode(CodecCoreEx<InStream, OutStream, Config> core, float[] value, OutStream out) {
            out.startArray();
            for (float val : value) {
                floatCodec().encodePrim(val, out);
            }
            return out.endArray();
        }

        @Override
        public float[] decode(CodecCoreEx<InStream, OutStream, Config> core, InStream in) {
            float[] arr = new float[config.defaultArraySize()];
            in.startArray();
            int i = 0;
            while (in.notEOF() && in.currentEventType() != JsonEvent.Type.ARRAY_END) {
                if (i == arr.length) {
                    arr = Arrays.copyOf(arr, config().resizeArray(arr.length));
                }
                arr[i++] = floatCodec().decodePrim(in);
            }
            in.endArray();
            return Arrays.copyOf(arr, i);
        }
    };

    @Override
    public Codec<float[], InStream, OutStream, Config> floatArrayCodec() {
        return floatArrayCodec;
    }

    protected static class DoubleCodec implements Codec.DoubleCodec<InStream, OutStream, Config> {

        @Override
        public OutStream encodePrim(double value, OutStream out) {
            return out.writeDouble(value);
        }

        @Override
        public double decodePrim(InStream in ) {
            return in.readDouble();
        }
    }

    protected final Codec.DoubleCodec<InStream, OutStream, Config> doubleCodec = new DoubleCodec();

    @Override
    public Codec.DoubleCodec<InStream, OutStream, Config> doubleCodec() {
        return doubleCodec;
    }

    protected final Codec<double[], InStream, OutStream, Config> doubleArrayCodec =
            new Codec<double[], InStream, OutStream, Config>() {

        @Override
        public Class<double[]> type() {
            return double[].class;
        }

        @Override
        public OutStream encode(CodecCoreEx<InStream, OutStream, Config> core, double[] value, OutStream out) {
            out.startArray();
            for (double val : value) {
                doubleCodec().encodePrim(val, out);
            }
            return out.endArray();
        }

        @Override
        public double[] decode(CodecCoreEx<InStream, OutStream, Config> core, InStream in) {
            double[] arr = new double[config.defaultArraySize()];
            in.startArray();
            int i = 0;
            while (in.notEOF() && in.currentEventType() != JsonEvent.Type.ARRAY_END) {
                if (i == arr.length) {
                    arr = Arrays.copyOf(arr, config().resizeArray(arr.length));
                }
                arr[i++] = doubleCodec().decodePrim(in);
            }
            in.endArray();
            return Arrays.copyOf(arr, i);
        }
    };

    @Override
    public Codec<double[], InStream, OutStream, Config> doubleArrayCodec() {
        return doubleArrayCodec;
    }

    protected static class StringCodec implements Codec<String, InStream, OutStream, Config> {

        @Override
        public Class<String> type() {
            return String.class;
        }

        @Override
        public OutStream encode(CodecCoreEx<InStream, OutStream, Config> core, String value, OutStream out) {
            return out.writeString(value);
        }

        @Override
        public String decode(CodecCoreEx<InStream, OutStream, Config> core, InStream in) {
            return in.readString();
        }
    }

    protected final Codec<String, InStream, OutStream, Config> stringCodec = new StringCodec();

    @Override
    public Codec<String, InStream, OutStream, Config> stringCodec() {
        return stringCodec;
    }

    @Override
    public <V> Codec<Map<String, V>, InStream, OutStream, Config> createMapCodec(
            Class<Map<String, V>> type,
            Codec<V, InStream, OutStream, Config> valueCodec) {
        return new JsonMapCodecs.StringMapCodec<V>(type, valueCodec);
    }

    @Override
    public <K, V> Codec<Map<K, V>, InStream, OutStream, Config> createMapCodec(
            Class<Map<K, V>> type,
            Codec<K, InStream, OutStream, Config> keyCodec,
            Codec<V, InStream, OutStream, Config> valueCodec) {
        return new JsonMapCodecs.MapCodec<K, V>(type, keyCodec, valueCodec);
    }

    @Override
    public <T> Codec<Collection<T>, InStream, OutStream, Config> createCollCodec(
            Class<Collection<T>> collType,
            Codec<T, InStream, OutStream, Config> elemCodec) {
        return new CollectionCodec<T, InStream, OutStream, Config>(collType, elemCodec) {

            @Override
            public OutStream encode(CodecCoreEx<InStream, OutStream, Config> core, Collection<T> value, OutStream out) {
                out.startArray();
                for (T val : value) {
                    elemCodec.encodeWithCheck(core, val, out);
                }
                return out.endArray();
            }

            @Override
            public Collection<T> decode(CodecCoreEx<InStream, OutStream, Config> core, InStream in) {
                final CollProxy<T> collProxy = getCollectionProxy(core);

                in.startArray();

                while(in.notEOF() && in.currentEventType() != JsonEvent.Type.ARRAY_END) {
                    collProxy.add(elemCodec.decodeWithCheck(core, in));
                }

                in.endArray();

                return collProxy.construct();
            }
        };
    }

    @Override
    public <T> Codec<T[], InStream, OutStream, Config> createObjectArrayCodec(
            Class<T[]> arrType,
            Class<T> elemType,
            Codec<T, InStream, OutStream, Config> elemCodec) {
        return new Codec<T[], InStream, OutStream, Config>() {
            @Override
            public Class<T[]> type() {
                return arrType;
            }

            @Override
            public OutStream encode(CodecCoreEx<InStream, OutStream, Config> core, T[] value, OutStream out) {
                out.startArray();
                for (T val : value) {
                    elemCodec.encodeWithCheck(core, val, out);
                }
                return out.endArray();
            }

            @Override
            public T[] decode(CodecCoreEx<InStream, OutStream, Config> core, InStream in) {
                T[] arr = (T[]) Array.newInstance(elemType, config.defaultArraySize());

                in.startArray();
                int i = 0;
                while (in.notEOF() && in.currentEventType() != JsonEvent.Type.ARRAY_END) {
                    if (i == arr.length) {
                        arr = Arrays.copyOf(arr, config().resizeArray(arr.length));
                    }
                    arr[i++] = elemCodec.decodeWithCheck(core, in);
                }
                in.endArray();

                return Arrays.copyOf(arr, i);
            }
        };
    }

    @Override
    public <T, RA extends ObjectMeta.Builder<T>> Codec<T, InStream, OutStream, Config> createObjectCodec(
            Class<T> type,
            ObjectMeta<T, InStream, OutStream, RA> objMeta) {
        if (Modifier.isFinal(type.getModifiers())) {
            return new FinalObjectCodec<T, RA>(type, objMeta);
        } else {
            return new ObjectCodec<T, RA>(type, objMeta);
        }
    }

    protected class ObjectCodec<T, RA extends ObjectMeta.Builder<T>>
            implements Codec<T, InStream, OutStream, Config> {

        private final Class<T> type;
        private final ObjectMeta<T, InStream, OutStream, RA> objMeta;
        private final Map<String, ObjectMeta.Field<T, InStream, OutStream, RA>> fields;

        private ObjectCodec(
                Class<T> type,
                ObjectMeta<T, InStream, OutStream, RA> objMeta) {
            this.type = type;
            this.objMeta = objMeta;
            this.fields = objMeta.stream()
                    .collect(toLinkedHashMap(
                            ObjectMeta.Field::name,
                            f -> f
                    ));
        }

        @Override
        public Class<T> type() {
            return type;
        }

        @Override
        public OutStream encode(CodecCoreEx<InStream, OutStream, Config> core, T value, OutStream out) {
            out.startObject();

            fields.forEach((name, field) -> {
                out.writeField(field.name());
                field.encodeField(value, out);
            });

            return out.endObject();
        }

        @Override
        public T decode(CodecCoreEx<InStream, OutStream, Config> core, InStream in) {
            in.startObject();

            final Set<String> expNames = fields.keySet();
            final Set<String> actNames = new HashSet<>();
            final RA ra = objMeta.startDecode();

            while (in.notEOF() && in.currentEventType() != JsonEvent.Type.OBJECT_END) {
                final String name = in.readFieldName();
                if (!expNames.contains(name)) {
                    if (config().failOnUnrecognisedFields()) {
                        throw new CodecException(
                                "Field name '" + name + "' unexpected for type " + type +
                                        " at location " + in.location());
                    } else {
                        in.skipNode();
                    }
                } else if (actNames.contains(name)) {
                    throw new CodecException(
                            "Duplicate field name '" + name + "' for type " + type +
                                    " at location " + in.location());
                } else {
                    actNames.add(name);
                    fields.get(name).decodeField(ra, in);
                }
            }

            checkFields(type, expNames, actNames);

            in.endObject();

            return ra.construct();
        }
    }

    protected class FinalObjectCodec<T, RA extends ObjectMeta.Builder<T>>
            extends ObjectCodec<T, RA>
            implements Codec.FinalCodec<T, InStream, OutStream, Config> {

        protected FinalObjectCodec(
                Class<T> type,
                ObjectMeta<T, InStream, OutStream, RA> objMeta) {
            super(type, objMeta);
        }
    }
}
