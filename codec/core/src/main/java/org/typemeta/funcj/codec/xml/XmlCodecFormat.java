package org.typemeta.funcj.codec.xml;

import org.typemeta.funcj.codec.*;
import org.typemeta.funcj.codec.stream.StreamCodecFormat;
import org.typemeta.funcj.codec.utils.CodecException;
import org.typemeta.funcj.codec.xml.XmlTypes.*;
import org.typemeta.funcj.functions.Functions;
import org.typemeta.funcj.tuples.Tuple2;

import java.lang.reflect.*;
import java.util.*;

import static org.typemeta.funcj.codec.utils.StreamUtils.toLinkedHashMap;

/**
 * Encoding via XML streams.
 */
@SuppressWarnings("unchecked")
public class XmlCodecFormat implements StreamCodecFormat<InStream, OutStream, Config> {

    protected final Config config;

    public XmlCodecFormat(Config config) {
        this.config = config;
    }

    public XmlCodecFormat() {
        this(new XmlConfigImpl());
    }

    @Override
    public Config config() {
        return config;
    }

    @Override
    public <T> Tuple2<Boolean, OutStream> encodeNull(T val, OutStream out) {
        if (val == null) {
            out.attribute(config.nullAttrName(), config.nullAttrVal());
            return Tuple2.of(true, out);
        } else {
            return Tuple2.of(false, out);
        }
    }

    @Override
    public boolean decodeNull(InStream in) {
        return in.attributeMap().nameHasValue(config.nullAttrName(), config.nullAttrVal());
    }

    @Override
    public <T> Tuple2<Boolean, OutStream> encodeDynamicType(
            CodecCoreEx<InStream, OutStream, Config> core,
            Codec<T, InStream, OutStream, Config> codec,
            T val,
            OutStream out,
            Functions.F<Class<T>, Codec<T, InStream, OutStream, Config>> getDynCodec) {
        final Class<T> dynType = (Class<T>) val.getClass();
        if (config().dynamicTypeMatch(codec.type(), dynType)) {
            return Tuple2.of(false, out);
        } else if (!config().dynamicTypeTags()) {
            final Codec<T, InStream, OutStream, Config> dynCodec = getDynCodec.apply(dynType);
            dynCodec.encode(core, val, out);
            return Tuple2.of(true, out);
        } else {
            final Codec<T, InStream, OutStream, Config> dynCodec = getDynCodec.apply(dynType);
            out.attribute(config.typeAttrName(), config().classToName(dynType));
            dynCodec.encode(core, val, out);
            return Tuple2.of(true, out);
        }
    }

