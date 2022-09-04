package org.typemeta.funcj.parser.enumexpr;

import org.junit.*;

public class GrammarTest {

    static  {
        Grammar.parser.acceptsEmpty();
        Grammar.parser.firstSet();
    }

    private static void assertSuccess(Token[] tokens, String expected) {
        final String result = Grammar.parse(tokens).getOrThrow().toString();
        Assert.assertEquals(expected, result);
        //System.out.println(result);
        //Assert.assertEquals(expected, Grammar.parse(result).getOrThrow().toString());
    }

    private static void assertFailure(Token[] tokens, Object position) {
        Grammar.parse(tokens).handle(
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
                new Token[]{
                        Token.number(123.456),
                        Token.MULT,
                        Token.number(4),
                        Token.PLUS,
                        Token.OPEN,
                        Token.number(5),
                        Token.PLUS,
                        Token.variable('x'),
                        Token.CLOSE,
                        Token.MINUS,
                        Token.number(1)
                },
            "(((123.456*4.0)+(5.0+x))-1.0)");
    }

    @Test
    public void testFailure() {
        assertFailure(
                new Token[]{
                        Token.number(3),
                        Token.MULT,
                        Token.number(4),
                        Token.PLUS,
                        Token.OPEN,
                        Token.number(5),
                        Token.PLUS,
                        Token.CLOSE,
                        Token.MINUS,
                        Token.number(1)
                },
                7
        );
    }
}
