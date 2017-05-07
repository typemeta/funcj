package org.funcj.json;


import org.funcj.data.*;
import org.funcj.document.*;
import org.funcj.util.*;
import org.funcj.util.Functions.F;

import java.util.*;

import static java.util.stream.Collectors.toMap;

public interface Node {
    static NullNode nul() {
        return NullNode.INSTANCE;
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

    static ArrayNode array(Node... values) {
        return new ArrayNode(IList.of(values));
    }

    static Tuple2<String, Node> entry(String name, Node node) {
        return Tuple2.of(name, node);
    }

    static ObjectNode object(Tuple2<String, Node>... fields) {
        return object(IList.of(fields));
    }

    static ObjectNode object(IList<Tuple2<String, Node>> fields) {
        final LinkedHashMap<String, Node> m =
                fields.stream().collect(
                        toMap(
                                Tuple2::get1,
                                Tuple2::get2,
                                Utils::duplicateKeyError,
                                LinkedHashMap::new
                        )
                );
        return new ObjectNode(m);
    }

    static ObjectNode object(LinkedHashMap<String, Node> values) {
        return new ObjectNode(values);
    }

    enum NullNode implements Node {
        INSTANCE;

        @Override
        public String toString() {
            return "null";
        }

        @Override
        public Document toDocument() {
            return API.text(toString());
        }

        @Override
        public StringBuilder toString(StringBuilder sb) {
            return sb.append(toString());
        }

        @Override
        public <T> T match(
                F<NullNode, T> nln,
                F<BoolNode, T> bn,
                F<NumberNode, T> nmn,
                F<StringNode, T> sn,
                F<ArrayNode, T> an,
                F<ObjectNode, T> on) {
            return nln.apply(this);
        }
    }

    enum BoolNode implements Node {
        TRUE(true),
        FALSE(false);

        public final boolean value;

        BoolNode(boolean value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return Boolean.toString(value);
        }

        @Override
        public Document toDocument() {
            return API.text(toString());
        }

        @Override
        public StringBuilder toString(StringBuilder sb) {
            return sb.append(toString());
        }

        @Override
        public <T> T match(
                F<NullNode, T> nln,
                F<BoolNode, T> bn,
                F<NumberNode, T> nmn,
                F<StringNode, T> sn,
                F<ArrayNode, T> an,
                F<ObjectNode, T> on) {
            return bn.apply(this);
        }
    }

    final class NumberNode implements Node {
        public final double value;

        public NumberNode(double value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return Utils.format(value);
        }

        @Override
        public Document toDocument() {
            return API.text(toString());
        }

        @Override
        public StringBuilder toString(StringBuilder sb) {
            return sb.append(toString());
        }

        @Override
        public <T> T match(
                F<NullNode, T> nln,
                F<BoolNode, T> bn,
                F<NumberNode, T> nmn,
                F<StringNode, T> sn,
                F<ArrayNode, T> an,
                F<ObjectNode, T> on) {
            return nmn.apply(this);
        }
    }

    final class StringNode implements Node {
        public final String value;

        public StringNode(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return Utils.format(value);
        }

        @Override
        public Document toDocument() {
            return API.text(toString());
        }

        @Override
        public StringBuilder toString(StringBuilder sb) {
            return Utils.format(value, sb);
        }

        @Override
        public <T> T match(
                F<NullNode, T> nln,
                F<BoolNode, T> bn,
                F<NumberNode, T> nmn,
                F<StringNode, T> sn,
                F<ArrayNode, T> an,
                F<ObjectNode, T> on) {
            return sn.apply(this);
        }
    }

    final class ArrayNode implements Node {
        public final IList<Node> values;

        public ArrayNode(IList<Node> values) {
            this.values = values;
        }

        @Override
        public String toString() {
            return toString(new StringBuilder()).toString();
        }

        @Override
        public Document toDocument() {
            return API.enclose(
                    API.text('['),
                    API.text(", "),
                    API.text(']'),
                    values.map(Node::toDocument)
            );
        }

        @Override
        public StringBuilder toString(StringBuilder sb) {
            sb.append('[');
            boolean first = true;
            for (Node n : values) {
                if (first) {
                    first = false;
                } else {
                    sb.append(',');
                }
                n.toString(sb);
            }
            return sb.append(']');
        }

        @Override
        public <T> T match(
                F<NullNode, T> nln,
                F<BoolNode, T> bn,
                F<NumberNode, T> nmn,
                F<StringNode, T> sn,
                F<ArrayNode, T> an,
                F<ObjectNode, T> on) {
            return an.apply(this);
        }
    }

    final class ObjectNode implements Node {
        public final LinkedHashMap<String, Node> fields;

        public ObjectNode(LinkedHashMap<String, Node> fields) {
            this.fields = fields;
        }

        @Override
        public String toString() {
            return toString(new StringBuilder()).toString();
        }

        @Override
        public Document toDocument() {
            return API.enclose(
                    API.text('{'),
                    API.text(", "),
                    API.text('}'),
                    Functors.map(Utils::toDoc, fields.entrySet())
            );
        }

        @Override
        public StringBuilder toString(StringBuilder sb) {
            sb.append('{');
            boolean first = true;
            for (Map.Entry<String, Node> en : fields.entrySet()) {
                if (first) {
                    first = false;
                } else {
                    sb.append(',');
                }
                Utils.format(en.getKey(), sb).append(':');
                en.getValue().toString(sb);
            }
            return sb.append('}');
        }

        @Override
        public <T> T match(
                F<NullNode, T> nln,
                F<BoolNode, T> bn,
                F<NumberNode, T> nmn,
                F<StringNode, T> sn,
                F<ArrayNode, T> an,
                F<ObjectNode, T> on) {
            return on.apply(this);
        }
    }

    Document toDocument();

    default String toJson(int width) {
        return DocWriter.format(width, toDocument());
    }

    StringBuilder toString(StringBuilder sb);

    <T> T match(
        F<NullNode, T> nln,
        F<BoolNode, T> bn,
        F<NumberNode, T> nmn,
        F<StringNode, T> sn,
        F<ArrayNode, T> an,
        F<ObjectNode, T> on
    );
}

class Utils {
    static Node duplicateKeyError(Node u, Node v) {
        throw new IllegalStateException("Duplicate keys");
    }

    static String format(double d) {
        final long ld = (long)d;
        if (d == ld) {
            return String.format("%d", ld);
        } else {
            return String.format("%s", d);
        }
    }

    static String format(String s) {
        return format(s, new StringBuilder()).toString();
    }

    static StringBuilder format(String s, StringBuilder sb) {
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
//                case '/':
//                    sb.append("\\/");
//                    break;
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

    static Document toDoc(Map.Entry<String, Node> field) {
        return API.concat(
                API.text("\"" + field.getKey() + "\""),
                API.text(" : "),
                field.getValue().toDocument()
        );
    }
}
