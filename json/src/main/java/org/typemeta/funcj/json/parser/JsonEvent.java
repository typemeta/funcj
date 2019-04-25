package org.typemeta.funcj.json.parser;

import java.util.Objects;

public interface JsonEvent {
    Type type();

    enum Type implements JsonEvent {
        ARRAY_END,
        ARRAY_START,
        COMMA,          // internal use only
        COLON,          // internal use only
        EOF,
        FALSE,
        FIELD_NAME,
        NULL,
        NUMBER,
        OBJECT_END,
        OBJECT_START,
        STRING,
        TRUE;

        @Override
        public Type type() {
            return this;
        }
    }

    final class FieldName implements JsonEvent {
        public final String value;

        public FieldName(String value) {
            this.value = Objects.requireNonNull(value);
        }

        @Override
        public Type type() {
            return Type.FIELD_NAME;
        }

        @Override
        public String toString() {
            return "FieldName{" + value + "}";
        }

        @Override
        public boolean equals(Object rhs) {
            if (this == rhs) {
                return true;
            } else if (rhs == null || getClass() != rhs.getClass()) {
                return false;
            } else {
                FieldName rhsJS = (FieldName) rhs;
                return value.equals(rhsJS.value);
            }
        }

        @Override
        public int hashCode() {
            return value.hashCode();
        }
    }

    final class JString implements JsonEvent {
        public final String value;

        public JString(String value) {
            this.value = Objects.requireNonNull(value);
        }

        @Override
        public Type type() {
            return Type.STRING;
        }

        @Override
        public String toString() {
            return "JString{" + value + "}";
        }

        @Override
        public boolean equals(Object rhs) {
            if (this == rhs) {
                return true;
            } else if (rhs == null || getClass() != rhs.getClass()) {
                return false;
            } else {
                final JString rhsJS = (JString) rhs;
                return value.equals(rhsJS.value);
            }
        }

        @Override
        public int hashCode() {
            return value.hashCode();
        }
    }

    final class JNumber implements JsonEvent {
        public final String value;

        public JNumber(String value) {
            this.value = Objects.requireNonNull(value);
        }

        @Override
        public Type type() {
            return Type.NUMBER;
        }

        @Override
        public String toString() {
            return "JNumber{" + value + "}";
        }

        @Override
        public boolean equals(Object rhs) {
            if (this == rhs) {
                return true;
            } else if (rhs == null || getClass() != rhs.getClass()) {
                return false;
            } else {
                final JNumber rhsJN = (JNumber) rhs;
                return value.equals(rhsJN.value);
            }
        }

        @Override
        public int hashCode() {
            return value.hashCode();
        }
    }
}
