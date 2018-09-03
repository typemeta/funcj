package org.typemeta.funcj.codec.json;

public abstract class JsonGen {

    interface JsonGenerator {
        JsonGenerator nul();

        JsonGenerator bool(boolean value);

        JsonGenerator number(String value);

        JsonGenerator number(byte value);
        JsonGenerator number(short value);
        JsonGenerator number(int value);
        JsonGenerator number(long value);
        JsonGenerator number(float value);
        JsonGenerator number(double value);
        JsonGenerator string(String value);

        JsonArrayGenerator array();

        JsonObjectGenerator object();
    }

    interface JsonArrayGenerator {
        JsonArrayGenerator nul();

        JsonArrayGenerator bool(boolean value);

        JsonArrayGenerator number(String value);
        JsonArrayGenerator number(byte value);
        JsonArrayGenerator number(short value);
        JsonArrayGenerator number(int value);
        JsonArrayGenerator number(long value);
        JsonArrayGenerator number(float value);
        JsonArrayGenerator number(double value);

        JsonArrayGenerator string(String value);

        JsonArrayGenerator array();

        JsonObjectGenerator object();

        void arrayEnd();
    }

    interface JsonObjectGenerator {
        JsonObjectGenerator nul(String name);

        JsonObjectGenerator bool(String name, boolean value);

        JsonObjectGenerator number(String name, String value);
        JsonObjectGenerator number(String name, byte value);
        JsonObjectGenerator number(String name, short value);
        JsonObjectGenerator number(String name, int value);
        JsonObjectGenerator number(String name, long value);
        JsonObjectGenerator number(String name, float value);
        JsonObjectGenerator number(String name, double value);

        JsonObjectGenerator string(String name, String value);

        JsonArrayGenerator array(String name);

        JsonObjectGenerator object(String name);

        void objectEnd();
    }
}
