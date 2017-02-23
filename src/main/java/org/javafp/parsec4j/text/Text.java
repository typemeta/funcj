package org.javafp.parsec4j.text;

import org.javafp.util.Chr;

import static org.javafp.parsec4j.text.Parser.*;

public class Text {
    public static Parser<Chr> anyChar() {
        return any();
    }

    public static Parser<Chr> chr(char c) {
        return satisfy(cc -> cc.equals(c));
    }

    public static Parser<Chr> notChr(char c) {
        return satisfy(cc -> !cc.equals(c));
    }

    public static Parser<Chr> alpha() {
        return satisfy(Chr::isLetter);
    }

    public static Parser<Chr> digit() {
        return satisfy(Chr::isDigit);
    }

    public static Parser<Chr> alphaNum() {
        return satisfy(Chr::isLetterOrDigit);
    }

    public static Parser<Chr> ws() {
        return satisfy(Chr::isWhitespace);
    }

    private static int digitToInt(Chr d) {
        return Chr.getNumericValue(d);
    }

    public static Parser<Integer> uintr() {
        return many1(digit().map(Text::digitToInt))
            .map(l -> l.foldl1((x, acc) -> x*10 + acc));
    }

    public static Parser<Integer> intr() {
        return chr('+').or(chr('-')).or(pure(Chr.valueOf('+')))
            .and(uintr())
            .map((sign, i) -> sign.charValue() == '+' ? i : -i);
    }

    public static Parser<Double> floating() {
        return many1(digit().map(Text::digitToInt))
            .map(l -> l.foldr((d, acc) -> d + acc / 10.0, 0.0) / 10.0);
    }

    public static Parser<Double> dble() {
        return intr().and(optional(chr('.').andR(floating())))
            .map((i, f) -> i.doubleValue() + f.orElse(0.0));
    }

    public static Parser<String> string(String s) {
        switch (s.length()) {
            case 0: return fail();
            case 1: return chr(s.charAt(0)).map(Object::toString);
            default: {
                return (input, pos) -> {
                    int pos2 = pos;
                    for (int i = 0; i < s.length(); ++i) {
                        if (input.isEof(pos2) || input.at(pos2) != s.charAt(i)) {
                            return Result.failure(pos);
                        } else {
                            pos2 = pos2+1;
                        }
                    }

                    return Result.success(s, pos2);
                };
            }
        }
    }
}