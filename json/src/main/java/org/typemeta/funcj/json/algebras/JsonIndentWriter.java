package org.typemeta.funcj.json.algebras;

import org.typemeta.funcj.functions.SideEffect;
import org.typemeta.funcj.json.model.*;
import org.typemeta.funcj.util.Exceptions;

import java.io.Writer;
import java.util.*;

public class JsonIndentWriter implements JsonAlg<SideEffect.F2<Integer, Writer>> {
    private static String generate(int n) {
        final StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; ++i) {
            sb.append(" ");
        }
        return sb.toString();
    }

    private static final int INDENT_LEN = 2;
    private static final int INITIAL_CACHE_SIZE = 4;
    private static final String EOL = System.lineSeparator();

    private final List<String> indentCache;

    public JsonIndentWriter(int indentMult) {
        this.indentCache = new ArrayList<String>(INITIAL_CACHE_SIZE);
        for (int i = 1; i <= INITIAL_CACHE_SIZE; ++i) {
            this.indentCache.add(generate(indentMult * i));
        }
    }

    private String indent(int depth) {
        if (depth == 0) {
            return "";
        } else {
            final int d2 = depth - 1;
            if (d2 >= indentCache.size()) {
                for (int i = indentCache.size(); i <= d2; ++i) {
                    indentCache.add(indentCache.get(i - 1) + indentCache.get(0));
                }
            }
            return indentCache.get(d2);
        }
    }


    /**
     * Write a JSON value to a {@link Writer}.
     * @param jv        the JSON value
     * @param w        the {@code Writer} to write the JSON to
     * @return          the string representation of formatted JSON
     */
    public static Writer toString(JsValue jv, Writer w, int indent) {
        jv.apply(new JsonIndentWriter(indent)).apply(0, w);
        return w;
    }

    @Override
    public SideEffect.F2<Integer, Writer> nul() {
        return (Integer depth, Writer wtr) -> {
            Exceptions.wrap(() -> {
                wtr.write(JsNull.NULL.toString());
            });
        };
    }

    @Override
    public SideEffect.F2<Integer, Writer> bool(boolean b) {
        return (Integer depth, Writer wtr) -> {
            Exceptions.wrap(() -> {
                wtr.write(Boolean.toString(b));
            });
        };
    }

    @Override
    public SideEffect.F2<Integer, Writer> num(double d) {
        return (Integer depth, Writer wtr) -> {
            Exceptions.wrap(() -> {
                wtr.write(Utils.format(d));
            });
        };
    }

    @Override
    public SideEffect.F2<Integer, Writer> str(String s) {
        return (Integer depth, Writer wtr) -> {
            Exceptions.wrap(() -> {
                wtr.write(Utils.format(s));
            });
        };
    }

    @Override
    public SideEffect.F2<Integer, Writer> arr(List<SideEffect.F2<Integer, Writer>> elems) {
        return (Integer depth, Writer wtr) -> {
            final int d2 = depth + 1;
            Exceptions.wrap(() -> {
                wtr.append('[');
                boolean first = true;
                for (SideEffect.F2<Integer, Writer> f : elems) {
                    if (first) {
                        first = false;
                    } else {
                        wtr.append(',');
                    }
                    wtr.append(EOL).append(indent(d2));
                    f.apply(d2, wtr);
                }
                wtr.append(EOL);
                wtr.append(indent(depth));
                wtr.append(']');
            });
        };
    }

    @Override
    public SideEffect.F2<Integer, Writer> obj(LinkedHashMap<String, SideEffect.F2<Integer, Writer>> fields) {
        return (Integer depth, Writer wtr) -> {
            final int d2 = depth + 1;
            Exceptions.wrap(() -> {
                wtr.append('{');
                boolean first = true;
                for (Map.Entry<String, SideEffect.F2<Integer, Writer>> field : fields.entrySet()) {
                    if (first) {
                        first = false;
                    } else {
                        wtr.append(',');
                    }
                    wtr.append(EOL).append(indent(d2));
                    Utils.format(field.getKey(), wtr).append(':').append(" ");
                    field.getValue().apply(d2, wtr);
                }
                wtr.append(EOL);
                wtr.append(indent(depth));
                wtr.append('}');
            });
        };
    }
}
