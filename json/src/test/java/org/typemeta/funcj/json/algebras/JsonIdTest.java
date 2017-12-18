package org.typemeta.funcj.json.algebras;

import org.junit.Test;
import org.typemeta.funcj.json.Example;
import org.typemeta.funcj.json.model.JsValue;

import static org.junit.Assert.assertEquals;

public class JsonIdTest {
    @Test
    public void test() {
        final JsValue copy = Example.testValue.apply(new JsonId());
        assertEquals("JsonId generates identical copy ", Example.testValue, copy);
    }
}
