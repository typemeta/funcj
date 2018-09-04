package org.typemeta.funcj.codec.json;

import org.typemeta.funcj.codec.*;
import org.typemeta.funcj.json.model.*;
import org.typemeta.funcj.util.*;

import java.lang.reflect.Array;
import java.util.*;

@SuppressWarnings("unchecked")
public class JsonCodecCoreImpl extends BaseCodecCore<JsValue, JsValue> implements JsonCodecCore {

    public JsonCodecCoreImpl() {
    }

    public String typeFieldName() {
        return "@type";
    }

    public String keyFieldName() {
        return "@key";
    }

    public String valueFieldName() {
        return "@value";
    }

    private final Codec.NullCodec<JsValue, JsValue> nullCodec = new Codec.NullCodec<JsValue, JsValue>() {
        @Override
        public boolean isNull(JsValue enc) {
            return enc.isNull();
        }

        @Override
        public JsValue encode(Object val, JsValue enc) {
            return JSAPI.nul();
        }

        @Override
        public Object decode(JsValue enc) {
            enc.asNull();
            return null;
        }
    };

    public <T> JsValue encode(T val) {
        return encode((Class<T>)val.getClass(), val);
    }

    public <T> JsValue encode(Class<T> type, T val) {
        return super.encode(type, val, null);
    }

    @Override
    public Codec.NullCodec<JsValue, JsValue> nullCodec() {
        return nullCodec;
    }

    protected final Codec.BooleanCodec<JsValue, JsValue> booleanCodec = new Codec.BooleanCodec<JsValue, JsValue>() {

        @Override
        public JsValue encodePrim(boolean val, JsValue out) {
            return JSAPI.bool(val);
        }

        @Override
        public boolean decodePrim(JsValue enc) {
            return enc.asBool().getValue();
        }
    };

    @Override
    public Codec.BooleanCodec<JsValue, JsValue> booleanCodec() {
        return booleanCodec;
    }

    protected final Codec<boolean[], JsValue, JsValue> booleanArrayCodec = new Codec<boolean[], JsValue, JsValue>() {

        @Override
        public JsValue encode(boolean[] vals, JsValue enc) {
            final List<JsValue> nodes = new ArrayList<>(vals.length);
            for (boolean val : vals) {
                nodes.add(booleanCodec().encode(val, enc));
            }
            return JSAPI.arr(nodes);
        }

        @Override
        public boolean[] decode(JsValue enc) {
            final JsArray arrNode = enc.asArray();
            final boolean[] vals = new boolean[arrNode.size()];
            int i = 0;
            for (JsValue node : arrNode) {
                vals[i++] = booleanCodec().decode(node);
            }
            return vals;
        }
    };

    @Override
    public Codec<boolean[], JsValue, JsValue> booleanArrayCodec() {
        return booleanArrayCodec;
    }

    protected final Codec.ByteCodec<JsValue, JsValue> byteCodec = new Codec.ByteCodec<JsValue, JsValue>() {

        @Override
        public JsValue encodePrim(byte val, JsValue out) {
            return JSAPI.num(val);
        }

        @Override
        public byte decodePrim(JsValue enc) {
            return (byte)enc.asNumber().getValue();
        }
    };

    @Override
    public Codec.ByteCodec<JsValue, JsValue> byteCodec() {
        return byteCodec;
    }

    protected final Codec<byte[], JsValue, JsValue> byteArrayCodec = new Codec<byte[], JsValue, JsValue>() {

        @Override
        public JsValue encode(byte[] vals, JsValue enc) {
            final List<JsValue> nodes = new ArrayList<>(vals.length);
            for (byte val : vals) {
                nodes.add(byteCodec().encode(val, enc));
            }
            return JSAPI.arr(nodes);
        }

        @Override
        public byte[] decode(JsValue enc) {
            final JsArray arrNode = enc.asArray();
            final byte[] vals = new byte[arrNode.size()];
            int i = 0;
            for (JsValue node : arrNode) {
                vals[i++] = byteCodec().decode(node);
            }
            return vals;
        }
    };

