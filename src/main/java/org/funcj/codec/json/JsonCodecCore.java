package org.funcj.codec.json;

import org.funcj.codec.*;
import org.funcj.control.Exceptions;
import org.funcj.json.Node;

import java.lang.reflect.Array;
import java.util.*;

public class JsonCodecCore extends CodecCore<Node> {

    protected String typeFieldName() {
        return "@type";
    }

    protected String keyFieldName() {
        return "@key";
    }

    protected String valueFieldName() {
        return "@value";
    }

    private final Codec.NullCodec<Node> nullCodec = new Codec.NullCodec<Node>() {
        @Override
        public boolean isNull(Node in) {
            return in.isNull();
        }

        @Override
        public Node encode(Object val, Node out) {
            return Node.nul();
        }

        @Override
        public Object decode(Node in) {
            in.asNull();
            return null;
        }
    };

    @Override
    public <T> Node encode(Class<T> type, T val) {
        return super.encode(type, val, null);
    }

    @Override
    protected Codec.NullCodec<Node> nullCodec() {
        return nullCodec;
    }

    private final Codec.BooleanCodec<Node> booleanCodec = new Codec.BooleanCodec<Node>() {

        @Override
        public Node encodePrim(boolean val, Node out) {
            return Node.bool(val);
        }

        @Override
        public boolean decodePrim(Node in) {
            return in.asBool().value;
        }
    };

    @Override
    protected Codec.BooleanCodec<Node> booleanCodec() {
        return booleanCodec;
    }

    private final Codec<boolean[], Node> booleanArrayCodec = new Codec<boolean[], Node>() {

        @Override
        public Node encode(boolean[] vals, Node out) {
            final List<Node> nodes = new ArrayList<>(vals.length);
            for (boolean val : vals) {
                nodes.add(booleanCodec().encode(val, out));
            }
            return Node.array(nodes);
        }

        @Override
        public boolean[] decode(Node in) {
            final Node.ArrayNode arrNode = in.asArray();
            final boolean[] vals = new boolean[arrNode.size()];
            int i = 0;
            for (Node node : arrNode) {
                vals[i++] = booleanCodec().decode(boolean.class, node);
            }
            return vals;
        }
    };

    @Override
    protected Codec<boolean[], Node> booleanArrayCodec() {
        return booleanArrayCodec;
    }

    private final Codec.ByteCodec<Node> byteCodec = new Codec.ByteCodec<Node>() {

        @Override
        public Node encodePrim(byte val, Node out) {
            return Node.number(val);
        }

        @Override
        public byte decodePrim(Node in) {
            return (byte)in.asNumber().getValue();
        }
    };

    @Override
    protected Codec.ByteCodec<Node> byteCodec() {
        return byteCodec;
    }

    private final Codec<byte[], Node> byteArrayCodec = new Codec<byte[], Node>() {

        @Override
        public Node encode(byte[] vals, Node out) {
            final List<Node> nodes = new ArrayList<>(vals.length);
            for (byte val : vals) {
                nodes.add(byteCodec().encode(val, out));
            }
            return Node.array(nodes);
        }

        @Override
        public byte[] decode(Node in) {
            final Node.ArrayNode arrNode = in.asArray();
            final byte[] vals = new byte[arrNode.size()];
            int i = 0;
            for (Node node : arrNode) {
                vals[i++] = byteCodec().decode(byte.class, node);
            }
            return vals;
        }
    };

    @Override
    protected Codec<byte[], Node> byteArrayCodec() {
        return byteArrayCodec;
    }

    private final Codec.CharCodec<Node> charCodec = new Codec.CharCodec<Node>() {

        @Override
        public Node encodePrim(char val, Node out) {
            return Node.string(String.valueOf(val));
        }

        @Override
        public char decodePrim(Node in) {
            return in.asString().getValue().charAt(0);
        }
    };

    @Override
    protected Codec.CharCodec<Node> charCodec() {
        return charCodec;
    }

    private final Codec<char[], Node> charArrayCodec = new Codec<char[], Node>() {

        @Override
        public Node encode(char[] vals, Node out) {
            final List<Node> nodes = new ArrayList<>(vals.length);
            for (char val : vals) {
                nodes.add(charCodec().encode(val, out));
            }
            return Node.array(nodes);
        }

        @Override
        public char[] decode(Node in) {
            final Node.ArrayNode arrNode = in.asArray();
            final char[] vals = new char[arrNode.size()];
            int i = 0;
            for (Node node : arrNode) {
                vals[i++] = charCodec().decode(char.class, node);
            }
            return vals;
        }
    };

