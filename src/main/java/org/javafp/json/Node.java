package org.javafp.json;


import org.javafp.data.*;

import java.util.*;

import static java.util.stream.Collectors.toMap;

public interface Node {
    static NullNode nul() {
        return NullNode.NULL;
    }

    static BoolNode bool(boolean value) {
        return value ? BoolNode.TRUE : BoolNode.FALSE;
    }

    static NumberNode number(double value) {
        return new NumberNode(value);
    }

    static StringNode string(String value) {
        return new StringNode(value);
    }

    static ArrayNode array(IList<Node> values) {
        return new ArrayNode(values);
    }

    static ObjectNode object(LinkedHashMap<String, Node> values) {
        return new ObjectNode(values);
    }

    static ObjectNode object(IList<Tuple2<String, Node>> fields) {
        final LinkedHashMap<String, Node> m =
            fields.stream().collect(
                toMap(
                    f -> f._1,
                    f -> f._2,
                    (u,v) -> { throw new IllegalStateException(String.format("Duplicate key %s", u)); },
                    LinkedHashMap::new
                )
            );
        return new ObjectNode(m);
    }

    final class NullNode implements Node {
        public static final NullNode NULL = new NullNode();

        private NullNode() {
        }

        @Override
        public StringBuilder toJson(StringBuilder sb) {
            return sb.append("null");
        }

        @Override
        public NullNode nullNode() {
            return this;
        }
    }

    final class BoolNode implements Node {
        public static final BoolNode TRUE = new BoolNode(true);
        public static final BoolNode FALSE = new BoolNode(false);

        public final boolean value;

        public BoolNode(boolean value) {
            this.value = value;
        }

        @Override
        public StringBuilder toJson(StringBuilder sb) {
            return sb.append(Boolean.toString(value));
        }

        @Override
        public BoolNode boolNode() {
            return this;
        }
    }

    final class NumberNode implements Node {
        public final double value;

        public NumberNode(double value) {
            this.value = value;
        }

        @Override
        public StringBuilder toJson(StringBuilder sb) {
            return sb.append(Double.toString(value));
        }

        @Override
        public NumberNode numberNode() {
            return this;
        }
    }

    final class StringNode implements Node {
        public final String value;

        public StringNode(String value) {
            this.value = value;
        }

        @Override
        public StringBuilder toJson(StringBuilder sb) {
            return Utils.string(value, sb);
        }

        @Override
        public StringNode stringNode() {
            return this;
        }
    }

    final class ArrayNode implements Node {
        public final IList<Node> values;

        public ArrayNode(IList<Node> values) {
            this.values = values;
        }

        @Override
        public StringBuilder toJson(StringBuilder sb) {
            sb.append('[');
            boolean first = true;
            for (Node n : values) {
                if (first) {
                    first = false;
                } else {
                    sb.append(',');
                }
                n.toJson(sb);
            }
            return sb.append(']');
        }

        @Override
        public ArrayNode arrayNode() {
            return this;
        }
    }

    final class ObjectNode implements Node {
        public final LinkedHashMap<String, Node> values;

        public ObjectNode(LinkedHashMap<String, Node> values) {
            this.values = values;
        }

        @Override
        public StringBuilder toJson(StringBuilder sb) {
            sb.append('{');
            boolean first = true;
            for (Map.Entry<String, Node> en : values.entrySet()) {
                if (first) {
                    first = false;
                } else {
                    sb.append(',');
                }
                Utils.string(en.getKey(), sb).append(':');
                en.getValue().toJson(sb);
            }
            return sb.append('}');
        }

        @Override
        public ObjectNode objectNode() {
            return this;
        }
    }

    StringBuilder toJson(StringBuilder sb);

    default String toJson() {
        return toJson(new StringBuilder()).toString();
    }

    default NullNode nullNode() {
        throw new RuntimeException(getClass().getSimpleName() + " is not a NullNode");
    }

    default BoolNode boolNode() {
        throw new RuntimeException(getClass().getSimpleName() + " is not a BoolNode");
    }

    default NumberNode numberNode() {
        throw new RuntimeException(getClass().getSimpleName() + " is not a NumberNode");
    }

    default StringNode stringNode() {
        throw new RuntimeException(getClass().getSimpleName() + " is not a StringNode");
    }

    default ArrayNode arrayNode() {
        throw new RuntimeException(getClass().getSimpleName() + " is not a ArrayNode");
    }

    default ObjectNode objectNode() {
        throw new RuntimeException(getClass().getSimpleName() + " is not a ObjectNode");
    }
}

class Utils {
    static StringBuilder string(String s, StringBuilder sb) {
        sb.append('"');
        escape(s, sb);
        return sb.append('"');
    }

    static StringBuilder escape(String s, StringBuilder sb) {
        final int len = s.length();
        for (int i = 0; i < len; ++i) {
            final char c = s.charAt(i);
            switch(c) {
                case '\"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '/':
                    sb.append("\\/");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                default:
                    if (c <= '\u001F' ||
                        c >= '\u007F' && c <= '\u009F' ||
                        c >= '\u2000' && c <= '\u20FF') {
                        sb.append("\\u").append(Integer.toHexString(c | 0x10000).substring(1));
                    } else {
                        sb.append(c);
                    }
            }
        }

        return sb;
    }
}