package org.typemeta.funcj.codec.jsons;

public interface JsonEvent {
    enum Enum implements JsonEvent {
        NULL,
        TRUE,
        FALSE,
        ARRAY_START,
        ARRAY_EMD,
        OBJECT_START,
        OBJECT_END
    }

    class JName implements JsonEvent {
        public final String value;

        public JName(String value) {
            this.value = value;
        }
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
