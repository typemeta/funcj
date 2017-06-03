package org.funcj.codec.json;

import org.funcj.codec.*;
import org.funcj.control.Exceptions;
import org.funcj.json.*;

import java.lang.reflect.Array;
import java.util.*;

public class JsonCodecCore extends CodecCore<JSValue> {

    public String typeFieldName() {
        return "@type";
    }

    public String keyFieldName() {
        return "@key";
    }

    public String valueFieldName() {
        return "@value";
    }

    private final Codec.NullCodec<JSValue> nullCodec = new Codec.NullCodec<JSValue>() {
        @Override
        public boolean isNull(JSValue in) {
            return in.isNull();
        }

        @Override
        public JSValue encode(Object val, JSValue out) {
            return Json.nul();
        }

        @Override
        public Object decode(JSValue in) {
            in.asNull();
            return null;
        }
    };

    @Override
    public <T> JSValue encode(Class<T> type, T val) {
        return super.encode(type, val, null);
    }

    @Override
    public Codec.NullCodec<JSValue> nullCodec() {
        return nullCodec;
    }

    private final Codec.BooleanCodec<JSValue> booleanCodec = new Codec.BooleanCodec<JSValue>() {

        @Override
        public JSValue encodePrim(boolean val, JSValue out) {
            return Json.bool(val);
        }

        @Override
        public boolean decodePrim(JSValue in) {
            return in.asBool().getValue();
        }
    };

    @Override
    public Codec.BooleanCodec<JSValue> booleanCodec() {
        return booleanCodec;
    }

    private final Codec<boolean[], JSValue> booleanArrayCodec = new Codec<boolean[], JSValue>() {

        @Override
        public JSValue encode(boolean[] vals, JSValue out) {
            final List<JSValue> nodes = new ArrayList<>(vals.length);
            for (boolean val : vals) {
                nodes.add(booleanCodec().encode(val, out));
            }
            return Json.array(nodes);
        }

        @Override
        public boolean[] decode(JSValue in) {
            final JSArray arrNode = in.asArray();
            final boolean[] vals = new boolean[arrNode.size()];
            int i = 0;
            for (JSValue node : arrNode) {
                vals[i++] = booleanCodec().decode(boolean.class, node);
            }
            return vals;
        }
    };

    @Override
    public Codec<boolean[], JSValue> booleanArrayCodec() {
        return booleanArrayCodec;
    }

    private final Codec.ByteCodec<JSValue> byteCodec = new Codec.ByteCodec<JSValue>() {

        @Override
        public JSValue encodePrim(byte val, JSValue out) {
            return Json.number(val);
        }

        @Override
        public byte decodePrim(JSValue in) {
            return (byte)in.asNumber().getValue();
        }
    };

    @Override
    public Codec.ByteCodec<JSValue> byteCodec() {
        return byteCodec;
    }

    private final Codec<byte[], JSValue> byteArrayCodec = new Codec<byte[], JSValue>() {

        @Override
        public JSValue encode(byte[] vals, JSValue out) {
            final List<JSValue> nodes = new ArrayList<>(vals.length);
            for (byte val : vals) {
                nodes.add(byteCodec().encode(val, out));
            }
            return Json.array(nodes);
        }

        @Override
        public byte[] decode(JSValue in) {
            final JSArray arrNode = in.asArray();
            final byte[] vals = new byte[arrNode.size()];
            int i = 0;
            for (JSValue node : arrNode) {
                vals[i++] = byteCodec().decode(byte.class, node);
            }
            return vals;
        }
    };

    @Override
    public Codec<byte[], JSValue> byteArrayCodec() {
        return byteArrayCodec;
    }

    private final Codec.CharCodec<JSValue> charCodec = new Codec.CharCodec<JSValue>() {

        @Override
        public JSValue encodePrim(char val, JSValue out) {
            return Json.string(String.valueOf(val));
        }

        @Override
        public char decodePrim(JSValue in) {
            return in.asString().getValue().charAt(0);
        }
    };

    @Override
    public Codec.CharCodec<JSValue> charCodec() {
        return charCodec;
    }

    private final Codec<char[], JSValue> charArrayCodec = new Codec<char[], JSValue>() {

        @Override
        public JSValue encode(char[] vals, JSValue out) {
            final List<JSValue> nodes = new ArrayList<>(vals.length);
            for (char val : vals) {
                nodes.add(charCodec().encode(val, out));
            }
            return Json.array(nodes);
        }

        @Override
        public char[] decode(JSValue in) {
            final JSArray arrNode = in.asArray();
            final char[] vals = new char[arrNode.size()];
            int i = 0;
            for (JSValue node : arrNode) {
                vals[i++] = charCodec().decode(char.class, node);
            }
            return vals;
        }
    };

