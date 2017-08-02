package org.funcj.json;

import org.funcj.data.*;
import org.funcj.parser.*;

import java.io.Reader;
import java.util.*;

import static org.funcj.parser.Parser.pure;
import static org.funcj.parser.Text.*;

/**
 * A parser for JSON.
 * Adapted from the Haskell Parsec-based JSON parser:
 * https://hackage.haskell.org/package/json
 */
public class JsonParser {
    private static <T> Parser<Chr, T> tok(Parser<Chr, T> p) {
        return p.andL(Combinators.skipMany(ws));
    }

    private static List<JSObject.Field> toMap(Iterable<Tuple2<String, JSValue>> iter) {
        final List<JSObject.Field> fields = new ArrayList<>();
        iter.forEach(t2 -> fields.add(JSObject.field(t2._1, t2._2)));
        return fields;
    }

    static {
        final Parser<Chr, JSValue> jnull = tok(string("null")).andR(pure(JSNull.of()));

        final Parser<Chr, Boolean> jtrue = tok(string("true")).andR(pure(Boolean.TRUE));
        final Parser<Chr, Boolean> jfalse = tok(string("false")).andR(pure(Boolean.FALSE));

        final Parser<Chr, JSValue> jbool = tok(jtrue.or(jfalse)).map(JSBool::of);

        final Parser<Chr, JSValue> jnumber = tok(dble).map(JSNumber::of);

        final Parser<Chr, Byte> hexDigit =
            Combinators.choice(
                Combinators.value(Chr.valueOf('0'), (byte)0),
                Combinators.value(Chr.valueOf('1'), (byte)1),
                Combinators.value(Chr.valueOf('2'), (byte)2),
                Combinators.value(Chr.valueOf('3'), (byte)3),
                Combinators.value(Chr.valueOf('4'), (byte)4),
                Combinators.value(Chr.valueOf('5'), (byte)5),
                Combinators.value(Chr.valueOf('6'), (byte)6),
                Combinators.value(Chr.valueOf('7'), (byte)7),
                Combinators.value(Chr.valueOf('8'), (byte)8),
                Combinators.value(Chr.valueOf('9'), (byte)9),
                Combinators.value(Chr.valueOf('a'), (byte)10),
                Combinators.value(Chr.valueOf('A'), (byte)10),
                Combinators.value(Chr.valueOf('b'), (byte)11),
                Combinators.value(Chr.valueOf('B'), (byte)11),
                Combinators.value(Chr.valueOf('c'), (byte)12),
                Combinators.value(Chr.valueOf('C'), (byte)12),
                Combinators.value(Chr.valueOf('d'), (byte)13),
                Combinators.value(Chr.valueOf('D'), (byte)13),
                Combinators.value(Chr.valueOf('e'), (byte)14),
                Combinators.value(Chr.valueOf('E'), (byte)14),
                Combinators.value(Chr.valueOf('f'), (byte)15),
                Combinators.value(Chr.valueOf('F'), (byte)15)
            );

        final Parser<Chr, Chr> uni =
            hexDigit
                .and(hexDigit)
                .and(hexDigit)
                .and(hexDigit)
                .map((d0, d1, d2, d3) -> (d0 << 0x3) & (d1 << 0x2) & (d2 << 0x1) & d0)
                .map(Chr::valueOf);

        final Parser<Chr, Chr> uChr = chr('u');
        final Parser<Chr, Chr> bsChr = chr('\\');
        final Parser<Chr, Chr> dqChr = chr('"');

        final Parser<Chr, Chr> esc =
            Combinators.choice(
                dqChr,
                bsChr,
                chr('/'),
                Combinators.value(Chr.valueOf('b'), Chr.valueOf('\b')),
                Combinators.value(Chr.valueOf('f'), Chr.valueOf('\f')),
                Combinators.value(Chr.valueOf('n'), Chr.valueOf('\n')),
                Combinators.value(Chr.valueOf('r'), Chr.valueOf('\r')),
                Combinators.value(Chr.valueOf('t'), Chr.valueOf('\t')),
                uChr.andR(uni)
            );

        final Parser<Chr, Chr> stringChar =
            (bsChr.andR(esc)).or(
                Combinators.satisfy("schar", c -> !c.equals('"') && !c.equals('\\'))
            );

        final Parser<Chr, String> jstring =
            tok(Combinators.between(
                dqChr,
                dqChr,
                Combinators.many(stringChar).map(Chr::listToString)
            ));

        final Parser<Chr, JSValue> jtext =
            jstring.map(JSString::of);

        final Ref<Chr, JSValue> jvalue = Ref.of();

        final Parser<Chr, JSValue> jarray =
            Combinators.between(
                tok(chr('[')),
                tok(chr(']')),
                Combinators.sepBy(
                    jvalue,
                    tok(chr(','))
                )
            ).map(IList::toList).map(JSArray::of);

        final Parser<Chr, Tuple2<String, JSValue>> jfield =
            jstring
                .andL(tok(chr(':')))
                .and(jvalue)
                .map(Tuple2::new);

        final Parser<Chr, JSValue> jobject =
            Combinators.between(
                tok(chr('{')),
                tok(chr('}')),
                Combinators.sepBy(
                    jfield,
                    tok(chr(','))
                ).map(JsonParser::toMap).map(JSObject::of)
            );

        jvalue.set(
            Combinators.choice(
                jnull,
                jbool,
                jnumber,
                jtext,
                jarray,
                jobject
            )
        );

        parser = Combinators.skipMany(ws).andR(tok(jvalue));
    }

    /**
     * Parser (primarily for composing with other Parsers).
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