    @Override
    public Codec<byte[], JsValue, JsValue> byteArrayCodec() {
        return byteArrayCodec;
    }

    protected final Codec.CharCodec<JsValue, JsValue> charCodec = new Codec.CharCodec<JsValue, JsValue>() {

        @Override
        public JsValue encodePrim(char val, JsValue out) {
            return JSAPI.str(String.valueOf(val));
        }

        @Override
        public char decodePrim(JsValue enc) {
            final String s = enc.asString().getValue();
            if (s.length() == 1) {
                return s.charAt(0);
            } else {
                throw new CodecException(
                        "Unexpected String of length " + s.length() + " when decoding a char");
            }
        }
    };

    @Override
    public Codec.CharCodec<JsValue, JsValue> charCodec() {
        return charCodec;
    }

    protected final Codec<char[], JsValue, JsValue> charArrayCodec = new Codec<char[], JsValue, JsValue>() {

        @Override
        public JsValue encode(char[] vals, JsValue enc) {
            final List<JsValue> nodes = new ArrayList<>(vals.length);
            for (char val : vals) {
                nodes.add(charCodec().encode(val, enc));
            }
            return JSAPI.arr(nodes);
        }

        @Override
        public char[] decode(JsValue enc) {
            final JsArray arrNode = enc.asArray();
            final char[] vals = new char[arrNode.size()];
            int i = 0;
            for (JsValue node : arrNode) {
                vals[i++] = charCodec().decode(node);
            }
            return vals;
        }
    };

    @Override
    public Codec<char[], JsValue, JsValue> charArrayCodec() {
        return charArrayCodec;
    }

    protected final Codec.ShortCodec<JsValue, JsValue> shortCodec = new Codec.ShortCodec<JsValue, JsValue>() {

        @Override
        public JsValue encodePrim(short val, JsValue out) {
            return JSAPI.num(val);
        }

        @Override
        public short decodePrim(JsValue enc) {
            return (short)enc.asNumber().getValue();
        }
    };

    @Override
    public Codec.ShortCodec<JsValue, JsValue> shortCodec() {
        return shortCodec;
    }

    protected final Codec<short[], JsValue, JsValue> shortArrayCodec = new Codec<short[], JsValue, JsValue>() {

        @Override
        public JsValue encode(short[] vals, JsValue enc) {
            final List<JsValue> nodes = new ArrayList<>(vals.length);
            for (short val : vals) {
                nodes.add(shortCodec().encode(val, enc));
            }
            return JSAPI.arr(nodes);
        }

        @Override
        public short[] decode(JsValue enc) {
            final JsArray arrNode = enc.asArray();
            final short[] vals = new short[arrNode.size()];
            int i = 0;
            for (JsValue node : arrNode) {
                vals[i++] = shortCodec().decode(node);
            }
            return vals;
        }
    };

    @Override
    public Codec<short[], JsValue, JsValue> shortArrayCodec() {
        return shortArrayCodec;
    }

    protected final Codec.IntCodec<JsValue, JsValue> intCodec = new Codec.IntCodec<JsValue, JsValue>() {

        @Override
        public JsValue encodePrim(int val, JsValue out) {
            return JSAPI.num(val);
        }

        @Override
        public int decodePrim(JsValue enc) {
            return (int)enc.asNumber().getValue();
        }
    };

    @Override
    public Codec.IntCodec<JsValue, JsValue> intCodec() {
        return intCodec;
    }

    protected final Codec<int[], JsValue, JsValue> intArrayCodec = new Codec<int[], JsValue, JsValue>() {

        @Override
        public JsValue encode(int[] vals, JsValue enc) {
            final List<JsValue> nodes = new ArrayList<>(vals.length);
            for (int val : vals) {
                nodes.add(intCodec().encode(val, enc));
            }
            return JSAPI.arr(nodes);
        }

        @Override
        public int[] decode(JsValue enc) {
            final JsArray arrNode = enc.asArray();
            final int[] vals = new int[arrNode.size()];
            int i = 0;
            for (JsValue node : arrNode) {
                vals[i++] = intCodec().decode(node);
            }
            return vals;
        }
    };

