package org.typemeta.funcj.json.algebra;

public class JsonAlgTransBase<T> implements JsonAlg.Transform<T> {
    protected final JsonAlg<T> alg;

    public JsonAlgTransBase(JsonAlg<T> alg) {
        this.alg = alg;
    }

    @Override
    public JsonAlg<T> alg() {
        return alg;
    }
}
