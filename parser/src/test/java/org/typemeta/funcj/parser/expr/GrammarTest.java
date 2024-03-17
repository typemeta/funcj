package org.typemeta.funcj.parser.expr;

import org.junit.*;

public class GrammarTest {

    static  {
        Grammar.parser.acceptsEmpty();
        Grammar.parser.firstSet();
    }

    private static void assertSuccess(String s, String expected) {
        final String result = Grammar.parse(s).getOrThrow().toString();
        Assert.assertEquals(expected, result);
        //System.out.println(result);
        Assert.assertEquals(expected, Grammar.parse(result).getOrThrow().toString());
    }

    private static void assertFailure(String s, Object position) {
        Grammar.parse(s).handle(
            ok -> {
                throw new RuntimeException("Expected parse to fail");
            },
            error -> {
                Assert.assertEquals("", error.input().position(), position);
            }
        );
    }

    @Test
    public void testSuccess() {
        assertSuccess(
            "123.456+4*(5+x)-1",
            "((123.456+(4.0*(5.0+x)))-1.0)");
    }

    @Test
    public void testFailure() {
        assertFailure("3*4+(5+)-1", 7);
    }
}
