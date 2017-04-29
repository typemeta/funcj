package org.funcj.json;

import org.funcj.util.Functions.F;

import java.util.*;

public interface Token {
    enum TokenImpl implements Token {
        LEFT_BRACE,
        RIGHT_BRACE,
        LEFT_SQR_BR,
        RIGHT_SQR_BR,
        COLON,
        COMMA,
        NULL,
        TRUE,
        FALSE;

        public <T> T match(F<TokenImpl, T> tokenImpl, F<Num, T> num, F<Str, T> str) {
            return tokenImpl.apply(this);
        }
    }

    class Num implements Token {
        public final double value;

        public Num(double value) {
            this.value = value;
        }

        @Override
        public <T> T match(F<TokenImpl, T> tokenImpl, F<Num, T> num, F<Str, T> str) {
            return num.apply(this);
        }
    }

    class Str implements Token {
        public final String value;

        public Str(String value) {
            this.value = value;
        }

        @Override
        public <T> T match(F<TokenImpl, T> tokenImpl, F<Num, T> num, F<Str, T> str) {
            return str.apply(this);
        }
    }

    <T> T match(F<TokenImpl, T> tokenImpl, F<Num, T> num, F<Str, T> str);

    static List<Token> tokenise(String s) {
        final List<Token> tokens = new ArrayList<>();
        return tokens;
    }
}
