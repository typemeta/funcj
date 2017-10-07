package org.typemeta.funcj.parser;

import org.typemeta.funcj.data.Chr;

/**
 * Parser combinators for working with {@link Chr} streams.
 */
public abstract class Text {
    /**
     * Specialisation of {@link Parser#pure(Object)} for {@code Chr}.
     * Construct a parser that always returns the supplied value, without consuming any input.
     * @param c         the char value
     * @return          a parser that always returns the supplied char
     */
    public static Parser<Chr, Chr> pure(char c) {
        return Parser.pure(Chr.valueOf(c));
    }

    /**
     * A parser that succeeds if the next input symbol equals the supplied char {@code c},
     * and returns the value.
     * @param c         the value expected by the parser
     * @return          as parser that succeeds if the next input symbol equals the supplied char {@code c}
     */
    public static Parser<Chr, Chr> chr(char c) {
        return Combinators.value(Chr.valueOf(c));
    }

    /**
     * A parser that succeeds if the next input symbol is an alphabetic letter.
     */
    public static final Parser<Chr, Chr> alpha = Combinators.satisfy("letter", Chr::isLetter);

    /**
     * A parser that succeeds if the next input symbol is a numeric digit.
     */
    public static final Parser<Chr, Chr> digit = Combinators.satisfy("digit", Chr::isDigit);

    /**
     * A parser that succeeds if the next input symbol is a letter or a digit.
     */
    public static final Parser<Chr, Chr> alphaNum = Combinators.satisfy("letterOrDigit", Chr::isLetterOrDigit);

    /**
     * A parser that succeeds if the next input symbol is whitespace.
     */
    public static final Parser<Chr, Chr> ws = Combinators.satisfy("ws", Chr::isWhitespace);

    private static int digitToInt(char c) {
        return Chr.getNumericValue(c);
    }

    private static int digitToInt(Chr c) {
        return Chr.getNumericValue(c.charValue());
    }

    private static final Parser<Chr, Boolean> sign =
            Combinators.choice(
                    chr('+').andR(Parser.pure(true)),
                    chr('-').andR(Parser.pure(false)),
                    Parser.pure(true)
            );

    /**
     * parser that parses an unsigned integer.
     */
    public static final Parser<Chr, Integer> uintr =
            Combinators.many1(digit.map(Text::digitToInt))
                    .map(l -> l.foldLeft1((acc, x) -> acc * 10 + x));

    /**
     * parser that parses a signed integer.
     */
    public static final Parser<Chr, Integer> intr =
            sign.and(uintr)
                    .map((sign, i) -> sign ? i : -i);

    /**
     * parser that parses an unsigned long.
     */
    public static final Parser<Chr, Long> ulng =
            Combinators.many1(digit.map(c -> (long)digitToInt(c)))
                    .map(l -> l.foldLeft1((acc, x) -> acc * 10 + x));

    /**
     * parser that parses a signed long.
     */
    public static final Parser<Chr, Long> lng =
            sign.and(ulng)
                    .map((sign, i) -> sign ? i : -i);

    private static final Parser<Chr, Double> floating =
            Combinators.many(digit.map(Text::digitToInt))
                    .map(l -> l.foldRight((d, acc) -> d + acc / 10.0, 0.0) / 10.0);

    private static final Parser<Chr, Integer> expnt =
            (chr('e').or(chr('E')))
                    .andR(intr);

    /**
     * parser that parses a floating point number.
     */
    public static final Parser<Chr, Double> dble =
            sign.and(ulng).and(Combinators.optional(chr('.').andR(floating)))
                    .and(Combinators.optional(expnt))
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

    /**
     * Construct a parser that succeeds if it can extract the supplied string from the input.
     * @param s         the expected string
     * @return          a parser that parses the supplied string value
     */
    public static Parser<Chr, String> string(String s) {
        switch (s.length()) {
            case 0: return Combinators.fail();
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
                                return Utils.failureEof(this, in);
                            } else if (!in.get().equals(s.charAt(i))) {
                                return Utils.failure(this, in);
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