    @Override
    public Codec<char[], JSValue> charArrayCodec() {
        return charArrayCodec;
    }

    private final Codec.ShortCodec<JSValue> shortCodec = new Codec.ShortCodec<JSValue>() {

        @Override
        public JSValue encodePrim(short val, JSValue out) {
            return Json.number(val);
        }

        @Override
        public short decodePrim(JSValue in) {
            return (short)in.asNumber().getValue();
        }
    };

    @Override
    public Codec.ShortCodec<JSValue> shortCodec() {
        return shortCodec;
    }

    private final Codec<short[], JSValue> shortArrayCodec = new Codec<short[], JSValue>() {

        @Override
        public JSValue encode(short[] vals, JSValue out) {
            final List<JSValue> nodes = new ArrayList<>(vals.length);
            for (short val : vals) {
                nodes.add(shortCodec().encode(val, out));
            }
            return Json.array(nodes);
        }

        @Override
        public short[] decode(JSValue in) {
            final JSArray arrNode = in.asArray();
            final short[] vals = new short[arrNode.size()];
            int i = 0;
            for (JSValue node : arrNode) {
                vals[i++] = shortCodec().decode(short.class, node);
            }
            return vals;
        }
    };

    @Override
    public Codec<short[], JSValue> shortArrayCodec() {
        return shortArrayCodec;
    }

    private final Codec.IntCodec<JSValue> intCodec = new Codec.IntCodec<JSValue>() {

        @Override
        public JSValue encodePrim(int val, JSValue out) {
            return Json.number(val);
        }

        @Override
        public int decodePrim(JSValue in) {
            return (int)in.asNumber().getValue();
        }
    };

    @Override
    public Codec.IntCodec<JSValue> intCodec() {
        return intCodec;
    }

    private final Codec<int[], JSValue> intArrayCodec = new Codec<int[], JSValue>() {

        @Override
        public JSValue encode(int[] vals, JSValue out) {
            final List<JSValue> nodes = new ArrayList<>(vals.length);
            for (int val : vals) {
                nodes.add(intCodec().encode(val, out));
            }
            return Json.array(nodes);
        }

        @Override
        public int[] decode(JSValue in) {
            final JSArray arrNode = in.asArray();
            final int[] vals = new int[arrNode.size()];
            int i = 0;
            for (JSValue node : arrNode) {
                vals[i++] = intCodec().decode(int.class, node);
            }
            return vals;
        }
    };

    @Override
    public Codec<int[], JSValue> intArrayCodec() {
        return intArrayCodec;
    }

    private final Codec.LongCodec<JSValue> longCodec = new Codec.LongCodec<JSValue>() {

        @Override
        public JSValue encodePrim(long val, JSValue out) {
            return Json.number(val);
        }

        @Override
        public long decodePrim(JSValue in) {
            return (long)in.asNumber().getValue();
        }
    };

    @Override
    public Codec.LongCodec<JSValue> longCodec() {
        return longCodec;
    }

    private final Codec<long[], JSValue> longArrayCodec = new Codec<long[], JSValue>() {

        @Override
        public JSValue encode(long[] vals, JSValue out) {
            final List<JSValue> nodes = new ArrayList<>(vals.length);
            for (long val : vals) {
                nodes.add(longCodec().encode(val, out));
            }
            return Json.array(nodes);
        }

        @Override
        public long[] decode(JSValue in) {
            final JSArray arrNode = in.asArray();
            final long[] vals = new long[arrNode.size()];
            int i = 0;
            for (JSValue node : arrNode) {
                vals[i++] = longCodec().decode(long.class, node);
            }
            return vals;
        }
    };

    @Override
    public Codec<long[], JSValue> longArrayCodec() {
        return longArrayCodec;
    }

    private final Codec.FloatCodec<JSValue> floatCodec = new Codec.FloatCodec<JSValue>() {

        @Override
        public JSValue encodePrim(float val, JSValue out) {
            return Json.number(val);
        }

        @Override
        public float decodePrim(JSValue in) {
            return (float)in.asNumber().getValue();
        }
    };

    @Override
    public Codec.FloatCodec<JSValue> floatCodec() {
        return floatCodec;
    }

