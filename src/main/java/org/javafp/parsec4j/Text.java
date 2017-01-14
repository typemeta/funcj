package org.javafp.parsec4j;

import static org.javafp.parsec4j.Parsers.*;

public class Text {
    public static P<Character, Character> anyChar = any();

    public static P<Character, Character> chr(char c) {
        return satisfy(cc -> cc == c);
    }

    public static P<Character, Character> notChr(char c) {
        return satisfy(cc -> cc != c);
    }

    public static final P<Character, Character> alpha =
        satisfy(Character::isLetter);

    public static final P<Character, Character> digit =
        satisfy(Character::isDigit);

    public static final P<Character, Character> alphaNum =
        satisfy(Character::isLetterOrDigit);

    public static final P<Character, Character> ws =
        satisfy(Character::isWhitespace);

    private static int digitToInt(char d) {
        return Character.getNumericValue(d);
    }

    public static final P<Character, Integer> uintr =
        many1(digit.map(Text::digitToInt))
            .map(l -> l.foldl1((x, acc) -> x*10 + acc));

    public static final P<Character, Integer> intr =
        chr('+').or(chr('-')).or(pure('+'))
            .and(uintr)
            .map((sign, i) -> sign == '+' ? i : -i);

    private static final P<Character, Double> floating =
        many1(digit.map(Text::digitToInt))
            .map(l -> l.foldr((d, acc) -> d + acc/10.0, 0.0) / 10.0);

    public static P<Character, Double> dble =
        intr.and(optional(chr('.').andR(floating)))
            .map((i, f) -> i.doubleValue() + f.orElse(0.0));

    public static P<Character, String> string(String s) {
        switch (s.length()) {
            case 0: return fail();
            case 1: return chr(s.charAt(0)).map(Object::toString);
            default: {
                return new P<Character, String>() {
                    @Override public Result<Character, String> parse(Input<Character> input) {
                        Input<Character> input2 = input;
                        for (int i = 0; i < s.length(); ++i) {
                            if (input2.isEof() || input2.head().charValue() != s.charAt(i)) {
                                return Result.failure(input);
                            } else {
                                input2 = input2.tail();
                            }
                        }

                        return Result.success(s, input2);
                    }

                    @Override public boolean accepts(Character token) {
                        return s.charAt(0) == token;
                    }
                };
            }
        }
    }
}
