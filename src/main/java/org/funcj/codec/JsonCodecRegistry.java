package org.funcj.codec;

import org.funcj.json.Node;

import java.util.LinkedHashMap;
import java.util.function.BinaryOperator;

import static java.util.stream.Collectors.toMap;

public class JsonCodecRegistry implements CodecRegistry<Node> {

    private final Codec.BooleanCodec<Node> booleanCodec = new Codec.BooleanCodec<Node>() {

        @Override
        public Node encode(boolean val, Node out) {
            return Node.bool(val);
        }

        @Override
        public boolean decode(Node in) {
            return ((Node.BoolNode)in).value;
        }
    };

    @Override
    public Codec.BooleanCodec<Node> booleanCodec() {
        return booleanCodec;
    }

    @Override
    public <T> Codec<T, Node> objectCodec(ClassDef classDef) {
        return new Codec<T, Node>() {
            @Override
            public Node encode(T val, Node out) {
                final LinkedHashMap<String, Node> fields =
                        classDef.fields
                                .entrySet()
                                .stream()
                                .collect(
                                     toMap(
                                             en -> en.getKey(),
                                             en -> en.getValue().encode(JsonCodecRegistry.this, val, out),
                                             JsonCodecRegistry::throwingMerger,
                                             LinkedHashMap::new
                                );
            }

            @Override
            public T decode(Node in) {
                return null;
            }
        };
    }

    private static <T> BinaryOperator<T> throwingMerger() {
        return (u, v) -> { throw new IllegalStateException(String.format("Duplicate key %s", u)); };
    }
}