    @Override
    public <T> T decodeDynamicType(InStream in, Functions.F<String, T> decoder) {
        if (!config().dynamicTypeTags()) {
            return null;
        } else if (in.attributeMap().hasName(config.typeAttrName())) {
            final String typeName = in.attributeMap().getValue(config.typeAttrName());
            return decoder.apply(typeName);
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
            for (boolean val : value) {
                booleanCodec().encodePrim(val, out.startElement(config.entryElemName()));
                out.endElement();
            }
            return out;
        }

        @Override
        public boolean[] decode(CodecCoreEx<InStream, OutStream, Config> core, InStream in) {
            boolean[] arr = new boolean[config.defaultArraySize()];
            int i = 0;
            while (in.hasNext()) {
                if (!in.type().equals(InStream.Type.START_ELEMENT)) {
                    break;
                }

                if (i == arr.length) {
                    arr = Arrays.copyOf(arr, config().resizeArray(arr.length));
                }

                in.startElement(config.entryElemName());
                arr[i++] = booleanCodec().decodePrim(in);
                in.endElement();
            }
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
            for (byte val : value) {
                byteCodec().encodePrim(val, out.startElement(config.entryElemName()));
                out.endElement();
            }
            return out;
        }

        @Override
        public byte[] decode(CodecCoreEx<InStream, OutStream, Config> core, InStream in) {
            byte[] arr = new byte[config.defaultArraySize()];
            int i = 0;
            while (in.hasNext()) {
                if (!in.type().equals(InStream.Type.START_ELEMENT)) {
                    break;
                }

                if (i == arr.length) {
                    arr = Arrays.copyOf(arr, config().resizeArray(arr.length));
                }

                in.startElement(config.entryElemName());
                arr[i++] = byteCodec().decodePrim(in);
                in.endElement();
            }
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
            for (char val : value) {
                charCodec().encodePrim(val, out.startElement(config.entryElemName()));
                out.endElement();
            }
            return out;
        }

        @Override
        public char[] decode(CodecCoreEx<InStream, OutStream, Config> core, InStream in) {
            char[] arr = new char[config.defaultArraySize()];
            int i = 0;
            while (in.hasNext()) {
                if (!in.type().equals(InStream.Type.START_ELEMENT)) {
                    break;
                }

                if (i == arr.length) {
                    arr = Arrays.copyOf(arr, config().resizeArray(arr.length));
                }

                in.startElement(config.entryElemName());
                arr[i++] = charCodec().decodePrim(in);
                in.endElement();
            }
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
            for (short val : value) {
                shortCodec().encodePrim(val, out.startElement(config.entryElemName()));
                out.endElement();
            }
            return out;
        }

        @Override
        public short[] decode(CodecCoreEx<InStream, OutStream, Config> core, InStream in) {
            short[] arr = new short[config.defaultArraySize()];
            int i = 0;
            while (in.hasNext()) {
                if (!in.type().equals(InStream.Type.START_ELEMENT)) {
                    break;
                }

                if (i == arr.length) {
                    arr = Arrays.copyOf(arr, config().resizeArray(arr.length));
                }

                in.startElement(config.entryElemName());
                arr[i++] = shortCodec().decodePrim(in);
                in.endElement();
            }
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
            for (int val : value) {
                intCodec().encodePrim(val, out.startElement(config.entryElemName()));
                out.endElement();
            }
            return out;
        }

        @Override
        public int[] decode(CodecCoreEx<InStream, OutStream, Config> core, InStream in) {
            int[] arr = new int[config.defaultArraySize()];
            int i = 0;
            while (in.hasNext()) {
                if (!in.type().equals(InStream.Type.START_ELEMENT)) {
                    break;
                }

                if (i == arr.length) {
                    arr = Arrays.copyOf(arr, config().resizeArray(arr.length));
                }

                in.startElement(config.entryElemName());
                arr[i++] = intCodec().decodePrim(in);
                in.endElement();
            }
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
            for (long val : value) {
                longCodec().encodePrim(val, out.startElement(config.entryElemName()));
                out.endElement();
            }
            return out;
        }

        @Override
        public long[] decode(CodecCoreEx<InStream, OutStream, Config> core, InStream in) {
            long[] arr = new long[config.defaultArraySize()];
            int i = 0;
            while (in.hasNext()) {
                if (!in.type().equals(InStream.Type.START_ELEMENT)) {
                    break;
                }

                if (i == arr.length) {
                    arr = Arrays.copyOf(arr, config().resizeArray(arr.length));
                }

                in.startElement(config.entryElemName());
                arr[i++] = longCodec().decodePrim(in);
                in.endElement();
            }
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
            for (float val : value) {
                floatCodec().encodePrim(val, out.startElement(config.entryElemName()));
                out.endElement();
            }
            return out;
        }

        @Override
        public float[] decode(CodecCoreEx<InStream, OutStream, Config> core, InStream in) {
            float[] arr = new float[config.defaultArraySize()];
            int i = 0;
            while (in.hasNext()) {
                if (!in.type().equals(InStream.Type.START_ELEMENT)) {
                    break;
                }

                if (i == arr.length) {
                    arr = Arrays.copyOf(arr, config().resizeArray(arr.length));
                }

                in.startElement(config.entryElemName());
                arr[i++] = floatCodec().decodePrim(in);
                in.endElement();
            }
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
            for (double val : value) {
                doubleCodec().encodePrim(val, out.startElement(config.entryElemName()));
                out.endElement();
            }
            return out;
        }

        @Override
        public double[] decode(CodecCoreEx<InStream, OutStream, Config> core, InStream in) {
            double[] arr = new double[config.defaultArraySize()];
            int i = 0;
            while (in.hasNext()) {
                if (!in.type().equals(InStream.Type.START_ELEMENT)) {
                    break;
                }

                if (i == arr.length) {
                    arr = Arrays.copyOf(arr, config().resizeArray(arr.length));
                }

                in.startElement(config.entryElemName());
                arr[i++] = doubleCodec().decodePrim(in);
                in.endElement();
            }
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
        return new XmlMapCodecs.StringMapCodec<V>(type, valueCodec);
    }

    @Override
    public <K, V> Codec<Map<K, V>, InStream, OutStream, Config> createMapCodec(
            Class<Map<K, V>> type,
            Codec<K, InStream, OutStream, Config> keyCodec,
            Codec<V, InStream, OutStream, Config> valueCodec) {
        return new XmlMapCodecs.MapCodec<K, V>(type, keyCodec, valueCodec);
    }

    @Override
    public <T> Codec<Collection<T>, InStream, OutStream, Config> createCollCodec(
            Class<Collection<T>> collType,
            Codec<T, InStream, OutStream, Config> elemCodec) {
        return new CollectionCodec<T, InStream, OutStream, Config>(collType, elemCodec) {

            @Override
            public OutStream encode(CodecCoreEx<InStream, OutStream, Config> core, Collection<T> value, OutStream out) {
                for (T val : value) {
                    elemCodec.encodeWithCheck(core, val, out.startElement(config.entryElemName()));
                    out.endElement();
                }
                return out;
            }

            @Override
            public Collection<T> decode(CodecCoreEx<InStream, OutStream, Config> core, InStream in) {
                final CollProxy<T> collProxy = getCollectionProxy(core);

                while (in.hasNext()) {
                    if (!in.type().equals(InStream.Type.START_ELEMENT)) {
                        break;
                    }

                    in.startElement(config.entryElemName());
                    collProxy.add(elemCodec.decodeWithCheck(core, in));
                    in.endElement();
                }

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
                for (T val : value) {
                    elemCodec.encodeWithCheck(core, val, out.startElement(config.entryElemName()));
                    out.endElement();
                }
                return out;
            }

            @Override
            public T[] decode(CodecCoreEx<InStream, OutStream, Config> core, InStream in) {
                T[] arr = (T[]) Array.newInstance(elemCodec.type(), config.defaultArraySize());
                int i = 0;
                while (in.hasNext()) {
                    if (!in.type().equals(InStream.Type.START_ELEMENT)) {
                        break;
                    }

                    if (i == arr.length) {
                        arr = Arrays.copyOf(arr, config().resizeArray(arr.length));
                    }

                    in.startElement(config.entryElemName());
                    arr[i++] = elemCodec.decodeWithCheck(core, in);
                    in.endElement();
                }
                return Arrays.copyOf(arr, i);
            }
        };
    }

    @Override
    public <T, RA extends ObjectMeta.ResultAccumlator<T>> Codec<T, InStream, OutStream, Config> createObjectCodec(
            Class<T> type,
            ObjectMeta<T, InStream, OutStream, RA> objMeta) {
        if (Modifier.isFinal(type.getModifiers())) {
            return new FinalObjectCodec<T, RA>(type, objMeta);
        } else {
            return new ObjectCodec<T, RA>(type, objMeta);
        }
    }

    protected static class ObjectCodec<T, RA extends ObjectMeta.ResultAccumlator<T>>
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
            fields.forEach((name, field) -> {
                field.encodeField(value, out.startElement(field.name()));
                out.endElement();
            });

            return out;
        }

