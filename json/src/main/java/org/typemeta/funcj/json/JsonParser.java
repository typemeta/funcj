package org.typemeta.funcj.json;

import org.typemeta.funcj.data.Chr;
import org.typemeta.funcj.json.model.*;
import org.typemeta.funcj.parser.*;

import java.io.Reader;

import static org.typemeta.funcj.parser.Combinators.*;
import static org.typemeta.funcj.parser.Parser.pure;
import static org.typemeta.funcj.parser.Text.*;

/**
 * A parser for JSON.
 * Adapted from the Haskell Parsec-based JSON parser:
 * https://hackage.haskell.org/package/json
 */
public class JsonParser {
    private static <T> Parser<Chr, T> tok(Parser<Chr, T> p) {
        return p.andL(ws.skipMany());
    }

    static {
        final Parser<Chr, JSNull> jnull = tok(string("null")).andR(pure(JSAPI.nul()));

        final Parser<Chr, Boolean> jtrue = tok(string("true")).andR(pure(Boolean.TRUE));
        final Parser<Chr, Boolean> jfalse = tok(string("false")).andR(pure(Boolean.FALSE));

        final Parser<Chr, JSBool> jbool = tok(jtrue.or(jfalse)).map(JSAPI::bool);

        final Parser<Chr, JSNumber> jnumber = tok(dble).map(JSAPI::num);

        final Parser<Chr, Byte> digit = Text.digit.map(Byte.class::cast);
        final Parser<Chr, Byte> hexA = chr('a').or(chr('A')).map(u -> (byte)10);
        final Parser<Chr, Byte> hexB = chr('b').or(chr('B')).map(u -> (byte)11);
        final Parser<Chr, Byte> hexC = chr('c').or(chr('C')).map(u -> (byte)12);
        final Parser<Chr, Byte> hexD = chr('d').or(chr('D')).map(u -> (byte)13);
        final Parser<Chr, Byte> hexE = chr('e').or(chr('E')).map(u -> (byte)14);
        final Parser<Chr, Byte> hexF = chr('f').or(chr('F')).map(u -> (byte)15);

        final Parser<Chr, Byte> hexDigit =
                choice(
                        digit,
                        hexA,
                        hexB,
                        hexC,
                        hexD,
                        hexE,
                        hexF
                );

        final Parser<Chr, Chr> uni =
                hexDigit.and(hexDigit).and(hexDigit).and(hexDigit)
                        .map((d0, d1, d2, d3) ->
                                (d0.intValue() << 12) |
                                        (d1.intValue() << 8) |
                                        (d2.intValue() << 4) |
                                        d3.intValue())
                        .map(Chr::valueOf);

        final Parser<Chr, Chr> uChr = chr('u');
        final Parser<Chr, Chr> bsChr = chr('\\');
        final Parser<Chr, Chr> dqChr = chr('"');

        final Parser<Chr, Chr> esc =
                choice(
                        dqChr,
                        bsChr,
                        chr('/'),
                        value(Chr.valueOf('b'), Chr.valueOf('\b')),
                        value(Chr.valueOf('f'), Chr.valueOf('\f')),
                        value(Chr.valueOf('n'), Chr.valueOf('\n')),
                        value(Chr.valueOf('r'), Chr.valueOf('\r')),
                        value(Chr.valueOf('t'), Chr.valueOf('\t')),
                        uChr.andR(uni)
                );

        final Parser<Chr, Chr> stringChar =
                (bsChr.andR(esc)).or(
                        satisfy("schar", c -> !c.equals('"') && !c.equals('\\'))
                );

        final Parser<Chr, String> jstring =
                tok(stringChar.many()
                        .map(Chr::listToString)
                        .between(dqChr, dqChr)
                );

        final Parser<Chr, JSString> jtext =
                jstring.map(JSAPI::str);

        final Ref<Chr, JSValue> jvalue = Parser.ref();

        final Parser<Chr, JSArray> jarray =
                jvalue.sepBy(tok(chr(',')))
                        .between(
                                tok(chr('[')),
                                tok(chr(']')))
                        .map(JSAPI::arr);

        final Parser<Chr, JSObject.Field> jfield =
                jstring
                        .andL(tok(chr(':')))
                        .and(jvalue)
                        .map(JSAPI::field);

        final Parser<Chr, JSObject> jobject =
                jfield
                        .sepBy(tok(chr(',')))
                        .between(
                                tok(chr('{')),
                                tok(chr('}'))
                        ).map(JSAPI::obj);

        jvalue.set(
                choice(
                        jnull,
                        jbool,
                        jnumber,
                        jtext,
                        jarray,
                        jobject
                )
        );

        parser = ws.skipMany().andR(tok(jvalue));
    }

    /**
     * Parser value (primarily for composing with other Parsers).
     */
    public static final Parser<Chr, JSValue> parser;

    /**
     * Parse a JSON string into a parse result.
     * @param str JSON string
     * @return parse result
     */
    public static Result<Chr, JSValue> parse(String str) {
        return parser.run(Input.of(str));
    }


    /**
     * Parse a JSON input stream into a parse result.
     * @param rdr JSON input stream
     * @return parse result
     */
    public static Result<Chr, JSValue> parse(Reader rdr) {
        return parser.run(Input.of(rdr));
    }
}