    private final Codec<float[], JSValue> floatArrayCodec = new Codec<float[], JSValue>() {

        @Override
        public JSValue encode(float[] vals, JSValue out) {
            final List<JSValue> nodes = new ArrayList<>(vals.length);
            for (float val : vals) {
                nodes.add(floatCodec().encode(val, out));
            }
            return Json.array(nodes);
        }

        @Override
        public float[] decode(JSValue in) {
            final JSArray arrNode = in.asArray();
            final float[] vals = new float[arrNode.size()];
            int i = 0;
            for (JSValue node : arrNode) {
                vals[i++] = floatCodec().decode(float.class, node);
            }
            return vals;
        }
    };

    @Override
    public Codec<float[], JSValue> floatArrayCodec() {
        return floatArrayCodec;
    }

    private final Codec.DoubleCodec<JSValue> doubleCodec = new Codec.DoubleCodec<JSValue>() {

        @Override
        public JSValue encodePrim(double val, JSValue out) {
            return Json.number(val);
        }

        @Override
        public double decodePrim(JSValue in) {
            return in.asNumber().getValue();
        }
    };

    @Override
    public Codec.DoubleCodec<JSValue> doubleCodec() {
        return doubleCodec;
    }

    private final Codec<double[], JSValue> doubleArrayCodec = new Codec<double[], JSValue>() {

        @Override
        public JSValue encode(double[] vals, JSValue out) {
            final List<JSValue> nodes = new ArrayList<>(vals.length);
            for (double val : vals) {
                nodes.add(doubleCodec().encode(val, out));
            }
            return Json.array(nodes);
        }

        @Override
        public double[] decode(JSValue in) {
            final JSArray arrNode = in.asArray();
            final double[] vals = new double[arrNode.size()];
            int i = 0;
            for (JSValue node : arrNode) {
                vals[i++] = doubleCodec().decode(double.class, node);
            }
            return vals;
        }
    };

    @Override
    public Codec<double[], JSValue> doubleArrayCodec() {
        return doubleArrayCodec;
    }

    private final Codec<String, JSValue> stringCodec = new Codec<String, JSValue>() {
        @Override
        public JSValue encode(String val, JSValue out) {
            return Json.string(val);
        }

        @Override
        public String decode(JSValue in) {
            return in.asString().getValue();
        }
    };

    @Override
    public Codec<String, JSValue> stringCodec() {
        return stringCodec;
    }

    @Override
    public <EM extends Enum<EM>> Codec<EM, JSValue> enumCodec(Class<? super EM> enumType) {
        return new Codec<EM, JSValue>() {
            @Override
            public JSValue encode(EM val, JSValue out) {
                return Json.string(val.name());
            }

            @Override
            public EM decode(Class<EM> dynType, JSValue in) {
                return EM.valueOf(dynType, in.asString().getValue());
            }
        };
    }

    @Override
    public <V> Codec<Map<String, V>, JSValue> mapCodec(Codec<V, JSValue> valueCodec) {
        return new JsonMapCodecs.StringMapCodec<V>(this, valueCodec);
    }

    @Override
    public <K, V> Codec<Map<K, V>, JSValue> mapCodec(Codec<K, JSValue> keyCodec, Codec<V, JSValue> valueCodec) {
        return new JsonMapCodecs.MapCodec<K, V>(this, keyCodec, valueCodec);
    }

    @Override
    public <T> Codec<Collection<T>, JSValue> collCodec(Class<T> elemType, Codec<T, JSValue> elemCodec) {
        return new Codec<Collection<T>, JSValue>() {
            @Override
            public JSValue encode(Collection<T> vals, JSValue out) {
                final List<JSValue> nodes = new ArrayList<>(vals.size());
                for (T val : vals) {
                    nodes.add(elemCodec.encode(val, out));
                }
                return Json.array(nodes);
            }

            @Override
            public Collection<T> decode(Class<Collection<T>> dynType, JSValue in) {
                final Class<T> dynElemType = (Class<T>)dynType.getComponentType();
                final JSArray arrNode = in.asArray();
                final Collection<T> vals = Exceptions.wrap(() -> dynType.newInstance());
                int i = 0;
                for (JSValue node : arrNode) {
                    vals.add(elemCodec.decode(dynElemType, node));
                }
                return vals;
            }
        };
    }

    @Override
    public <T> Codec<T[], JSValue> objectArrayCodec(Class<T> elemType, Codec<T, JSValue> elemCodec) {
        return new Codec<T[], JSValue>() {
            @Override
            public JSValue encode(T[] vals, JSValue out) {
                final List<JSValue> nodes = new ArrayList<>(vals.length);
                for (T val : vals) {
                    nodes.add(elemCodec.encode(val, out));
                }
                return Json.array(nodes);
            }

            @Override
            public T[] decode(Class<T[]> dynType, JSValue in) {
                final Class<T> dynElemType = (Class<T>)dynType.getComponentType();
                final JSArray arrNode = in.asArray();
                final T[] vals = (T[]) Array.newInstance(elemType, arrNode.size());
                int i = 0;
                for (JSValue node : arrNode) {
                    vals[i++] = elemCodec.decode(dynElemType, node);
                }
                return vals;
            }
        };
    }

