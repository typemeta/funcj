package org.typemeta.funcj.codec.old;

public interface JsonEvent {
    enum Enum implements JsonEvent {
        NULL,
        TRUE,
        FALSE,
        ARRAY_START,
        COMMA,
        ARRAY_EMD,
        OBJECT_START,
        COLON,
        OBJECT_END
    }

    class JString implements JsonEvent {
        public final String value;

        public JString(String value) {
            this.value = value;
        }
    }

    class JNumber implements JsonEvent {
        public final String value;

        public JNumber(String value) {
            this.value = value;
        }
    }
}
