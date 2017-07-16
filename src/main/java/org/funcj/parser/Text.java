package org.funcj.parser;

import org.funcj.data.Chr;

import static org.funcj.parser.Parser.*;

/**
 * Parser combinators for working with Chr streams.
 */
public abstract class Text {
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

    private static final Parser<Chr, Boolean> sign =
            choice(
                    chr('+').andR(pure(true)),
                    chr('-').andR(pure(false)),
                    pure(true)
            );

    public static final Parser<Chr, Integer> uintr =
            many1(digit.map(Text::digitToInt))
                    .map(l -> l.foldLeft1((acc, x) -> acc * 10 + x));

    public static final Parser<Chr, Integer> intr =
            sign.and(uintr)
                    .map((sign, i) -> sign ? i : -i);

    public static final Parser<Chr, Long> ulng =
            many1(digit.map(c -> (long)digitToInt(c)))
                    .map(l -> l.foldLeft1((acc, x) -> acc * 10 + x));

    public static final Parser<Chr, Long> lng =
            sign.and(ulng)
                    .map((sign, i) -> sign ? i : -i);

    private static final Parser<Chr, Double> floating =
            many(digit.map(Text::digitToInt))
                    .map(l -> l.foldRight((d, acc) -> d + acc / 10.0, 0.0) / 10.0);

    private static final Parser<Chr, Integer> expnt =
            (chr('e').or(chr('E')))
                    .andR(intr);

    public static final Parser<Chr, Double> dble =
            sign.and(ulng).and(optional(chr('.').andR(floating)))
                    .and(optional(expnt))
                    .map((sn, i, f, exp) -> {
                        double r = i.doubleValue();
                        if (f.isPresent()) {
                            r += f.get();
                        }
                        if (exp.isPresent()) {
                            r = r * Math.pow(10.0, exp.get());
                        }
                        return sn ? r : -r;
                    });

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
                    public Result<Chr, String> parse(Input<Chr> in, SymSet<Chr> follow) {
                        for (int i = 0; i < s.length(); ++i) {
                            if (in.isEof()) {
                                return ParserUtils.failureEof(this, in);
                            } else if(!in.get().equals(s.charAt(i))) {
                                return ParserUtils.failure(this, in);
                            } else {
                                in = in.next();
                            }
                        }

                        return Result.success(s, in);
                    }
                };
            }
        }
    }
}
