package org.typemeta.funcj.jsonp.algebras;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.typemeta.funcj.jsonp.Example.testValue;

public class JsonToStringTest {
    @Test
    public void computeNodeAsPrimes() {
        final String str = JsonToString.toString(testValue);
        final String exp = "{\"bools\":[true,false],\"null\":null,\"numbers\":[1.2,3.4,4.5]," +
                "\"objects\":[{\"a\":1,\"b\":2},{\"c\":3,\"d\":4}],\"strings\":[\"abcd\",\"efgh\",\"ijkl\"]}";
        assertEquals("JSON to string", exp, str);
    }
}