    @Override
    public Codec<int[], JsValue, JsValue> intArrayCodec() {
        return intArrayCodec;
    }

    protected final Codec.LongCodec<JsValue, JsValue> longCodec = new Codec.LongCodec<JsValue, JsValue>() {

        @Override
        public JsValue encodePrim(long val, JsValue out) {
            return JSAPI.num(val);
        }

        @Override
        public long decodePrim(JsValue enc) {
            return (long)enc.asNumber().getValue();
        }
    };

    @Override
    public Codec.LongCodec<JsValue, JsValue> longCodec() {
        return longCodec;
    }

    protected final Codec<long[], JsValue, JsValue> longArrayCodec = new Codec<long[], JsValue, JsValue>() {

        @Override
        public JsValue encode(long[] vals, JsValue enc) {
            final List<JsValue> nodes = new ArrayList<>(vals.length);
            for (long val : vals) {
                nodes.add(longCodec().encode(val, enc));
            }
            return JSAPI.arr(nodes);
        }

        @Override
        public long[] decode(JsValue enc) {
            final JsArray arrNode = enc.asArray();
            final long[] vals = new long[arrNode.size()];
            int i = 0;
            for (JsValue node : arrNode) {
                vals[i++] = longCodec().decode(node);
            }
            return vals;
        }
    };

    @Override
    public Codec<long[], JsValue, JsValue> longArrayCodec() {
        return longArrayCodec;
    }

    protected final Codec.FloatCodec<JsValue, JsValue> floatCodec = new Codec.FloatCodec<JsValue, JsValue>() {

        @Override
        public JsValue encodePrim(float val, JsValue out) {
            return JSAPI.num(val);
        }

        @Override
        public float decodePrim(JsValue enc) {
            return (float)enc.asNumber().getValue();
        }
    };

    @Override
    public Codec.FloatCodec<JsValue, JsValue> floatCodec() {
        return floatCodec;
    }

    protected final Codec<float[], JsValue, JsValue> floatArrayCodec = new Codec<float[], JsValue, JsValue>() {

        @Override
        public JsValue encode(float[] vals, JsValue enc) {
            final List<JsValue> nodes = new ArrayList<>(vals.length);
            for (float val : vals) {
                nodes.add(floatCodec().encode(val, enc));
            }
            return JSAPI.arr(nodes);
        }

        @Override
        public float[] decode(JsValue enc) {
            final JsArray arrNode = enc.asArray();
            final float[] vals = new float[arrNode.size()];
            int i = 0;
            for (JsValue node : arrNode) {
                vals[i++] = floatCodec().decode(node);
            }
            return vals;
        }
    };

    @Override
    public Codec<float[], JsValue, JsValue> floatArrayCodec() {
        return floatArrayCodec;
    }

    protected final Codec.DoubleCodec<JsValue, JsValue> doubleCodec = new Codec.DoubleCodec<JsValue, JsValue>() {

        @Override
        public JsValue encodePrim(double val, JsValue out) {
            return JSAPI.num(val);
        }

        @Override
        public double decodePrim(JsValue enc) {
            return enc.asNumber().getValue();
        }
    };

    @Override
    public Codec.DoubleCodec<JsValue, JsValue> doubleCodec() {
        return doubleCodec;
    }

    protected final Codec<double[], JsValue, JsValue> doubleArrayCodec = new Codec<double[], JsValue, JsValue>() {

        @Override
        public JsValue encode(double[] vals, JsValue enc) {
            final List<JsValue> nodes = new ArrayList<>(vals.length);
            for (double val : vals) {
                nodes.add(doubleCodec().encode(val, enc));
            }
            return JSAPI.arr(nodes);
        }

        @Override
        public double[] decode(JsValue enc) {
            final JsArray arrNode = enc.asArray();
            final double[] vals = new double[arrNode.size()];
            int i = 0;
            for (JsValue node : arrNode) {
                vals[i++] = doubleCodec().decode(node);
            }
            return vals;
        }
    };

