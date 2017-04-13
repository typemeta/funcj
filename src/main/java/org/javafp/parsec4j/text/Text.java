package org.javafp.parsec4j.text;

import org.javafp.parsec4j.SymSet;
import org.javafp.util.Chr;

import static org.javafp.parsec4j.text.Parser.*;

public class Text {
    public static final Parser<Chr> anyChar = any();

    public static Parser<Chr> chr(char c) {
        return satisfy(cc -> cc.equals(c));
    }

    public static Parser<Chr> notChr(char c) {
        return satisfy(cc -> !cc.equals(c));
    }

    public static final Parser<Chr> alpha = satisfy(Chr::isLetter);

    public static final Parser<Chr> digit = satisfy(Chr::isDigit);

    public static final Parser<Chr> alphaNum = satisfy(Chr::isLetterOrDigit);

    public static final Parser<Chr> ws = satisfy(Chr::isWhitespace);

    private static int digitToInt(Chr d) {
        return Chr.getNumericValue(d);
    }

    public static final Parser<Integer> uintr =
        many1(digit.map(Text::digitToInt))
            .map(l -> l.foldl1((acc, x) -> acc * 10 + x));

    public static final Parser<Integer> intr =
        chr('+').or(chr('-')).or(pure(Chr.valueOf('+')))
            .and(uintr)
            .map((sign, i) -> sign.charValue() == '+' ? i : -i);

    public static final Parser<Double> floating =
        many1(digit.map(Text::digitToInt))
            .map(l -> l.foldr((d, acc) -> d + acc / 10.0, 0.0) / 10.0);

    public static final Parser<Double> dble =
        intr.and(optional(chr('.').andR(floating)))
            .map((i, f) -> i.doubleValue() + f.orElse(0.0));

    public static Parser<String> string(String s) {
        switch (s.length()) {
            case 0: return fail();
            case 1: return chr(s.charAt(0)).map(Object::toString);
            default: {
                return new ParserImpl<String>() {
                    @Override
                    public boolean acceptsEmpty() {
                        return false;
                    }

                    @Override
                    public SymSet<Chr> firstSetCalc() {
                        return SymSet.value(Chr.valueOf(s.charAt(0)));
                    }

                    @Override
                    public Result<String> parse(Input in, int pos) {
                        int pos2 = pos;
                        for (int i = 0; i < s.length(); ++i) {
                            if (in.isEof(pos2) || in.at(pos2) != s.charAt(i)) {
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
