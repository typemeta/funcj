package org.funcj.codec;

import org.funcj.control.Exceptions;
import org.funcj.json.Node;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.BinaryOperator;

import static java.util.stream.Collectors.toMap;
import static org.funcj.control.Exceptions.TODO;

public class JsonCodec extends Codec.Registry<Node> {

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
    protected <T> Codec.BoxedArrayCodec<T, Node> boxedArrayCodec(
            Class<T> elemClass,
            Codec<T, Node> elemCodec) {
        return new Codec.BoxedArrayCodec<T, Node>(elemCodec) {
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

    @Override
    protected <T> Codec<T[], Node> objectArrayCodec() {
        throw TODO();
    }

    protected String typeFieldName() {
        return "@type";
    }

    @Override
    protected <T> Codec<T, Node> getCodecImpl(Class<T> clazz, Map<String, FieldCodec<Node>> fieldCodecs) {
        return new Codec<T, Node>() {
            @Override
            public Node encode(T val, Node out) {
                final LinkedHashMap<String, Node> fields = new LinkedHashMap<>();
                fields.put(typeFieldName(), Node.string(classToName(clazz)));
                fieldCodecs.forEach((name, codec) -> fields.put(name, codec.encode(val, out)));
                return Node.object(fields);
            }

            @Override
            public T decode(Node in) {
                final Node.ObjectNode objNode = in.asObject();
                final T val = (T)Exceptions.wrap(() -> clazz.newInstance());
                fieldCodecs.forEach((name, codec) -> {
                    codec.decode(val, objNode.fields.get(name));
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

    private static <T> BinaryOperator<T> throwingMerger() {
        return (u, v) -> {
            throw new IllegalStateException(String.format("Duplicate key %s", u));
        };
    }
}
