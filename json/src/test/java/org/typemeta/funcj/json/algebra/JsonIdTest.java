package org.typemeta.funcj.json.algebra;

import org.junit.Test;
import org.typemeta.funcj.json.TestData;
import org.typemeta.funcj.json.model.JsValue;

import static org.junit.Assert.assertEquals;

public class JsonIdTest {
    @Test
    public void test() {
        final JsValue copy = TestData.testValue.apply(JsonId.INSTANCE);
        assertEquals("JsonId generates identical copy ", TestData.testValue, copy);
    }
}
