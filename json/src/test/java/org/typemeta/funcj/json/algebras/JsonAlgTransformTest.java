package org.typemeta.funcj.json.algebras;

import org.typemeta.funcj.json.model.JsValue;
import org.typemeta.funcj.json.model.JsonAlg;

public class JsonAlgTransformTest {
    static class Copy extends JsonAlg.Transform.Base<JsValue> {

        public Copy(JsonAlg<JsValue> alg) {
            super(alg);
        }
    }
}
