package org.typemeta.funcj.json.algebras;

import org.typemeta.funcj.document.API;
import org.typemeta.funcj.document.Document;
import org.typemeta.funcj.functions.SideEffect;
import org.typemeta.funcj.json.model.*;

import java.util.*;

public class JsonToString implements JsonAlg<SideEffect.F<StringBuilder>> {
    public static final JsonToString INSTANCE = new JsonToString();

    /**
     * Write a JSON value to a {@link StringBuilder}.
     * @param jv        the JSON value
     * @param sb        the {@code StringBuilder} to write the JSON to
     * @return          the string representation of formatted JSON
     */
    public static StringBuilder toString(JsValue jv, StringBuilder sb) {
        jv.apply(new JsonToString()).apply(sb);
        return sb;
    }

    @Override
    public SideEffect.F<StringBuilder> nul() {
        return sb -> sb.append(JsNull.NULL.toString());
    }

    @Override
    public SideEffect.F<StringBuilder> bool(boolean b) {
        return sb -> sb.append(b);
    }

    @Override
    public SideEffect.F<StringBuilder> num(double value) {
        return sb -> sb.append(Utils.format(value));
    }

    @Override
    public SideEffect.F<StringBuilder> str(String s) {
        return sb -> sb.append(Utils.format(s));
    }

    @Override
    public SideEffect.F<StringBuilder> arr(List<SideEffect.F<StringBuilder>> elems) {
        return sb -> {
            sb.append('[');
            boolean first = true;
            for (SideEffect.F<StringBuilder> f : elems) {
                if (first) {
                    first = false;
                } else {
                    sb.append(',');
                }
                f.apply(sb);
            }
            sb.append(']');
        };
    }

    @Override
    public SideEffect.F<StringBuilder> obj(LinkedHashMap<String, SideEffect.F<StringBuilder>> fields) {
        return sb -> {
            sb.append('{');
            boolean first = true;
            for (Map.Entry<String, SideEffect.F<StringBuilder>> field : fields.entrySet()) {
                if (first) {
                    first = false;
                } else {
                    sb.append(',');
                }
                Utils.format(field.getKey(), sb).append(':');
                field.getValue().apply(sb);
            }
            sb.append('}');
        };
    }

    private static Document fieldToDoc(String name, Document value) {
        return API.concat(
                API.text(Utils.format(name)),
                API.text(": "),
                value);
    }
}
