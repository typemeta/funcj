package org.javafp.parsec4j;

import org.javafp.util.Chr;

import static org.javafp.parsec4j.Parser.*;

public class Text {
    private static final Parser<Chr, Ctx<Chr>, Chr> anyChar = any();

    public static <CTX extends Parser.Context<Chr>>
    Parser<Chr, CTX, Chr> anyChar() {
        return (Parser<Chr, CTX, Chr>) anyChar;
    }

    public static <CTX extends Parser.Context<Chr>>
    Parser<Chr, CTX, Chr> chr(char c) {
        return value(new Chr(c));
    }

    private static final Parser<Chr, Ctx<Chr>, Chr> alpha = satisfy(Chr::isLetter);

    public static <CTX extends Parser.Context<Chr>>
    Parser<Chr, CTX, Chr> alpha() {
        return (Parser<Chr, CTX, Chr>) alpha;
    }

    private static final Parser<Chr, Ctx<Chr>, Chr> digit = satisfy(Chr::isDigit);

    public static <CTX extends Parser.Context<Chr>>
    Parser<Chr, CTX, Chr> digit() {
        return (Parser<Chr, CTX, Chr>) digit;
    }

    private static final Parser<Chr, Ctx<Chr>, Chr> alphaNum = satisfy(Chr::isLetterOrDigit);

    public static <CTX extends Parser.Context<Chr>>
    Parser<Chr, CTX, Chr> alphaNum() {
        return (Parser<Chr, CTX, Chr>) alphaNum;
    }

    private static final Parser<Chr, Ctx<Chr>, Chr> ws = satisfy(Chr::isWhitespace);

    public static <CTX extends Parser.Context<Chr>>
    Parser<Chr, CTX, Chr> ws() {
        return (Parser<Chr, CTX, Chr>) ws;
    }

    private static int digitToInt(char c) {
        return Chr.getNumericValue(c);
    }

    private static int digitToInt(Chr c) {
        return Chr.getNumericValue(c.charValue());
    }

    private static final Parser<Chr, Ctx<Chr>, Integer> uintr =
        many1(Text.<Ctx<Chr>>digit().map(Text::digitToInt))
            .map(l -> l.foldl1((x, acc) -> x + acc * 10));

    public static <CTX extends Parser.Context<Chr>>
    Parser<Chr, CTX, Integer> uintr() {
        return (Parser<Chr, CTX, Integer>) uintr;
    }

    private static final Parser<Chr, Ctx<Chr>, Integer> intr =
        choice(
            Text.<Ctx<Chr>>chr('+'),
            chr('-'),
            pure(Chr.valueOf('+'))
        ).and(uintr())
            .map((sign, i) -> sign.charValue() == '+' ? i : -i);

    public static <CTX extends Parser.Context<Chr>>
    Parser<Chr, CTX, Integer> intr() {
        return (Parser<Chr, CTX, Integer>) intr;
    }

    private static final Parser<Chr, Ctx<Chr>, Double> floating =
        many1(Text.<Ctx<Chr>>digit().map(Text::digitToInt))
            .map(l -> l.foldr((d, acc) -> d + acc / 10.0, 0.0) / 10.0);

    public static <CTX extends Parser.Context<Chr>>
    Parser<Chr, CTX, Double> floating() {
        return (Parser<Chr, CTX, Double>)floating;
    }

    private static final Parser<Chr, Ctx<Chr>, Double> dble =
        Text.<Ctx<Chr>>intr()
            .and(optional(Text.<Ctx<Chr>>chr('.')
                .andR(floating())))
            .map((i, f) -> i.doubleValue() + f.orElse(0.0));

    public static <CTX extends Parser.Context<Chr>>
    Parser<Chr, CTX, Double> dble() {
        return (Parser<Chr, CTX, Double>) dble;
    }

    public static <CTX extends Parser.Context<Chr>>
    Parser<Chr, CTX, String> string(String s) {
        switch (s.length()) {
            case 0: return fail();
            case 1: return Text.<CTX>chr(s.charAt(0)).map(Object::toString);
            default: {
                return new ParserImpl<Chr, CTX, String>() {
                    @Override
                    public boolean acceptsEmpty() {
                        return false;
                    }

                    @Override
                    public SymSet<Chr> firstSetCalc() {
                        return SymSet.value(Chr.valueOf(s.charAt(0)));
                    }

                    @Override
                    public Result<Chr, String> parse(CTX ctx, int pos) {
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
                };
            }
        }
    }
}
