package org.typemeta.funcj.parser;

import org.typemeta.funcj.data.Chr;

import static org.typemeta.funcj.parser.Combinators.*;

/**
 * Parser combinators for working with {@link Chr} streams.
 */
public abstract class Text {
    /**
     * Specialisation of {@link Parser#pure(Object)} for {@code Chr}.
     * Construct a parser that always returns the given value, without consuming any input.
     * @param c         the char value
     * @return          a parser that always returns the given char
     */
    public static Parser<Chr, Chr> pure(char c) {
        return Parser.pure(Chr.valueOf(c));
    }

    /**
     * A parser that succeeds if the next input symbol equals the given char {@code c},
     * and returns the value.
     * @param c         the value expected by the parser
     * @return          as parser that succeeds if the next input symbol equals the given char {@code c}
     */
    public static Parser<Chr, Chr> chr(char c) {
        return value(Chr.valueOf(c));
    }

    /**
     * A parser that succeeds if the next input symbol is an alphabetic letter.
     */
    public static final Parser<Chr, Chr> alpha = satisfy("letter", Chr::isAlphabetic);

    /**
     * A parser that succeeds if the next input symbol is a numeric digit.
     */
    public static final Parser<Chr, Chr> digit = satisfy("digit", Chr::isDigit);

    /**
     * A parser that succeeds if the next input symbol is a numeric digit.
     */
    public static final Parser<Chr, Chr> nonZeroDigit = satisfy(
            "nonZeroDigit",
            c -> c.charValue() != '0' && Chr.isDigit(c));

    /**
     * A parser that succeeds if the next input symbol is a letter or a digit.
     */
    public static final Parser<Chr, Chr> alphaNum = satisfy("letterOrDigit", Chr::isLetterOrDigit);

    /**
     * A parser that succeeds if the next input symbol is whitespace.
     */
    public static final Parser<Chr, Chr> ws = satisfy("ws", Chr::isWhitespace);

    public static int digitToInt(Chr c) {
        return Chr.getNumericValue(c);
    }

    public static final Parser<Chr, Boolean> sign =
            choice(
                    chr('+').andR(Parser.pure(true)),
                    chr('-').andR(Parser.pure(false)),
                    Parser.pure(true)
            );

    private static final Parser<Chr, Integer> uintrZero =
            chr('0').map(zs -> 0);

    private static final Parser<Chr, Integer> uintrNotZero =
            nonZeroDigit.and(digit.many())
                    .map(d -> ds -> ds.add(d))
                    .map(ds -> ds.map(Text::digitToInt))
                    .map(is -> is.foldLeft1((acc, x) -> acc * 10 + x));

    /**
     * A parser for an unsigned integer.
     */
    public static final Parser<Chr, Integer> uintr = uintrZero.or(uintrNotZero);

    /**
     * A parser for a signed integer.
     */
    public static final Parser<Chr, Integer> intr =
            sign.and(uintr)
                    .map((sign, i) -> sign ? i : -i);

    private static final Parser<Chr, Long> ulngZero =
            chr('0').map(zs -> 0l);

    private static final Parser<Chr, Long> ulngNotZero =
            nonZeroDigit.and(digit.many())
                    .map(d -> ds -> ds.add(d))
                    .map(ds -> ds.map(Text::digitToInt))
                    .map(ds -> ds.foldLeft((acc, x) -> acc * 10l + x, 0l));

    /**
     * A parser for an unsigned long.
     */
    public static final Parser<Chr, Long> ulng = ulngZero.or(ulngNotZero);

    /**
     * A parser for an unsigned long.
     */
    public static final Parser<Chr, Long> lng =
            sign.and(ulng)
                    .map((sign, i) -> sign ? i : -i);

    private static final Parser<Chr, Double> floating =
            digit.many()
                    .map(ds -> ds.map(Text::digitToInt))
                    .map(l -> l.foldRight((d, acc) -> d + acc / 10.0, 0.0) / 10.0);

    private static final Parser<Chr, Integer> expnt =
            (chr('e').or(chr('E')))
                    .andR(intr);

    /**
     * A parser for a floating point number.
     */
    public static final Parser<Chr, Double> dble =
            sign.and(ulng)
                    .and((chr('.').andR(floating)).optional())
                    .and(expnt.optional())
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
     * A parser that succeeds if it can extract the given string from the input.
     * @param s         the expected string
     * @return          a parser for the given string value
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
                    public Result<Chr, String> apply(Input<Chr> in, SymSet<Chr> follow) {
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
