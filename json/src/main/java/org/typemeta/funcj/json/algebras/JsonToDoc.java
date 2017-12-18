package org.typemeta.funcj.json.algebras;

import org.typemeta.funcj.document.*;
import org.typemeta.funcj.json.model.*;
import org.typemeta.funcj.json.model.Utils;

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
    public static String toString(JsValue jv, int width) {
        return DocFormat.format(width, jv.apply(new JsonToDoc()));
    }

    @Override
    public Document nul() {
        return API.text(JsNull.NULL.toString());
    }

    @Override
    public Document bool(boolean b) {
        return API.text(Boolean.toString(b));
    }

    @Override
    public Document num(Object value) {
        return API.text(Utils.formatAsNumber(value));
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
    public Document obj(LinkedHashMap<String, Document> fields) {
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
