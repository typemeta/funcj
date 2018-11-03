package org.typemeta.funcj.codec.xml;

import org.typemeta.funcj.codec.*;
import org.typemeta.funcj.codec.xml.io.XmlIO.*;
import org.typemeta.funcj.functions.Functions;

import java.lang.reflect.*;
import java.util.*;

import static org.typemeta.funcj.codec.utils.StreamUtils.toLinkedHashMap;

@SuppressWarnings("unchecked")
public class XmlCodecCoreImpl extends BaseCodecCore<Input, Output> implements XmlCodecCore {

    protected final XmlCodecConfig config;

    public XmlCodecCoreImpl(XmlCodecConfig config) {
        this.config = config;
    }

    public XmlCodecCoreImpl() {
        this(new XmlCodecConfigImpl());
    }

    @Override
    public XmlCodecConfig config() {
        return config;
    }

    @Override
    public <T> boolean encodeNull(T val, Output out) {
        if (val == null) {
            out.attribute(config.nullAttrName(), config.nullAttrVal());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean decodeNull(Input in) {
        return in.attributeMap().nameHasValue(config.nullAttrName(), config.nullAttrVal());
    }

    @Override
    public <T> boolean encodeDynamicType(
            Codec<T, Input, Output> codec,
            T val,
            Output out,
            Functions.F<Class<T>, Codec<T, Input, Output>> getDynCodec) {
        final Class<T> dynType = (Class<T>) val.getClass();
        if (dynType.equals(codec.type())) {
            return false;
        } else {
            final Codec<T, Input, Output> dynCodec = getDynCodec.apply(dynType);
            out.attribute(config.typeAttrName(), config().classToName(dynType));
            dynCodec.encode(val, out);
            return true;
        }
    }

    @Override
    public <T> T decodeDynamicType(Input in, Functions.F<String, T> decoder) {
        if (in.attributeMap().hasName(config.typeAttrName())) {
            final String typeName = in.attributeMap().getValue(config.typeAttrName());
            return decoder.apply(typeName);
        } else {
            return null;
        }
    }

    @Override
    public <T> T decodeDynamicType(Input in) {
        return decodeDynamicType(in, name -> getCodec(this.config().<T>nameToClass(name)).decode(in));
    }

    protected final Codec.BooleanCodec<Input, Output> booleanCodec =
            new Codec.BooleanCodec<Input, Output>() {

        @Override
        public CodecCoreInternal<Input, Output> core() {
            return XmlCodecCoreImpl.this;
        }

        @Override
        public Output encodePrim(boolean val, Output out) {
            return out.writeBoolean(val);
        }

        @Override
        public boolean decodePrim(Input in) {
            return in.readBoolean();
        }
    };

    @Override
    public Codec.BooleanCodec<Input, Output> booleanCodec() {
        return booleanCodec;
    }

    protected final Codec<boolean[], Input, Output> booleanArrayCodec =
            new Codec.Base<boolean[], Input, Output>(this) {

        @Override
        public Class<boolean[]> type() {
            return boolean[].class;
        }

        @Override
        public Output encode(boolean[] value, Output out) {
            for (boolean val : value) {
                booleanCodec().encode(val, out.startElement(config.entryElemName()));
                out.endElement();
            }
            return out;
        }

        @Override
        public boolean[] decode(Input in) {
            boolean[] arr = new boolean[config.defaultArraySize()];
            int i = 0;
            while (in.hasNext()) {
                if (!in.type().equals(Input.Type.START_ELEMENT)) {
                    break;
                }

                if (i == arr.length) {
                    arr = Arrays.copyOf(arr, arr.length * 2);
                }

                in.startElement(config.entryElemName());
                arr[i++] = booleanCodec().decode(in);
                in.endElement();
            }
            return Arrays.copyOf(arr, i);
        }
    };

    @Override
    public Codec<boolean[], Input, Output> booleanArrayCodec() {
        return booleanArrayCodec;
    }

    protected final Codec.ByteCodec<Input, Output> byteCodec =
            new Codec.ByteCodec<Input, Output>() {

        @Override
        public CodecCoreInternal<Input, Output> core() {
            return XmlCodecCoreImpl.this;
        }

        @Override
        public Output encodePrim(byte val, Output out) {
            return out.writeByte(val);
        }

        @Override
        public byte decodePrim(Input in) {
            return in.readByte();
        }
    };

    @Override
    public Codec.ByteCodec<Input, Output> byteCodec() {
        return byteCodec;
    }

    protected final Codec<byte[], Input, Output> byteArrayCodec =
            new Codec.Base<byte[], Input, Output>(this) {

        @Override
        public Class<byte[]> type() {
            return byte[].class;
        }

        @Override
        public Output encode(byte[] value, Output out) {
            for (byte val : value) {
                byteCodec().encode(val, out.startElement(config.entryElemName()));
                out.endElement();
            }
            return out;
        }

        @Override
        public byte[] decode(Input in) {
            byte[] arr = new byte[config.defaultArraySize()];
            int i = 0;
            while (in.hasNext()) {
                if (!in.type().equals(Input.Type.START_ELEMENT)) {
                    break;
                }

                if (i == arr.length) {
                    arr = Arrays.copyOf(arr, arr.length * 2);
                }

                in.startElement(config.entryElemName());
                arr[i++] = byteCodec().decode(in);
                in.endElement();
            }
            return Arrays.copyOf(arr, i);
        }
    };

    @Override
    public Codec<byte[], Input, Output> byteArrayCodec() {
        return byteArrayCodec;
    }

    protected final Codec.CharCodec<Input, Output> charCodec =
            new Codec.CharCodec<Input, Output>() {

        @Override
        public CodecCoreInternal<Input, Output> core() {
            return XmlCodecCoreImpl.this;
        }

        @Override
        public Output encodePrim(char val, Output out) {
            return out.writeChar(val);
        }

        @Override
        public char decodePrim(Input in ) {
            return in.readChar();
        }
    };

    @Override
    public Codec.CharCodec<Input, Output> charCodec() {
        return charCodec;
    }

    protected final Codec<char[], Input, Output> charArrayCodec =
            new Codec.Base<char[], Input, Output>(this) {

        @Override
        public Class<char[]> type() {
            return char[].class;
        }

        @Override
        public Output encode(char[] value, Output out) {
            for (char val : value) {
                charCodec().encode(val, out.startElement(config.entryElemName()));
                out.endElement();
            }
            return out;
        }

        @Override
        public char[] decode(Input in) {
            char[] arr = new char[config.defaultArraySize()];
            int i = 0;
            while (in.hasNext()) {
                if (!in.type().equals(Input.Type.START_ELEMENT)) {
                    break;
                }

                if (i == arr.length) {
                    arr = Arrays.copyOf(arr, arr.length * 2);
                }

                in.startElement(config.entryElemName());
                arr[i++] = charCodec().decode(in);
                in.endElement();
            }
            return Arrays.copyOf(arr, i);
        }
    };

    @Override
    public Codec<char[], Input, Output> charArrayCodec() {
        return charArrayCodec;
    }

    protected final Codec.ShortCodec<Input, Output> shortCodec =
            new Codec.ShortCodec<Input, Output>() {

        @Override
        public CodecCoreInternal<Input, Output> core() {
            return XmlCodecCoreImpl.this;
        }

        @Override
        public Output encodePrim(short val, Output out) {
            return out.writeShort(val);
        }

        @Override
        public short decodePrim(Input in ) {
            return in.readShort();
        }
    };

    @Override
    public Codec.ShortCodec<Input, Output> shortCodec() {
        return shortCodec;
    }

    protected final Codec<short[], Input, Output> shortArrayCodec =
            new Codec.Base<short[], Input, Output>(this) {

        @Override
        public Class<short[]> type() {
            return short[].class;
        }

        @Override
        public Output encode(short[] value, Output out) {
            for (short val : value) {
                shortCodec().encode(val, out.startElement(config.entryElemName()));
                out.endElement();
            }
            return out;
        }

        @Override
        public short[] decode(Input in) {
            short[] arr = new short[config.defaultArraySize()];
            int i = 0;
            while (in.hasNext()) {
                if (!in.type().equals(Input.Type.START_ELEMENT)) {
                    break;
                }

                if (i == arr.length) {
                    arr = Arrays.copyOf(arr, arr.length * 2);
                }

                in.startElement(config.entryElemName());
                arr[i++] = shortCodec().decode(in);
                in.endElement();
            }
            return Arrays.copyOf(arr, i);
        }
    };

    @Override
    public Codec<short[], Input, Output> shortArrayCodec() {
        return shortArrayCodec;
    }

    protected final Codec.IntCodec<Input, Output> intCodec =
            new Codec.IntCodec<Input, Output>() {

        @Override
        public CodecCoreInternal<Input, Output> core() {
            return XmlCodecCoreImpl.this;
        }

        @Override
        public Output encodePrim(int val, Output out) {
            return out.writeInt(val);
        }

        @Override
        public int decodePrim(Input in ) {
            return in.readInt();
        }
    };

    @Override
    public Codec.IntCodec<Input, Output> intCodec() {
        return intCodec;
    }

    protected final Codec<int[], Input, Output> intArrayCodec =
            new Codec.Base<int[], Input, Output>(this) {

        @Override
        public Class<int[]> type() {
            return int[].class;
        }

        @Override
        public Output encode(int[] value, Output out) {
            for (int val : value) {
                intCodec().encode(val, out.startElement(config.entryElemName()));
                out.endElement();
            }
            return out;
        }

        @Override
        public int[] decode(Input in) {
            int[] arr = new int[config.defaultArraySize()];
            int i = 0;
            while (in.hasNext()) {
                if (!in.type().equals(Input.Type.START_ELEMENT)) {
                    break;
                }

                if (i == arr.length) {
                    arr = Arrays.copyOf(arr, arr.length * 2);
                }

                in.startElement(config.entryElemName());
                arr[i++] = intCodec().decode(in);
                in.endElement();
            }
            return Arrays.copyOf(arr, i);
        }
    };

    @Override
    public Codec<int[], Input, Output> intArrayCodec() {
        return intArrayCodec;
    }

    protected final Codec.LongCodec<Input, Output> longCodec =
            new Codec.LongCodec<Input, Output>() {

        @Override
        public CodecCoreInternal<Input, Output> core() {
            return XmlCodecCoreImpl.this;
        }

        @Override
        public Output encodePrim(long val, Output out) {
            return out.writeLong(val);
        }

        @Override
        public long decodePrim(Input in) {
            return in.readLong();
        }
    };

    @Override
    public Codec.LongCodec<Input, Output> longCodec() {
        return longCodec;
    }

    protected final Codec<long[], Input, Output> longArrayCodec =
            new Codec.Base<long[], Input, Output>(this) {

        @Override
        public Class<long[]> type() {
            return long[].class;
        }

        @Override
        public Output encode(long[] value, Output out) {
            for (long val : value) {
                longCodec().encode(val, out.startElement(config.entryElemName()));
                out.endElement();
            }
            return out;
        }

        @Override
        public long[] decode(Input in) {
            long[] arr = new long[config.defaultArraySize()];
            int i = 0;
            while (in.hasNext()) {
                if (!in.type().equals(Input.Type.START_ELEMENT)) {
                    break;
                }

                if (i == arr.length) {
                    arr = Arrays.copyOf(arr, arr.length * 2);
                }

                in.startElement(config.entryElemName());
                arr[i++] = longCodec().decode(in);
                in.endElement();
            }
            return Arrays.copyOf(arr, i);
        }
    };

    @Override
    public Codec<long[], Input, Output> longArrayCodec() {
        return longArrayCodec;
    }

    protected final Codec.FloatCodec<Input, Output> floatCodec =
            new Codec.FloatCodec<Input, Output>() {

        @Override
        public CodecCoreInternal<Input, Output> core() {
            return XmlCodecCoreImpl.this;
        }

        @Override
        public Output encodePrim(float val, Output out) {
            return out.writeFloat(val);
        }

        @Override
        public float decodePrim(Input in ) {
            return in.readFloat();
        }
    };

    @Override
    public Codec.FloatCodec<Input, Output> floatCodec() {
        return floatCodec;
    }

    protected final Codec<float[], Input, Output> floatArrayCodec =
            new Codec.Base<float[], Input, Output>(this) {

        @Override
        public Class<float[]> type() {
            return float[].class;
        }

        @Override
        public Output encode(float[] value, Output out) {
            for (float val : value) {
                floatCodec().encode(val, out.startElement(config.entryElemName()));
                out.endElement();
            }
            return out;
        }

        @Override
        public float[] decode(Input in) {
            float[] arr = new float[config.defaultArraySize()];
            int i = 0;
            while (in.hasNext()) {
                if (!in.type().equals(Input.Type.START_ELEMENT)) {
                    break;
                }

                if (i == arr.length) {
                    arr = Arrays.copyOf(arr, arr.length * 2);
                }

                in.startElement(config.entryElemName());
                arr[i++] = floatCodec().decode(in);
                in.endElement();
            }
            return Arrays.copyOf(arr, i);
        }
    };

    @Override
    public Codec<float[], Input, Output> floatArrayCodec() {
        return floatArrayCodec;
    }

    protected final Codec.DoubleCodec<Input, Output> doubleCodec =
            new Codec.DoubleCodec<Input, Output>() {

        @Override
        public CodecCoreInternal<Input, Output> core() {
            return XmlCodecCoreImpl.this;
        }

        @Override
        public Output encodePrim(double value, Output out) {
            return out.writeDouble(value);
        }

        @Override
        public double decodePrim(Input in ) {
            return in.readDouble();
        }
    };

    @Override
    public Codec.DoubleCodec<Input, Output> doubleCodec() {
        return doubleCodec;
    }

    protected final Codec<double[], Input, Output> doubleArrayCodec =
            new Codec.Base<double[], Input, Output>(this) {

        @Override
        public Class<double[]> type() {
            return double[].class;
        }

        @Override
        public Output encode(double[] value, Output out) {
            for (double val : value) {
                doubleCodec().encode(val, out.startElement(config.entryElemName()));
                out.endElement();
            }
            return out;
        }

        @Override
        public double[] decode(Input in) {
            double[] arr = new double[config.defaultArraySize()];
            int i = 0;
            while (in.hasNext()) {
                if (!in.type().equals(Input.Type.START_ELEMENT)) {
                    break;
                }

                if (i == arr.length) {
                    arr = Arrays.copyOf(arr, arr.length * 2);
                }

                in.startElement(config.entryElemName());
                arr[i++] = doubleCodec().decode(in);
                in.endElement();
            }
            return Arrays.copyOf(arr, i);
        }
    };

    @Override
    public Codec<double[], Input, Output> doubleArrayCodec() {
        return doubleArrayCodec;
    }

    protected final Codec<String, Input, Output> stringCodec =
            new Codec.Base<String, Input, Output>(this) {

        @Override
        public Class<String> type() {
            return String.class;
        }

        @Override
        public Output encode(String value, Output out) {
            return out.writeString(value);
        }

        @Override
        public String decode(Input in) {
            return in.readString();
        }
    };

    @Override
    public Codec<String, Input, Output> stringCodec() {
        return stringCodec;
    }

    @Override
    public <EM extends Enum<EM>> Codec<EM, Input, Output> enumCodec(Class<EM> enumType) {
        return new Codec.Base<EM, Input, Output>(this) {
            @Override
            public Class<EM> type() {
                return enumType;
            }

            @Override
            public Output encode(EM value, Output out) {
                return out.writeString(value.name());
            }

            @Override
            public EM decode(Input in) {
                return EM.valueOf(type(), in.readString());
            }
        };
    }

    @Override
    public <V> Codec<Map<String, V>, Input, Output> createMapCodec(
            Class<Map<String, V>> type,
            Codec<V, Input, Output> valueCodec) {
        return new XmlMapCodecs.StringMapCodec<V>(this, type, valueCodec);
    }

    @Override
    public <K, V> Codec<Map<K, V>, Input, Output> createMapCodec(
            Class<Map<K, V>> type,
            Codec<K, Input, Output> keyCodec,
            Codec<V, Input, Output> valueCodec) {
        return new XmlMapCodecs.MapCodec<K, V>(this, type, keyCodec, valueCodec);
    }

    @Override
    public <T> Codec<Collection<T>, Input, Output> createCollCodec(
            Class<Collection<T>> collType,
            Codec<T, Input, Output> elemCodec) {
        return new CollectionCodec<T, Input, Output>(
            XmlCodecCoreImpl.this,
                elemCodec) {
            @Override
            public Class<Collection<T>> type() {
                return collType;
            }

            @Override
            public Output encode(Collection<T> value, Output out) {
                for (T val : value) {
                    elemCodec.encodeWithCheck(val, out.startElement(config.entryElemName()));
                    out.endElement();
                }
                return out;
            }

            @Override
            public Collection<T> decode(Input in) {
                final Collection<T> vals = getTypeConstructor(collType).construct();

                while (in.hasNext()) {
                    if (!in.type().equals(Input.Type.START_ELEMENT)) {
                        break;
                    }

                    in.startElement(config.entryElemName());
                    vals.add(elemCodec.decodeWithCheck(in));
                    in.endElement();
                }

                return vals;
            }
        };
    }

    @Override
    public <T> Codec<T[], Input, Output> createObjectArrayCodec(
            Class<T[]> arrType,
            Class<T> elemType,
            Codec<T, Input, Output> elemCodec) {
        return new Codec<T[], Input, Output>() {
            @Override
            public CodecCoreInternal<Input, Output> core() {
                return XmlCodecCoreImpl.this;
            }

            @Override
            public Class<T[]> type() {
                return arrType;
            }

            @Override
            public Output encode(T[] value, Output out) {
                for (T val : value) {
                    elemCodec.encodeWithCheck(val, out.startElement(config.entryElemName()));
                    out.endElement();
                }
                return out;
            }

            @Override
            public T[] decode(Input in) {
                T[] arr = (T[]) Array.newInstance(elemCodec.type(), config.defaultArraySize());
                int i = 0;
                while (in.hasNext()) {
                    if (!in.type().equals(Input.Type.START_ELEMENT)) {
                        break;
                    }

                    if (i == arr.length) {
                        arr = Arrays.copyOf(arr, arr.length * 2);
                    }

                    in.startElement(config.entryElemName());
                    arr[i++] = elemCodec.decodeWithCheck(in);
                    in.endElement();
                }
                return Arrays.copyOf(arr, i);
            }
        };
    }

    @Override
    public <T, RA extends ObjectMeta.ResultAccumlator<T>> Codec<T, Input, Output> createObjectCodec(
            Class<T> type,
            ObjectMeta<T, Input, Output, RA> objMeta) {
        if (Modifier.isFinal(type.getModifiers())) {
            return new FinalObjectCodec<T, RA>(this, type, objMeta);
        } else {
            return new ObjectCodec<T, RA>(this, type, objMeta);
        }
    }

    private static class ObjectCodec<T, RA extends ObjectMeta.ResultAccumlator<T>>
            extends Codec.Base<T, Input, Output> {

        private final Class<T> type;
        private final ObjectMeta<T, Input, Output, RA> objMeta;
        private final Map<String, ObjectMeta.Field<T, Input, Output, RA>> fields;

        private ObjectCodec(
                CodecCoreInternal<Input, Output> core,
                Class<T> type,
                ObjectMeta<T, Input, Output, RA> objMeta) {
            super(core);
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
        public Output encode(T value, Output out) {
            fields.forEach((name, field) -> {
                field.encodeField(value, out.startElement(field.name()));
                out.endElement();
            });

            return out;
        }

        @Override
        public T decode(Input in) {
            final Set<String> expKeys = fields.keySet();
            final Set<String> setFields = new HashSet<>();
            final RA ra = objMeta.startDecode();

            while(in.hasNext() && in.type().equals(Input.Type.START_ELEMENT)) {
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

    private static class FinalObjectCodec<T, RA extends ObjectMeta.ResultAccumlator<T>>
            extends ObjectCodec<T, RA>
            implements Codec.FinalCodec<T, Input, Output> {

        private FinalObjectCodec(
                CodecCoreInternal<Input, Output> core,
                Class<T> type,
                ObjectMeta<T, Input, Output, RA> objMeta) {
            super(core, type, objMeta);
        }
    }
}
