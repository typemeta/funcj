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
        return new ArrayNode(IList.ofArray(values));
    }

    static ArrayNode array(Iterable<Node> values) {
        return new ArrayNode(IList.ofIterable(values));
    }

    static Tuple2<String, Node> entry(String name, Node node) {
        return Tuple2.of(name, node);
    }

    static ObjectNode object(Tuple2<String, Node>... fields) {
        return object(IList.ofArray(fields));
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

        @Override
        public boolean isNull() {
            return true;
        }

        @Override
        public boolean isBool() {
            return false;
        }

        @Override
        public boolean isNumber() {
            return false;
        }

        @Override
        public boolean isString() {
            return false;
        }

        @Override
        public boolean isArray() {
            return false;
        }

        @Override
        public boolean isObject() {
            return false;
        }

        @Override
        public NullNode asNull() {
            return this;
        }

        @Override
        public BoolNode asBool() {
            throw new RuntimeException(getClass().getSimpleName() + " is not the expected node type");
        }

        @Override
        public NumberNode asNumber() {
            throw new RuntimeException(getClass().getSimpleName() + " is not the expected node type");
        }

        @Override
        public StringNode asString() {
            throw new RuntimeException(getClass().getSimpleName() + " is not the expected node type");
        }

        @Override
        public ArrayNode asArray() {
            throw new RuntimeException(getClass().getSimpleName() + " is not the expected node type");
        }

        @Override
        public ObjectNode asObject() {
            throw new RuntimeException(getClass().getSimpleName() + " is not the expected node type");
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

        @Override
        public boolean isNull() {
            return false;
        }

        @Override
        public boolean isBool() {
            return true;
        }

        @Override
        public boolean isNumber() {
            return false;
        }

        @Override
        public boolean isString() {
            return false;
        }

        @Override
        public boolean isArray() {
            return false;
        }

        @Override
        public boolean isObject() {
            return false;
        }

        @Override
        public NullNode asNull() {
            throw new RuntimeException(getClass().getSimpleName() + " is not the expected node type");
        }

        @Override
        public BoolNode asBool() {
            return this;
        }

        @Override
        public NumberNode asNumber() {
            throw new RuntimeException(getClass().getSimpleName() + " is not the expected node type");
        }

        @Override
        public StringNode asString() {
            throw new RuntimeException(getClass().getSimpleName() + " is not the expected node type");
        }

        @Override
        public ArrayNode asArray() {
            throw new RuntimeException(getClass().getSimpleName() + " is not the expected node type");
        }

        @Override
        public ObjectNode asObject() {
            throw new RuntimeException(getClass().getSimpleName() + " is not the expected node type");
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
        public boolean equals(Object rhs) {
            if (this == rhs) {
                return true;
            }
            if (rhs == null || getClass() != rhs.getClass()) {
                return false;
            }
            final NumberNode that = (NumberNode) rhs;
            return Double.compare(that.value, value) == 0;
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
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

        @Override
        public boolean isNull() {
            return false;
        }

        @Override
        public boolean isBool() {
            return false;
        }

        @Override
        public boolean isNumber() {
            return true;
        }

        @Override
        public boolean isString() {
            return false;
        }

        @Override
        public boolean isArray() {
            return false;
        }

        @Override
        public boolean isObject() {
            return false;
        }

        @Override
        public NullNode asNull() {
            throw new RuntimeException(getClass().getSimpleName() + " is not the expected node type");
        }

        @Override
        public BoolNode asBool() {
            throw new RuntimeException(getClass().getSimpleName() + " is not the expected node type");
        }

        @Override
        public NumberNode asNumber() {
            return this;
        }

        @Override
        public StringNode asString() {
            throw new RuntimeException(getClass().getSimpleName() + " is not the expected node type");
        }

        @Override
        public ArrayNode asArray() {
            throw new RuntimeException(getClass().getSimpleName() + " is not the expected node type");
        }

        @Override
        public ObjectNode asObject() {
            throw new RuntimeException(getClass().getSimpleName() + " is not the expected node type");
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
        public boolean equals(Object rhs) {
            if (this == rhs) {
                return true;
            }
            if (rhs == null || getClass() != rhs.getClass()) {
                return false;
            }
            final StringNode that = (StringNode) rhs;
            return value.equals(that.value);
        }

        @Override
        public int hashCode() {
            return value.hashCode();
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

        @Override
        public boolean isNull() {
            return false;
        }

        @Override
        public boolean isBool() {
            return false;
        }

        @Override
        public boolean isNumber() {
            return false;
        }

        @Override
        public boolean isString() {
            return true;
        }

        @Override
        public boolean isArray() {
            return false;
        }

        @Override
        public boolean isObject() {
            return false;
        }

        @Override
        public NullNode asNull() {
            throw new RuntimeException(getClass().getSimpleName() + " is not the expected node type");
        }

        @Override
        public BoolNode asBool() {
            throw new RuntimeException(getClass().getSimpleName() + " is not the expected node type");
        }

        @Override
        public NumberNode asNumber() {
            throw new RuntimeException(getClass().getSimpleName() + " is not the expected node type");
        }

        @Override
        public StringNode asString() {
            return this;
        }

        @Override
        public ArrayNode asArray() {
            throw new RuntimeException(getClass().getSimpleName() + " is not the expected node type");
        }

        @Override
        public ObjectNode asObject() {
            throw new RuntimeException(getClass().getSimpleName() + " is not the expected node type");
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
        public boolean equals(Object rhs) {
            if (this == rhs) {
                return true;
            }
            if (rhs == null || getClass() != rhs.getClass()) {
                return false;
            }
            final ArrayNode that = (ArrayNode) rhs;
            return values.equals(that.values);
        }

        @Override
        public int hashCode() {
            return values.hashCode();
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

        @Override
        public boolean isNull() {
            return false;
        }

        @Override
        public boolean isBool() {
            return false;
        }

        @Override
        public boolean isNumber() {
            return false;
        }

        @Override
        public boolean isString() {
            return false;
        }

        @Override
        public boolean isArray() {
            return true;
        }

        @Override
        public boolean isObject() {
            return false;
        }

        @Override
        public NullNode asNull() {
            throw new RuntimeException(getClass().getSimpleName() + " is not the expected node type");
        }

        @Override
        public BoolNode asBool() {
            throw new RuntimeException(getClass().getSimpleName() + " is not the expected node type");
        }

        @Override
        public NumberNode asNumber() {
            throw new RuntimeException(getClass().getSimpleName() + " is not the expected node type");
        }

        @Override
        public StringNode asString() {
            throw new RuntimeException(getClass().getSimpleName() + " is not the expected node type");
        }

        @Override
        public ArrayNode asArray() {
            return this;
        }

        @Override
        public ObjectNode asObject() {
            throw new RuntimeException(getClass().getSimpleName() + " is not the expected node type");
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
        public boolean equals(Object rhs) {
            if (this == rhs) {
                return true;
            }
            if (rhs == null || getClass() != rhs.getClass()) {
                return false;
            }
            final ObjectNode that = (ObjectNode) rhs;
            return fields.equals(that.fields);
        }

        @Override
        public int hashCode() {
            return fields.hashCode();
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

        @Override
        public boolean isNull() {
            return false;
        }

        @Override
        public boolean isBool() {
            return false;
        }

        @Override
        public boolean isNumber() {
            return false;
        }

        @Override
        public boolean isString() {
            return false;
        }

        @Override
        public boolean isArray() {
            return false;
        }

        @Override
        public boolean isObject() {
            return true ;
        }

        @Override
        public NullNode asNull() {
            throw new RuntimeException(getClass().getSimpleName() + " is not the expected node type");
        }

        @Override
        public BoolNode asBool() {
            throw new RuntimeException(getClass().getSimpleName() + " is not the expected node type");
        }

        @Override
        public NumberNode asNumber() {
            throw new RuntimeException(getClass().getSimpleName() + " is not the expected node type");
        }

        @Override
        public StringNode asString() {
            throw new RuntimeException(getClass().getSimpleName() + " is not the expected node type");
        }

        @Override
        public ArrayNode asArray() {
            throw new RuntimeException(getClass().getSimpleName() + " is not the expected node type");
        }

        @Override
        public ObjectNode asObject() {
            return this;
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

    boolean isNull();
    boolean isBool();
    boolean isNumber();
    boolean isString();
    boolean isArray();
    boolean isObject();

    NullNode asNull();
    BoolNode asBool();
    NumberNode asNumber();
    StringNode asString();
    ArrayNode asArray();
    ObjectNode asObject();

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