    @Override
    protected Codec<char[], Node> charArrayCodec() {
        return charArrayCodec;
    }

    private final Codec.ShortCodec<Node> shortCodec = new Codec.ShortCodec<Node>() {

        @Override
        public Node encodePrim(short val, Node out) {
            return Node.number(val);
        }

        @Override
        public short decodePrim(Node in) {
            return (short)in.asNumber().getValue();
        }
    };

    @Override
    protected Codec.ShortCodec<Node> shortCodec() {
        return shortCodec;
    }

    private final Codec<short[], Node> shortArrayCodec = new Codec<short[], Node>() {

        @Override
        public Node encode(short[] vals, Node out) {
            final List<Node> nodes = new ArrayList<>(vals.length);
            for (short val : vals) {
                nodes.add(shortCodec().encode(val, out));
            }
            return Node.array(nodes);
        }

        @Override
        public short[] decode(Node in) {
            final Node.ArrayNode arrNode = in.asArray();
            final short[] vals = new short[arrNode.size()];
            int i = 0;
            for (Node node : arrNode) {
                vals[i++] = shortCodec().decode(short.class, node);
            }
            return vals;
        }
    };

    @Override
    protected Codec<short[], Node> shortArrayCodec() {
        return shortArrayCodec;
    }

    private final Codec.IntCodec<Node> intCodec = new Codec.IntCodec<Node>() {

        @Override
        public Node encodePrim(int val, Node out) {
            return Node.number(val);
        }

        @Override
        public int decodePrim(Node in) {
            return (int)in.asNumber().getValue();
        }
    };

    @Override
    protected Codec.IntCodec<Node> intCodec() {
        return intCodec;
    }

    private final Codec<int[], Node> integerArrayCodec = new Codec<int[], Node>() {

        @Override
        public Node encode(int[] vals, Node out) {
            final List<Node> nodes = new ArrayList<>(vals.length);
            for (int val : vals) {
                nodes.add(intCodec().encode(val, out));
            }
            return Node.array(nodes);
        }

        @Override
        public int[] decode(Node in) {
            final Node.ArrayNode arrNode = in.asArray();
            final int[] vals = new int[arrNode.size()];
            int i = 0;
            for (Node node : arrNode) {
                vals[i++] = intCodec().decode(int.class, node);
            }
            return vals;
        }
    };

    @Override
    protected Codec<int[], Node> intArrayCodec() {
        return integerArrayCodec;
    }

    private final Codec.LongCodec<Node> longCodec = new Codec.LongCodec<Node>() {

        @Override
        public Node encodePrim(long val, Node out) {
            return Node.number(val);
        }

        @Override
        public long decodePrim(Node in) {
            return (long)in.asNumber().getValue();
        }
    };

    @Override
    protected Codec.LongCodec<Node> longCodec() {
        return longCodec;
    }

    private final Codec<long[], Node> longArrayCodec = new Codec<long[], Node>() {

        @Override
        public Node encode(long[] vals, Node out) {
            final List<Node> nodes = new ArrayList<>(vals.length);
            for (long val : vals) {
                nodes.add(longCodec().encode(val, out));
            }
            return Node.array(nodes);
        }

        @Override
        public long[] decode(Node in) {
            final Node.ArrayNode arrNode = in.asArray();
            final long[] vals = new long[arrNode.size()];
            int i = 0;
            for (Node node : arrNode) {
                vals[i++] = longCodec().decode(long.class, node);
            }
            return vals;
        }
    };

    @Override
    protected Codec<long[], Node> longArrayCodec() {
        return longArrayCodec;
    }

    private final Codec.FloatCodec<Node> floatCodec = new Codec.FloatCodec<Node>() {

        @Override
        public Node encodePrim(float val, Node out) {
            return Node.number(val);
        }

        @Override
        public float decodePrim(Node in) {
            return (float)in.asNumber().getValue();
        }
    };

    @Override
    protected Codec.FloatCodec<Node> floatCodec() {
        return floatCodec;
    }

