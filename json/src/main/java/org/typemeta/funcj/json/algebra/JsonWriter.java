package org.typemeta.funcj.json.algebra;

import org.typemeta.funcj.functions.SideEffect;
import org.typemeta.funcj.json.model.*;
import org.typemeta.funcj.util.Exceptions;

import java.io.Writer;
import java.util.*;

public class JsonWriter implements JsonAlg<SideEffect.F<Writer>> {
    public static final JsonWriter INSTANCE = new JsonWriter();

    /**
     * Write a JSON value to a {@link Writer}.
     * @param jv        the JSON value
     * @param w        the {@code Writer} to write the JSON to
     * @return          the string representation of formatted JSON
     */
    public static Writer toString(JsValue jv, Writer w) {
        jv.apply(INSTANCE).apply(w);
        return w;
    }

    @Override
    public SideEffect.F<Writer> nul() {
        return Exceptions.wrap(w -> {w.write(JsNull.NULL.toString());});
    }

    @Override
    public SideEffect.F<Writer> bool(boolean b) {
        return Exceptions.wrap(w -> {w.write(Boolean.toString(b));});
    }

    @Override
    public SideEffect.F<Writer> num(double d) {
        return Exceptions.wrap(w -> {w.write(Utils.format(d));});
    }

    @Override
    public SideEffect.F<Writer> str(String s) {
        return Exceptions.wrap(w -> {w.write(Utils.format(s));});
    }

    @Override
    public SideEffect.F<Writer> arr(List<SideEffect.F<Writer>> elems) {
        return Exceptions.wrap(w -> {
            w.append('[');
            boolean first = true;
            for (SideEffect.F<Writer> f : elems) {
                if (first) {
                    first = false;
                } else {
                    w.append(',');
                }
                f.apply(w);
            }
            w.append(']');
        });
    }

    @Override
    public SideEffect.F<Writer> obj(LinkedHashMap<String, SideEffect.F<Writer>> fields) {
        return Exceptions.wrap(w -> {
            w.append('{');
            boolean first = true;
            for (Map.Entry<String, SideEffect.F<Writer>> field : fields.entrySet()) {
                if (first) {
                    first = false;
                } else {
                    w.append(',');
                }
                Utils.format(field.getKey(), w).append(':');
                field.getValue().apply(w);
            }
            w.append('}');
        });
    }
}
