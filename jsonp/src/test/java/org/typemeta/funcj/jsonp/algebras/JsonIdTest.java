package org.typemeta.funcj.jsonp.algebras;

import org.junit.Test;
import org.typemeta.funcj.jsonp.Example;

import javax.json.JsonValue;

import static org.junit.Assert.assertEquals;

public class JsonIdTest {
    @Test
    public void test() {
        final JsonValue copy = JsonAlgStack.apply(Example.testValue, new JsonId());
        copy.equals(Example.testValue);
        assertEquals("JsonId generates identical copy ", Example.testValue, copy);
    }
}
