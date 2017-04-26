package org.javafp.parsec4j;

import org.javafp.util.Chr;

import static org.javafp.parsec4j.Parser.*;

public class Text {
    public static final Parser<Chr, Chr> anyChar = any();

    public static Parser<Chr, Chr> chr(char c) {
        return value(Chr.valueOf(c));
    }

    public static final Parser<Chr, Chr> alpha = satisfy("letter", Chr::isLetter);

    public static final Parser<Chr, Chr> digit = satisfy("digit", Chr::isDigit);

    public static final Parser<Chr, Chr> alphaNum = satisfy("letterOrDigit", Chr::isLetterOrDigit);

    public static final Parser<Chr, Chr> ws = satisfy("ws", Chr::isWhitespace);

    private static int digitToInt(char c) {
        return Chr.getNumericValue(c);
    }

    private static int digitToInt(Chr c) {
        return Chr.getNumericValue(c.charValue());
    }

    public static final Parser<Chr, Integer> uintr =
        many1(digit.map(Text::digitToInt))
            .map(l -> l.foldLeft1((acc, x) -> acc * 10 + x));

    public static final Parser<Chr, Integer> intr =
        choice(
            chr('+'),
            chr('-'),
            pure(Chr.valueOf('+'))
        ).and(uintr)
            .map((sign, i) -> sign.charValue() == '+' ? i : -i);

    public static final Parser<Chr, Double> floating =
        many(digit.map(Text::digitToInt))
            .map(l -> l.foldRight((d, acc) -> d + acc / 10.0, 0.0) / 10.0);

    public static final Parser<Chr, Double> dble =
        intr.and(optional(chr('.')
                .andR(floating)))
            .map((i, f) -> i.doubleValue() + f.orElse(0.0));

    public static Parser<Chr, String> string(String s) {
        switch (s.length()) {
            case 0: return fail();
            case 1: return chr(s.charAt(0)).map(Object::toString);
            default: {
                return new ParserImpl<Chr, String>(
                    () -> false,
                    () -> SymSet.value(Chr.valueOf(s.charAt(0)))
                ) {
                    @Override
                    public Result<Chr, String> parse(Input<Chr> in, int pos, SymSet<Chr> follow) {
                        int pos2 = pos;
                        for (int i = 0; i < s.length(); ++i) {
                            if (in.isEof(pos2) || !in.at(pos2).equals(s.charAt(i))) {
                                return Result.failure(pos);
                            } else {
                                pos2 = pos2+1;
                            }
                        }

                        return Result.success(s, pos2);
                    }
                };
            }
        }
    }
}