        @Override
        public T decode(CodecCoreEx<InStream, OutStream, Config> core, InStream in) {
            final Set<String> expKeys = fields.keySet();
            final Set<String> setFields = new HashSet<>();
            final RA ra = objMeta.startDecode();

            while(in.hasNext() && in.type().equals(InStream.Type.START_ELEMENT)) {
                final String name = in.startElement();
                if (!expKeys.contains(name)) {
                    throw new CodecException("Field name '" + name + "' unexpected for type " + type +
                                                     " at location " + in.location());
                } else if (setFields.contains(name)) {
                    throw new CodecException("Duplicate field name '" + name + "' for type " + type +
                                                     " at location " + in.location());
                }

                setFields.add(name);
                fields.get(name).decodeField(ra, in);
                in.endElement();
            }

            if (!setFields.equals(expKeys)) {
                // TODO more informative error message.
                throw new CodecException("Encountered fields differs to expected fields for type " + type +
                                                 " at location " + in.location());
            }

            return ra.construct();
        }
    }

    protected static class FinalObjectCodec<T, RA extends ObjectMeta.ResultAccumlator<T>>
            extends ObjectCodec<T, RA>
            implements Codec.FinalCodec<T, InStream, OutStream, Config> {

        protected FinalObjectCodec(
                Class<T> type,
                ObjectMeta<T, InStream, OutStream, RA> objMeta) {
            super(type, objMeta);
        }
    }
}
