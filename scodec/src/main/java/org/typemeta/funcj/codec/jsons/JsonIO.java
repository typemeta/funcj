package org.typemeta.funcj.codec.jsons;

import java.math.BigDecimal;

public abstract class JsonIO {
    interface Input  {

        enum Type {
            ARRAY_END,
            ARRAY_START,
            BOOL_FALSE,
            BOOL_TRUE,
            FIELD_NAME,
            OBJECT_END,
            OBJECT_START,
            NUMBER,
            NULL,
            STRING
        };

        boolean hasNext();

        Type nextType();

        Void readNull();

        boolean readBool();

        String readStr();
        char readChar();

        byte readByte();
        short readShort();
        int readInt();
        long readLong();
        float readFloat();
        double readDbl();
        BigDecimal readBigDec();
        Number readNumber();

        void startObject();
        String readFieldName();
        void endObject();

        void startArray();
        void endArray();
    }

    interface Output {

        Output writeNull();

        Output writeBool(boolean value);

        Output writeStr(String value);
        Output writeChar(char value);

        Output writeNum(byte value);
        Output writeNum(short value);
        Output writeNum(int value);
        Output writeNum(long value);
        Output writeNum(float value);
        Output writeNum(double value);
        Output writeNum(BigDecimal value);
        Output writeNum(String value);

        Output startObject();
        Output writeField(String name);
        Output endObject();

        Output startArray();
        Output endArray();
    }
}
