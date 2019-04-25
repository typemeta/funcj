package org.typemeta.funcj.json.algebras;

import org.typemeta.funcj.json.model.JsonAlg;

import java.util.LinkedHashMap;
import java.util.List;

public class JsonToJava implements JsonAlg<Object> {
    public static final JsonToJava INSTANCE = new JsonToJava();

    @Override
    public Object nul() {
        return null;
    }

    @Override
    public Object bool(boolean b) {
        return b;
    }

    @Override
    public Object num(double v) {
        return v;
    }

    @Override
    public Object str(String s) {
        return s;
    }

    @Override
    public Object arr(List<Object> list) {
        return list;
    }

    @Override
    public Object obj(LinkedHashMap<String, Object> map) {
        return map;
    }
}
