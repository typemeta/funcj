package org.typemeta.funcj.json.algebras;

import org.typemeta.funcj.json.model.*;

public class JsonAlgTransformTest {
    static class Copy extends JsonAlg.Transform.Base<JsValue> {

        public Copy(JsonAlg<JsValue> alg) {
            super(alg);
        }
    }
}