    @Override
    public Codec<double[], JsValue, JsValue> doubleArrayCodec() {
        return doubleArrayCodec;
    }

    protected final Codec<String, JsValue, JsValue> stringCodec = new Codec<String, JsValue, JsValue>() {
        @Override
        public JsValue encode(String val, JsValue enc) {
            return JSAPI.str(val);
        }

        @Override
        public String decode(JsValue enc) {
            return enc.asString().getValue();
        }
    };

    @Override
    public Codec<String, JsValue, JsValue> stringCodec() {
        return stringCodec;
    }

    @Override
    public <EM extends Enum<EM>> Codec<EM, JsValue, JsValue> enumCodec(Class<? super EM> enumType) {
        return new Codec<EM, JsValue, JsValue>() {
            @Override
            public JsValue encode(EM val, JsValue enc) {
                return JSAPI.str(val.name());
            }

            @Override
            public EM decode(Class<EM> dynType, JsValue enc) {
                final Class<EM> type = dynType != null ? dynType : (Class<EM>)enumType;
                return EM.valueOf(type, enc.asString().getValue());
            }
        };
    }

    @Override
    public <V> Codec<Map<String, V>, JsValue, JsValue> mapCodec(Codec<V, JsValue, JsValue> valueCodec) {
        return new JsonMapCodecs.StringMapCodec<V>(this, valueCodec);
    }

    @Override
    public <K, V> Codec<Map<K, V>, JsValue, JsValue> mapCodec(Codec<K, JsValue, JsValue> keyCodec, Codec<V, JsValue, JsValue> valueCodec) {
        return new JsonMapCodecs.MapCodec<K, V>(this, keyCodec, valueCodec);
    }

    @Override
    public <T> Codec<Collection<T>, JsValue, JsValue> collCodec(Class<T> elemType, Codec<T, JsValue, JsValue> elemCodec) {
        return new Codec<Collection<T>, JsValue, JsValue>() {
            @Override
            public JsValue encode(Collection<T> vals, JsValue enc) {
                final List<JsValue> nodes = new ArrayList<>(vals.size());
                for (T val : vals) {
                    nodes.add(elemCodec.encode(val, enc));
                }
                return JSAPI.arr(nodes);
            }

            @Override
            public Collection<T> decode(Class<Collection<T>> dynType, JsValue enc) {
                final Class<T> dynElemType = (Class<T>)dynType.getComponentType();
                final JsArray arrNode = enc.asArray();
                final Collection<T> vals = getTypeConstructor(dynType).construct();

                for (JsValue node : arrNode) {
                    vals.add(elemCodec.decode(dynElemType, node));
                }

                return vals;

            }
        };
    }

    @Override
    public <T> Codec<T[], JsValue, JsValue> objectArrayCodec(Class<T> elemType, Codec<T, JsValue, JsValue> elemCodec) {
        return new Codec<T[], JsValue, JsValue>() {
            @Override
            public JsValue encode(T[] vals, JsValue enc) {
                final List<JsValue> nodes = new ArrayList<>(vals.length);
                for (T val : vals) {
                    nodes.add(elemCodec.encode(val, enc));
                }
                return JSAPI.arr(nodes);
            }

            @Override
            public T[] decode(Class<T[]> dynType, JsValue enc) {
                final Class<T> dynElemType = (Class<T>)dynType.getComponentType();
                final JsArray arrNode = enc.asArray();
                final T[] vals = (T[]) Array.newInstance(elemType, arrNode.size());
                int i = 0;
                for (JsValue node : arrNode) {
                    vals[i++] = elemCodec.decode(dynElemType, node);
                }
                return vals;
            }
        };
    }

