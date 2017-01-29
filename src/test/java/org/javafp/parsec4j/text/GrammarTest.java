package org.javafp.parsec4j.text;

import org.javafp.util.Unit;
import org.junit.*;

public class GrammarTest {

    private static void assertSuccess(String s, String expected) throws Exception {
        final String result = Grammar.parse(s).getOrThrow().toString();
        Assert.assertEquals(expected, result);
        Assert.assertEquals(expected, Grammar.parse(result).getOrThrow().toString());
    }

    private static void assertFailure(String s) {
        Grammar.parse(s).match(
            ok -> {
                throw new RuntimeException("Expected parse to fail");
            },
            error -> Unit.UNIT
        );
    }

    @Test
    public void testSuccess() throws Exception {
        assertSuccess("3*-max(4%+(5bp+-x),-2bp)-1", "(3.0*-((max((4.0%+(5.0bp+-(x))),-2.0bp)-1.0)))");
    }

    @Test
    public void testFailure() throws Exception {
        assertFailure("3*-max(4%+(5bp+),-2bp)-1");
    }
}
