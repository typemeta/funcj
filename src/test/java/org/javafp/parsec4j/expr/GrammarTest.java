package org.javafp.parsec4j.expr;

import org.javafp.util.Unit;
import org.junit.*;

public class GrammarTest {

    static  {
        System.out.println("Initialising...");
        Grammar.parser.acceptsEmpty();
        Grammar.parser.firstSet();
    }

    private static void assertSuccess(String s, String expected) {
        final String result = Grammar.parse(s).getOrThrow().toString();
        Assert.assertEquals(expected, result);
        Assert.assertEquals(expected, Grammar.parse(result).getOrThrow().toString());
    }

    private static void assertFailure(String s, String position) {
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
    public void testSuccess() throws Exception {
        assertSuccess(
            "123.456*max(4%+(5bp+x),-2bp)-1",
            "((123.456*max((4.0%+(5.0bp+x)),-2.0bp))-1.0)");
    }

    @Test
    public void testFailure() throws Exception {
        assertFailure("3*max(4%+(5bp+),-2bp)-1", "14");
    }
}