    @Override
    public <T> Codec<T, JSValue> dynamicCodec(Class<T> stcType) {
        return new Codec<T, JSValue>() {
            @Override
            public JSValue encode(T val, JSValue out) {
                final Class<? extends T> dynType = (Class<? extends T>)val.getClass();
                if (dynType.equals(stcType)) {
                    return JsonCodecCore.this.getNullUnsafeCodec(stcType).encode(val, out);
                } else {
                    final LinkedHashMap<String, JSValue> fields = new LinkedHashMap<>();
                    fields.put(typeFieldName(), Json.string(classToName(dynType)));
                    fields.put(valueFieldName(), encode2(JsonCodecCore.this.getNullUnsafeCodec(dynType), val, out));
                    return Json.object(fields);
                }
            }

            protected <S extends T> JSValue encode2(Codec<S, JSValue> codec, T val, JSValue out) {
                return codec.encode((S)val, out);
            }

            @Override
            public T decode(JSValue in) {
                if (in.isObject()) {
                    final JSObject objNode = in.asObject();
                    final String typeFieldName = typeFieldName();
                    final String valueFieldName = valueFieldName();
                    if (objNode.fields.size() == 2 &&
                            objNode.fields.containsKey(typeFieldName) &&
                            objNode.fields.containsKey(valueFieldName)) {
                        final JSValue typeNode = objNode.fields.get(typeFieldName());
                        final JSValue valueNode = objNode.fields.get(valueFieldName());
                        return decode2(valueNode, nameToClass(typeNode.asString().getValue()));
                    }
                }

                final Codec<T, JSValue> codec = JsonCodecCore.this.getNullUnsafeCodec(stcType);
                return codec.decode(stcType, in);
            }

            protected <S extends T> S decode2(JSValue in, Class<S> dynType) {
                final Codec<S, JSValue> codec = JsonCodecCore.this.getNullUnsafeCodec(dynType);
                return codec.decode(dynType, in);
            }
        };
    }

    @Override
    public <T> Codec<T, JSValue> dynamicCodec(Codec<T, JSValue> codec, Class<T> stcType) {
        return new Codec<T, JSValue>() {
            @Override
            public JSValue encode(T val, JSValue out) {
                final Class<? extends T> dynType = (Class<? extends T>)val.getClass();
                if (dynType.equals(stcType)) {
                    return codec.encode(val, out);
                } else {
                    final LinkedHashMap<String, JSValue> fields = new LinkedHashMap<>();
                    fields.put(typeFieldName(), Json.string(classToName(dynType)));
                    fields.put(valueFieldName(), codec.encode(val, out));
                    return Json.object(fields);
                }
            }

            @Override
            public T decode(JSValue in) {
                final JSObject objNode = in.asObject();
                final String typeFieldName = typeFieldName();
                final String valueFieldName = valueFieldName();
                if (objNode.fields.size() == 2 &&
                        objNode.fields.containsKey(typeFieldName) &&
                        objNode.fields.containsKey(valueFieldName)) {
                    final JSValue typeNode = objNode.fields.get(typeFieldName());
                    final Class<?> dynType = nameToClass(typeNode.asString().getValue());
                    final JSValue valueNode = objNode.fields.get(valueFieldName());
                    return codec.decode((Class<T>)dynType, valueNode);
                } else {
                    return codec.decode(stcType, in);
                }
            }
        };
    }

    @Override
    public <T> Codec<T, JSValue> createObjectCodec(
            Class<T> type,
            Map<String, FieldCodec<JSValue>> fieldCodecs) {
        return new Codec<T, JSValue>() {
            @Override
            public JSValue encode(T val, JSValue out) {
                final LinkedHashMap<String, JSValue> fields = new LinkedHashMap<>();
                fieldCodecs.forEach((name, codec) -> fields.put(name, codec.encode(val, out)));
                return Json.object(fields);
            }

            @Override
            public T decode(Class<T> dynType, JSValue in) {
                final JSObject objNode = in.asObject();
                final T val = Exceptions.wrap(() -> dynType.newInstance(), JsonCodecException::new);
                fieldCodecs.forEach((name, codec) -> {
                    codec.decode(val, objNode.fields.get(name));
                });
                return val;
            }
        };
    }
}
