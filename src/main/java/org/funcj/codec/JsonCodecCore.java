package org.funcj.codec;

import org.funcj.control.Exceptions;
import org.funcj.json.Node;

import java.lang.reflect.*;
import java.util.*;

import static org.funcj.control.Exceptions.TODO;

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
    protected Codec.NullCodec<Node> nullCodec() {
        return nullCodec;
    }

    private final Codec.BooleanCodec<Node> booleanCodec = new Codec.BooleanCodec<Node>() {

        @Override
        Node encodePrim(boolean val, Node out) {
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
                nodes.add(booleanCodec.encode(val, out));
            }
            return Node.array(nodes);
        }

        @Override
        public boolean[] decode(Node in) {
            final Node.ArrayNode arrNode = in.asArray();
            final boolean[] vals = new boolean[arrNode.values.size()];
            int i = 0;
            for (Node node : arrNode.values) {
                vals[i++] = booleanCodec.decode(boolean.class, node);
            }
            return vals;
        }
    };

    @Override
    protected Codec<boolean[], Node> booleanArrayCodec() {
        return booleanArrayCodec;
    }

    private final Codec.IntegerCodec<Node> integerCodec = new Codec.IntegerCodec<Node>() {

        @Override
        Node encodePrim(int val, Node out) {
            return Node.number(val);
        }

        @Override
        public int decodePrim(Node in) {
            return (int)in.asNumber().value;
        }
    };

    @Override
    protected Codec.IntegerCodec<Node> integerCodec() {
        return integerCodec;
    }

    private final Codec<int[], Node> integerArrayCodec = new Codec<int[], Node>() {

        @Override
        public Node encode(int[] vals, Node out) {
            final List<Node> nodes = new ArrayList<>(vals.length);
            for (int val : vals) {
                nodes.add(integerCodec.encode(val, out));
            }
            return Node.array(nodes);
        }

        @Override
        public int[] decode(Node in) {
            final Node.ArrayNode arrNode = in.asArray();
            final int[] vals = new int[arrNode.values.size()];
            int i = 0;
            for (Node node : arrNode.values) {
                vals[i++] = integerCodec.decode(int.class, node);
            }
            return vals;
        }
    };

    @Override
    protected Codec<int[], Node> integerArrayCodec() {
        return integerArrayCodec;
    }

    private final Codec<String, Node> stringCodec = new Codec<String, Node>() {
        @Override
        public Node encode(String val, Node out) {
            return Node.string(val);
        }

        @Override
        public String decode(Node in) {
            return in.asString().value;
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
                return EM.valueOf(dynType, in.asString().value);
            }
        };
    }

    @Override
    protected <K, V> Codec<Map<K, V>, Node> mapCodec(
            Class<K> keyType,
            Class<V> valType) {
        final Codec<V, Node> valueCodec = dynamicCodec(valType);
        if (String.class.equals(keyType)) {
            return (Codec)new JsonMapCodecs.StringMapCodec<V>(this, valueCodec);
        } else {
            final Codec<K, Node> keyCodec = dynamicCodec(keyType);
            return new JsonMapCodecs.MapCodec<K, V>(this, keyCodec, valueCodec);
        }
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
                final Node.ArrayNode arrNode = in.asArray();
                final T[] vals = (T[]) Array.newInstance(elemType, arrNode.values.size());
                int i = 0;
                for (Node node : arrNode.values) {
                    vals[i++] = elemCodec.decode((Class<T>)dynType.getComponentType(), node);
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
                if (dynType.equals(stcType) || Modifier.isFinal(stcType.getModifiers())) {
                    return JsonCodecCore.this.getCodec(stcType).encode(val, out);
                } else {
                    final LinkedHashMap<String, Node> fields = new LinkedHashMap<>();
                    fields.put(typeFieldName(), Node.string(classToName(dynType)));
                    fields.put(valueFieldName(), encode2(JsonCodecCore.this. getCodec(dynType), val, out));
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
                        return decode2(valueNode, nameToClass(typeNode.asString().value));
                    }
                }

                final Codec<T, Node> codec = JsonCodecCore.this.getCodec(stcType);
                return codec.decode(stcType, in);
            }

            protected <S extends T> S decode2(Node in, Class<S> dynType) {
                final Codec<S, Node> codec = JsonCodecCore.this.getCodec(dynType);
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
                    final Class<?> dynType = nameToClass(typeNode.asString().value);
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
                final T val = (T) Exceptions.wrap(() -> dynType.newInstance());
                fieldCodecs.forEach((name, codec) -> {
                    codec.decode(val, objNode.fields.get(name));
                });
                return val;
            }
        };
    }
}