    private final Codec<float[], Node> floatArrayCodec = new Codec<float[], Node>() {

        @Override
        public Node encode(float[] vals, Node out) {
            final List<Node> nodes = new ArrayList<>(vals.length);
            for (float val : vals) {
                nodes.add(floatCodec().encode(val, out));
            }
            return Node.array(nodes);
        }

        @Override
        public float[] decode(Node in) {
            final Node.ArrayNode arrNode = in.asArray();
            final float[] vals = new float[arrNode.size()];
            int i = 0;
            for (Node node : arrNode) {
                vals[i++] = floatCodec().decode(float.class, node);
            }
            return vals;
        }
    };

    @Override
    protected Codec<float[], Node> floatArrayCodec() {
        return floatArrayCodec;
    }

    private final Codec.DoubleCodec<Node> doubleCodec = new Codec.DoubleCodec<Node>() {

        @Override
        public Node encodePrim(double val, Node out) {
            return Node.number(val);
        }

        @Override
        public double decodePrim(Node in) {
            return in.asNumber().getValue();
        }
    };

    @Override
    protected Codec.DoubleCodec<Node> doubleCodec() {
        return doubleCodec;
    }

    private final Codec<double[], Node> doubleArrayCodec = new Codec<double[], Node>() {

        @Override
        public Node encode(double[] vals, Node out) {
            final List<Node> nodes = new ArrayList<>(vals.length);
            for (double val : vals) {
                nodes.add(doubleCodec().encode(val, out));
            }
            return Node.array(nodes);
        }

        @Override
        public double[] decode(Node in) {
            final Node.ArrayNode arrNode = in.asArray();
            final double[] vals = new double[arrNode.size()];
            int i = 0;
            for (Node node : arrNode) {
                vals[i++] = doubleCodec().decode(double.class, node);
            }
            return vals;
        }
    };

    @Override
    protected Codec<double[], Node> doubleArrayCodec() {
        return doubleArrayCodec;
    }

    private final Codec<String, Node> stringCodec = new Codec<String, Node>() {
        @Override
        public Node encode(String val, Node out) {
            return Node.string(val);
        }

        @Override
        public String decode(Node in) {
            return in.asString().getValue();
        }
    };

    @Override
    protected Codec<String, Node> stringCodec() {
        return stringCodec;
    }

    @Override
    protected <EM extends Enum<EM>> Codec<EM, Node> enumCodec(Class<? super EM> enumType) {
        return new Codec<EM, Node>() {
            @Override
            public Node encode(EM val, Node out) {
                return Node.string(val.name());
            }

            @Override
            public EM decode(Class<EM> dynType, Node in) {
                return EM.valueOf(dynType, in.asString().getValue());
            }
        };
    }

    @Override
    protected <V> Codec<Map<String, V>, Node> mapCodec(Codec<V, Node> valueCodec) {
        return new JsonMapCodecs.StringMapCodec<V>(this, valueCodec);
    }

    @Override
    protected <K, V> Codec<Map<K, V>, Node> mapCodec(Codec<K, Node> keyCodec, Codec<V, Node> valueCodec) {
        return new JsonMapCodecs.MapCodec<K, V>(this, keyCodec, valueCodec);
    }

    @Override
    protected <T> Codec<Collection<T>, Node> collCodec(Class<T> elemType, Codec<T, Node> elemCodec) {
        return new Codec<Collection<T>, Node>() {
            @Override
            public Node encode(Collection<T> vals, Node out) {
                final List<Node> nodes = new ArrayList<>(vals.size());
                for (T val : vals) {
                    nodes.add(elemCodec.encode(val, out));
                }
                return Node.array(nodes);
            }

            @Override
            public Collection<T> decode(Class<Collection<T>> dynType, Node in) {
                final Class<T> dynElemType = (Class<T>)dynType.getComponentType();
                final Node.ArrayNode arrNode = in.asArray();
                final Collection<T> vals = Exceptions.wrap(() -> dynType.newInstance());
                int i = 0;
                for (Node node : arrNode) {
                    vals.add(elemCodec.decode(dynElemType, node));
                }
                return vals;
            }
        };
    }

    @Override
    protected <T> Codec<T[], Node> objectArrayCodec(Class<T> elemType, Codec<T, Node> elemCodec) {
        return new Codec<T[], Node>() {
            @Override
            public Node encode(T[] vals, Node out) {
                final List<Node> nodes = new ArrayList<>(vals.length);
                for (T val : vals) {
                    nodes.add(elemCodec.encode(val, out));
                }
                return Node.array(nodes);
            }

            @Override
            public T[] decode(Class<T[]> dynType, Node in) {
                final Class<T> dynElemType = (Class<T>)dynType.getComponentType();
                final Node.ArrayNode arrNode = in.asArray();
                final T[] vals = (T[]) Array.newInstance(elemType, arrNode.size());
                int i = 0;
                for (Node node : arrNode) {
                    vals[i++] = elemCodec.decode(dynElemType, node);
                }
                return vals;
            }
        };
    }

