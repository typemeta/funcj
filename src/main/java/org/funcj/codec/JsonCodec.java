package org.funcj.codec;

import org.funcj.control.Exceptions;
import org.funcj.json.Node;

import java.lang.reflect.Array;
import java.util.*;

public class JsonCodec extends Codec.DynamicCodec<Node> {

    private final Codec.BooleanCodec<Node> booleanCodec = new Codec.BooleanCodec<Node>() {

        @Override
        Node encodePrim(boolean val, Node out) {
            return Node.bool(val);
        }

        @Override
        public boolean decodePrim(Node in) {
            return ((Node.BoolNode)in).value;
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

    @Override
    protected <T> Codec.ObjectArrayCodec<T, Node> objectArrayCodec(
            Class<T> elemClass,
            Codec<T, Node> elemCodec) {
        return new Codec.ObjectArrayCodec<T, Node>(elemCodec) {
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
                final T[] vals = (T[])Array.newInstance(elemClass, arrNode.values.size());
                int i = 0;
                for (Node node : arrNode.values) {
                    vals[i++] = elemCodec.decode(node);
                }
                return vals;
            }
        };
    }

    protected String typeFieldName() {
        return "@type";
    }

    @Override
    protected <T> Codec<T, Node> getCodecImpl(
            Class<T> stcClass,
            Map<String, FieldCodec<Node>> fieldCodecs) {
        return new Codec<T, Node>() {
            @Override
            public Node encode(T val, Node out) {
                if (val == null) {
                    return nullCodec.encode(val, out);
                } else {
                    final Class<? extends T> dynClass = (Class<? extends T>)val.getClass();
                    final LinkedHashMap<String, Node> fields = new LinkedHashMap<>();
                    if (!dynClass.equals(stcClass)) {
                        fields.put(typeFieldName(), Node.string(classToName(dynClass)));
                    } else {
                        int i = 0;
                    }
                    fieldCodecs.forEach((name, codec) -> fields.put(name, codec.encode(val, out)));
                    return Node.object(fields);
                }
            }

            @Override
            public T decode(Node in) {
                final Node.ObjectNode objNode = in.asObject();
                final String typeFieldName = typeFieldName();
                final Class<? extends T> type;
                final Node typeNode = objNode.fields.get(typeFieldName);
                if (typeNode != null) {
                    type = nameToClass(typeNode.asString().value);
                } else {
                    type = stcClass;
                }
                final T val = (T)Exceptions.wrap(() -> type.newInstance());
                fieldCodecs.forEach((name, codec) -> {
                    if (!name.equals(typeFieldName)) {
                        codec.decode(val, objNode.fields.get(name));
                    }
                });
                return val;
            }
        };
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

    @Override
    public Object decode(Node in) {
        if (in.isNull()) {
            return nullCodec.decode(in);
        } else {
            final Node.ObjectNode objNode = in.asObject();
            final String typeName = objNode.fields.get(typeFieldName()).asString().value;
            final Class<?> clazz = nameToClass(typeName);
            return decode(in, clazz);
        }
    }
}
