package org.javafp.parsec4j.text;

import static org.javafp.parsec4j.text.Parser.*;

public class Text {
    public static Parser<Character> anyChar() {
        return any();
    }

    public static Parser<Character> chr(char c) {
        return satisfy(cc -> cc == c);
    }

    public static Parser<Character> notChr(char c) {
        return satisfy(cc -> cc != c);
    }

    public static Parser<Character> alpha() {
        return satisfy(Character::isLetter);
    }

    public static Parser<Character> digit() {
        return satisfy(Character::isDigit);
    }

    public static Parser<Character> alphaNum() {
        return satisfy(Character::isLetterOrDigit);
    }

    public static Parser<Character> ws() {
        return satisfy(Character::isWhitespace);
    }

    private static int digitToInt(char d) {
        return Character.getNumericValue(d);
    }

    public static Parser<Integer> uintr() {
        return many1(digit().map(Text::digitToInt))
            .map(l -> l.foldl1((x, acc) -> x*10 + acc));
    }

    public static Parser<Integer> intr() {
        return chr('+').or(chr('-')).or(pure('+'))
            .and(uintr())
            .map((sign, i) -> sign == '+' ? i : -i);
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
