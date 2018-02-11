package org.typemeta.funcj.codec.jsonp;

import java.math.BigDecimal;

public interface JsonIO {
    interface JsonInput extends JsonIO {
        void readNull();

        boolean readBool();

        String readStr();

        int readInt();
        double readDbl();
        BigDecimal readBigDec();
        Number readNumber();

        void readStartObject();
        void readEndObject();

        void readStartArray();
        void readEndArray();
    }

    interface JsonOutput extends JsonIO {
        void writeNull();

        void write(boolean value);

        void write(String value);

        void write(int value);

        void write(double value);

        void write(BigDecimal value);

        void writeStartObject();
        void writeField(String name);

        void writeStartArray();

        void writeEnd();
    }

    JsonInput input();

    JsonOutput output();
}