    @Override
    public <T> Codec<T, JsValue, JsValue> dynamicCodec(Class<T> stcType) {
        return new Codec<T, JsValue, JsValue>() {
            @Override
            public JsValue encode(T val, JsValue enc) {
                final Class<? extends T> dynType = (Class<? extends T>)val.getClass();
                if (dynType.equals(stcType)) {
                    return JsonCodecCoreImpl.this.getNullUnsafeCodecDyn(stcType).encode(val, enc);
                } else {
                    return JSAPI.obj(
                            JSAPI.field(
                                    typeFieldName(),
                                    JSAPI.str(classToName(dynType))),
                            JSAPI.field(
                                    valueFieldName(),
                                    encode2(JsonCodecCoreImpl.this.getNullUnsafeCodecDyn(dynType), val, enc))
                    );
                }
            }

            protected <S extends T> JsValue encode2(Codec<S, JsValue, JsValue> codec, T val, JsValue enc) {
                return codec.encode((S)val, enc);
            }

            @Override
            public T decode(JsValue enc) {
                if (enc.isObject()) {
                    final JsObject objNode = enc.asObject();
                    final String typeFieldName = typeFieldName();
                    final String valueFieldName = valueFieldName();
                    if (objNode.size() == 2 &&
                            objNode.containsName(typeFieldName) &&
                            objNode.containsName(valueFieldName)) {
                        final JsValue typeNode = objNode.get(typeFieldName());
                        final JsValue valueNode = objNode.get(valueFieldName());
                        return decode2(valueNode, nameToClass(typeNode.asString().getValue()));
                    }
                }

                final Codec<T, JsValue, JsValue> codec = JsonCodecCoreImpl.this.getNullUnsafeCodecDyn(stcType);
                return codec.decode(stcType, enc);
            }

            protected <S extends T> S decode2(JsValue in, Class<S> dynType) {
                final Codec<S, JsValue, JsValue> codec = JsonCodecCoreImpl.this.getNullUnsafeCodecDyn(dynType);
                return codec.decode(dynType, in);
            }
        };
    }

    @Override
    public <T> Codec<T, JsValue, JsValue> dynamicCodec(Codec<T, JsValue, JsValue> codec, Class<T> stcType) {
        return new Codec<T, JsValue, JsValue>() {
            @Override
            public JsValue encode(T val, JsValue enc) {
                final Class<? extends T> dynType = (Class<? extends T>)val.getClass();
                if (dynType.equals(stcType)) {
                    return codec.encode(val, enc);
                } else {
                    return JSAPI.obj(
                            JSAPI.field(typeFieldName(), JSAPI.str(classToName(dynType))),
                            JSAPI.field(valueFieldName(), codec.encode(val, enc))
                    );
                }
            }

            @Override
            public T decode(JsValue enc) {
                if (enc.isObject()) {
                    final JsObject objNode = enc.asObject();
                    final String typeFieldName = typeFieldName();
                    final String valueFieldName = valueFieldName();
                    if (objNode.size() == 2 &&
                            objNode.containsName(typeFieldName) &&
                            objNode.containsName(valueFieldName)) {
                        final JsValue typeNode = objNode.get(typeFieldName());
                        final Class<?> dynType = nameToClass(typeNode.asString().getValue());
                        final JsValue valueNode = objNode.get(valueFieldName());
                        return codec.decode((Class<T>) dynType, valueNode);
                    }
                }

                return codec.decode(stcType, enc);
            }
        };
    }

    @Override
    public <T, RA extends ObjectMeta.ResultAccumlator<T>> Codec<T, JsValue, JsValue> createObjectCodec(
            ObjectMeta<T, JsValue, JsValue, RA> objMeta) {
        return new Codec<T, JsValue, JsValue>() {
            @Override
            public JsValue encode(T val, JsValue enc) {
                final List<JsObject.Field> fields =
                        FunctorsGenEx.map(
                                field -> JSAPI.field(field.name(), field.encodeField(val, enc)),
                                objMeta
                        );
                return JSAPI.obj(fields);
            }

            @Override
            public T decode(Class<T> dynType, JsValue enc) {
                final JsObject objNode = enc.asObject();
                return FoldsGenEx.foldLeft(
                        (acc, field) -> field.decodeField(acc, objNode.get(field.name())),
                        objMeta.startDecode(dynType),
                        objMeta
                ).construct();
            }
        };
    }
}