    @Override
    protected <T> Codec<T, Node> dynamicCodec(Class<T> stcType) {
        return new Codec<T, Node>() {
            @Override
            public Node encode(T val, Node out) {
                final Class<? extends T> dynType = (Class<? extends T>)val.getClass();
                if (dynType.equals(stcType)) {
                    return JsonCodecCore.this.getNullUnsafeCodec(stcType).encode(val, out);
                } else {
                    final LinkedHashMap<String, Node> fields = new LinkedHashMap<>();
                    fields.put(typeFieldName(), Node.string(classToName(dynType)));
                    fields.put(valueFieldName(), encode2(JsonCodecCore.this.getNullUnsafeCodec(dynType), val, out));
                    return Node.object(fields);
                }
            }

            protected <S extends T> Node encode2(Codec<S, Node> codec, T val, Node out) {
                return codec.encode((S)val, out);
            }

            @Override
            public T decode(Node in) {
                if (in.isObject()) {
                    final Node.ObjectNode objNode = in.asObject();
                    final String typeFieldName = typeFieldName();
                    final String valueFieldName = valueFieldName();
                    if (objNode.fields.size() == 2 &&
                            objNode.fields.containsKey(typeFieldName) &&
                            objNode.fields.containsKey(valueFieldName)) {
                        final Node typeNode = objNode.fields.get(typeFieldName());
                        final Node valueNode = objNode.fields.get(valueFieldName());
                        return decode2(valueNode, nameToClass(typeNode.asString().getValue()));
                    }
                }

                final Codec<T, Node> codec = JsonCodecCore.this.getNullUnsafeCodec(stcType);
                return codec.decode(stcType, in);
            }

            protected <S extends T> S decode2(Node in, Class<S> dynType) {
                final Codec<S, Node> codec = JsonCodecCore.this.getNullUnsafeCodec(dynType);
                return codec.decode(dynType, in);
            }
        };
    }

    @Override
    protected <T> Codec<T, Node> dynamicCodec(Codec<T, Node> codec, Class<T> stcType) {
        return new Codec<T, Node>() {
            @Override
            public Node encode(T val, Node out) {
                final Class<? extends T> dynType = (Class<? extends T>)val.getClass();
                if (dynType.equals(stcType)) {
                    return codec.encode(val, out);
                } else {
                    final LinkedHashMap<String, Node> fields = new LinkedHashMap<>();
                    fields.put(typeFieldName(), Node.string(classToName(dynType)));
                    fields.put(valueFieldName(), codec.encode(val, out));
                    return Node.object(fields);
                }
            }

            @Override
            public T decode(Node in) {
                final Node.ObjectNode objNode = in.asObject();
                final String typeFieldName = typeFieldName();
                final String valueFieldName = valueFieldName();
                if (objNode.fields.size() == 2 &&
                        objNode.fields.containsKey(typeFieldName) &&
                        objNode.fields.containsKey(valueFieldName)) {
                    final Node typeNode = objNode.fields.get(typeFieldName());
                    final Class<?> dynType = nameToClass(typeNode.asString().getValue());
                    final Node valueNode = objNode.fields.get(valueFieldName());
                    return codec.decode((Class<T>)dynType, valueNode);
                } else {
                    return codec.decode(stcType, in);
                }
            }
        };
    }

    @Override
    protected <T> Codec<T, Node> createObjectCodec(
            Class<T> type,
            Map<String, FieldCodec<Node>> fieldCodecs) {
        return new Codec<T, Node>() {
            @Override
            public Node encode(T val, Node out) {
                final LinkedHashMap<String, Node> fields = new LinkedHashMap<>();
                fieldCodecs.forEach((name, codec) -> fields.put(name, codec.encode(val, out)));
                return Node.object(fields);
            }

            @Override
            public T decode(Class<T> dynType, Node in) {
                final Node.ObjectNode objNode = in.asObject();
                final T val = Exceptions.wrap(() -> dynType.newInstance(), JsonCodecException::new);
                fieldCodecs.forEach((name, codec) -> {
                    codec.decode(val, objNode.fields.get(name));
                });
                return val;
            }
        };
    }
}
