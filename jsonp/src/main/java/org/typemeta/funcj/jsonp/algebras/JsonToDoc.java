package org.typemeta.funcj.jsonp.algebras;

import org.typemeta.funcj.document.*;

import javax.json.*;
import java.math.BigDecimal;
import java.util.*;

import static java.util.stream.Collectors.toList;
import static org.typemeta.funcj.util.Streams.tupleStream;

public class JsonToDoc implements JsonAlg<Document> {
    /**
     * Pretty-print a JSON value as a JSON string.
     * @param jv        the JSON value
     * @param width     the maximum line length
     * @return          the string representation of formatted JSON
     */
    public static String toString(JsonValue jv, int width) {
        return DocFormat.format(width, JsonAlgStack.apply(jv, new JsonToDoc()));
    }

    @Override
    public Document nul() {
        return API.text(JsonValue.NULL.toString());
    }

    @Override
    public Document bool(boolean b) {
        return API.text(Boolean.toString(b));
    }

    @Override
    public Document num(int value) {
        return API.text(Utils.format(value));
    }

    @Override
    public Document num(double value) {
        return API.text(Utils.format(value));
    }

    @Override
    public Document num(BigDecimal value) {
        return API.text(Utils.format(value));
    }

    @Override
    public Document str(String s) {
        return API.text(Utils.format(s));
    }

    @Override
    public Document arr(List<Document> elems) {
        return API.enclose(
                API.text('['),
                API.text(", "),
                API.text(']'),
                elems
        );
    }

    @Override
    public Document obj(Map<String, Document> fields) {
        return API.enclose(
                API.text('{'),
                API.text(", "),
                API.text('}'),
                tupleStream(fields).map(fld -> fld.applyFrom(JsonToDoc::fieldToDoc)).collect(toList())
        );
    }

    private static Document fieldToDoc(String name, Document value) {
        return API.concat(
                API.text(Utils.format(name)),
                API.text(": "),
                value);
    }
}
