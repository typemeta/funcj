package org.funcj.codec;

import org.funcj.control.Exceptions;
import org.funcj.data.IList;
import org.funcj.json.Node;

import java.lang.reflect.Array;
import java.util.*;

import static java.util.stream.Collectors.toList;
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

    private final Codec.BooleanArrayCodec<Node> booleanArrayCodec = new Codec.BooleanArrayCodec<Node>() {

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
                vals[i++] = booleanCodec.decode(node);
            }
            return vals;
        }
    };

    @Override
    protected Codec.BooleanArrayCodec<Node> booleanArrayCodec() {
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
                vals[i++] = integerCodec.decode(node);
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
            if (val == null) {
                return JsonCodecCore.this.nullCodec().encode(val, out);
            } else {
                return Node.string(val);
            }
        }

        @Override
        public String decode(Node in) {
            if (in.isNull()) {
                return (String)JsonCodecCore.this.nullCodec().decode(in);
            } else {
                return in.asString().value;
            }
        }
    };

    @Override
    protected Codec<String, Node> stringCodec() {
        return stringCodec;
    }

    @Override
    protected <K, V> Codec<Map<K, V>, Node> mapCodec(
            Class<Map<K, V>> stcClass,
            Class<K> stcKeyClass,
            Class<V> stcValClass) {
        if (stcKeyClass.equals(String.class)) {
            return (Codec)new JsonMapCodecs.StringMapCodec<V>(this, stcValClass);
        } else {
            return new JsonMapCodecs.MapCodec<K, V>(this, stcKeyClass, stcValClass);
        }
    }

    @Override
    protected <T> Codec<T, Node> dynamicCodec(Class<T> stcClass) {
        return new Codec<T, Node>() {
            @Override
            public Node encode(T val, Node out) {
                if (val == null) {
                    return JsonCodecCore.this.nullCodec().encode(val, out);
                } else {
                    final Class<? extends T> dynClass = (Class<? extends T>)val.getClass();
                    final Codec<Object, Node> codec = JsonCodecCore.this.getCodec(stcClass, (Class)val.getClass());
                    if (dynClass == stcClass) {
                        return codec.encode(val, out);
                    } else {
                        final LinkedHashMap<String, Node> fields = new LinkedHashMap<>();
                        fields.put(typeFieldName(), Node.string(classToName(dynClass)));
                        fields.put(valueFieldName(), codec.encode(val, out));
                        return Node.object(fields);
                    }
                }
            }

            @Override
            public T decode(Node in) {
                Class<? extends T> dynClass = stcClass;
                if (in.isNull()) {
                    return (T)nullCodec.decode(in);
                } else if (in.isObject()) {
                    final Node.ObjectNode objNode = in.asObject();
                    final Node typeNode = objNode.fields.get(typeFieldName());
                    if (typeNode != null) {
                        dynClass = nameToClass(typeNode.asString().value);
                    }

                    final Node valueNode = objNode.fields.get(valueFieldName());
                    if (valueNode != null) {
                        return JsonCodecCore.this.getCodec(stcClass, dynClass).decode(valueNode);
                    }
                }

                return JsonCodecCore.this.getCodec(stcClass, dynClass).decode(in);
            }
        };
    }

    @Override
    protected <T> Codec<T[], Node> objectArrayCodec(Class<T> elemClass, Codec<T, Node> elemCodec) {
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
            public T[] decode(Node in) {
                final Node.ArrayNode arrNode = in.asArray();
                final T[] vals = (T[]) Array.newInstance(elemClass, arrNode.values.size());
                int i = 0;
                for (Node node : arrNode.values) {
                    vals[i++] = elemCodec.decode(node);
                }
                return vals;
            }
        };
    }

    @Override
    protected <T> Codec<T, Node> createObjectCodec(
            Class<T> stcClass,
            Class<? extends T> dynClass,
            Map<String, FieldCodec<Node>> fieldCodecs) {
        return new Codec<T, Node>() {
            @Override
            public Node encode(T val, Node out) {
                if (val == null) {
                    return nullCodec.encode(val, out);
                } else {
                    final Class<? extends T> dynClass = (Class<? extends T>)val.getClass();
                    final LinkedHashMap<String, Node> fields = new LinkedHashMap<>();
                    if (dynClass != stcClass) {
                        fields.put(typeFieldName(), Node.string(classToName(dynClass)));
                    }
                    fieldCodecs.forEach((name, codec) -> fields.put(name, codec.encode(val, out)));
                    return Node.object(fields);
                }
            }

            @Override
            public T decode(Node in) {
                if (in.isNull()) {
                    return (T)nullCodec.decode(in);
                } else {
                    final Node.ObjectNode objNode = in.asObject();
                    final String typeFieldName = typeFieldName();
                    final Class<? extends T> type;
                    final Node typeNode = objNode.fields.get(typeFieldName);
                    if (typeNode != null) {
                        type = nameToClass(typeNode.asString().value);
                    } else {
                        type = stcClass;
                    }
                    final T val = (T) Exceptions.wrap(() -> type.newInstance());
                    fieldCodecs.forEach((name, codec) -> {
                        if (!name.equals(typeFieldName)) {
                            codec.decode(val, objNode.fields.get(name));
                        }
                    });
                    return val;
                }
            }
        };
    }
}
