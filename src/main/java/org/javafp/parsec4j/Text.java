package org.javafp.parsec4j;

import static org.javafp.parsec4j.Parser.*;

public class Text {
    public static <CTX extends Parser.Context<Character>>
    Parser<Character, CTX, Character> anyChar() {
        return any();
    }

    public static <CTX extends Parser.Context<Character>>
    Parser<Character, CTX, Character> chr(char c) {
        return satisfy(cc -> cc == c);
    }

    public static <CTX extends Parser.Context<Character>>
    Parser<Character, CTX, Character> notChr(char c) {
        return satisfy(cc -> cc != c);
    }

    public static <CTX extends Parser.Context<Character>>
    Parser<Character, CTX, Character> alpha() {
        return satisfy(Character::isLetter);
    }

    public static <CTX extends Parser.Context<Character>>
    Parser<Character, CTX, Character> digit() {
        return satisfy(Character::isDigit);
    }

    public static <CTX extends Parser.Context<Character>>
    Parser<Character, CTX, Character> alphaNum() {
        return satisfy(Character::isLetterOrDigit);
    }

    public static <CTX extends Parser.Context<Character>>
    Parser<Character, CTX, Character> ws() {
        return satisfy(Character::isWhitespace);
    }

    private static int digitToInt(char d) {
        return Character.getNumericValue(d);
    }

    public static <CTX extends Parser.Context<Character>>
    Parser<Character, CTX, Integer> uintr() {
        return many1(Text.<CTX>digit().map(Text::digitToInt))
            .map(l -> l.foldl1((x, acc) -> x*10 + acc));
    }

    public static <CTX extends Parser.Context<Character>>
    Parser<Character, CTX, Integer> intr() {
        return Text.<CTX>chr('+').or(chr('-')).or(pure('+'))
            .and(uintr())
            .map((sign, i) -> sign == '+' ? i : -i);
    }

    public static <CTX extends Parser.Context<Character>>
    Parser<Character, CTX, Double> floating() {
        return many1(Text.<CTX>digit().map(Text::digitToInt))
            .map(l -> l.foldr((d, acc) -> d + acc / 10.0, 0.0) / 10.0);
    }

    public static <CTX extends Parser.Context<Character>>
    Parser<Character, CTX, Double> dble() {
        return Text.<CTX>intr().and(optional(Text.<CTX>chr('.').andR(floating())))
            .map((i, f) -> i.doubleValue() + f.orElse(0.0));
    }

    public static <CTX extends Parser.Context<Character>>
    Parser<Character, CTX, String> string(String s) {
        switch (s.length()) {
            case 0: return fail();
            case 1: return Text.<CTX>chr(s.charAt(0)).map(Object::toString);
            default: {
                return new Parser<Character, CTX, String>() {
                    @Override public Result<Character, String> parse(CTX ctx, int pos) {
                        int pos2 = pos;
                        for (int i = 0; i < s.length(); ++i) {
                            if (ctx.input().isEof(pos2) || ctx.input().at(pos2).charValue() != s.charAt(i)) {
                                return Result.failure(pos);
                            } else {
                                pos2 = pos2+1;
                            }
                        }

                        return Result.success(s, pos2);
                    }

                    @Override public boolean accepts(Character token) {
                        return s.charAt(0) == token;
                    }
                };
            }
        }
    }
}
