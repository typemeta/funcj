package org.typemeta.funcj.jsonp.algebras;

import org.typemeta.funcj.jsonp.algebras.JsonAlg;

import javax.json.JsonValue;

public class JsonAlgTransformTest {
    static class Copy extends JsonAlg.Transform.Base<JsonValue> {

        public Copy(JsonAlg<JsonValue> alg) {
            super(alg);
        }
    }
}
