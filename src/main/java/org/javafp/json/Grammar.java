package org.javafp.json;

import org.javafp.data.*;
import org.javafp.parsec4j.*;
import org.javafp.util.Chr;

import java.util.LinkedHashMap;

import static org.javafp.parsec4j.Parser.*;
import static org.javafp.parsec4j.Text.*;

/**
 * A grammar for JSON.
 * Adapted from the Haskell Parsec-based JSON parser:
 * https://hackage.haskell.org/package/json
 */
public class Grammar {
    private static <T> Parser<Chr, T> tok(Parser<Chr, T> p) {
        return p.andL(skipMany(ws));
    }

    private static LinkedHashMap<String, Node> toMap(IList<Tuple2<String, Node>> fields) {
        final LinkedHashMap<String, Node> map = new LinkedHashMap<String, Node>();
        fields.forEach(field -> map.put(field._1, field._2));
        return map;
    }

    static {
        final Parser<Chr, Node> jnull = tok(string("null")).andR(pure(Node.nul()));

        final Parser<Chr, Boolean> jtrue = tok(string("true")).andR(pure(Boolean.TRUE));
        final Parser<Chr, Boolean> jfalse = tok(string("false")).andR(pure(Boolean.FALSE));

        final Parser<Chr, Node> jbool = tok(jtrue.or(jfalse)).map(Node::bool);

        final Parser<Chr, Node> jnumber = tok(dble).map(Node::number);

        final Parser<Chr, Byte> hexDigit =
            choice(
                value(Chr.valueOf('0'), (byte)0),
                value(Chr.valueOf('1'), (byte)1),
                value(Chr.valueOf('2'), (byte)2),
                value(Chr.valueOf('3'), (byte)3),
                value(Chr.valueOf('4'), (byte)4),
                value(Chr.valueOf('5'), (byte)5),
                value(Chr.valueOf('6'), (byte)6),
                value(Chr.valueOf('7'), (byte)7),
                value(Chr.valueOf('8'), (byte)8),
                value(Chr.valueOf('9'), (byte)9),
                value(Chr.valueOf('a'), (byte)10),
                value(Chr.valueOf('A'), (byte)10),
                value(Chr.valueOf('b'), (byte)11),
                value(Chr.valueOf('B'), (byte)11),
                value(Chr.valueOf('c'), (byte)12),
                value(Chr.valueOf('C'), (byte)12),
                value(Chr.valueOf('d'), (byte)13),
                value(Chr.valueOf('D'), (byte)13),
                value(Chr.valueOf('e'), (byte)14),
                value(Chr.valueOf('E'), (byte)14),
                value(Chr.valueOf('f'), (byte)15),
                value(Chr.valueOf('F'), (byte)15)
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
            (
                bsChr.andR(esc)
            ).or(
                satisfy("schar", c -> !c.equals('"') && !c.equals('\\'))
            );

        final Parser<Chr, String> jstring =
            tok(between(
                dqChr,
                dqChr,
                many(stringChar).map(Chr::listToString)
            ));

        final Parser<Chr, Node> jtext =
            jstring.map(Node::string);

        final Ref<Chr, Node> jvalue = Ref.of();

        final Parser<Chr, Node> jarray =
            between(
                tok(chr('[')),
                tok(chr(']')),
                sepBy(
                    jvalue,
                    tok(chr(','))
                )
            ).map(Node::array);

        final Parser<Chr, Tuple2<String, Node>> jfield =
            (jstring
                .andL(tok(chr(':')))
                .and(jvalue)
                .map(Tuple2::new)
            );

        final Parser<Chr, Node> jobject =
            between(
                tok(chr('{')),
                tok(chr('}')),
                sepBy(
                    jfield,
                    tok(chr(','))
                ).map(Grammar::toMap).map(Node::object)
            );

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

        parser = tok(jvalue);
    }

    public static final Parser<Chr, Node> parser;

    public static Result<Chr, Node> parse(String str) {
        return skipMany(ws).andR(parser).run(Input.of(str));
    }
}

