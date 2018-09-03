package org.typemeta.funcj.codec.json;

import org.typemeta.funcj.codec.CodecException;

import java.io.*;
import java.util.*;

public abstract class JsonGeneratorFactory {

    public static JsonGen.JsonObjectGenerator create(OutputStream os) {
        return new JsonObjectGeneratorImpl(new Root(os));
    }

    static class Root {

        private final OutputStream os;
        private final List<Generator> genStack = new ArrayList<>();

        Root(OutputStream os) {
            this.os = os;
        }

        protected void pop() {
            genStack.remove(genStack.size() - 1);
        }
    }

    private static abstract class Generator {
        protected final Root root;

        protected Generator(Root root) {
            this.root = root;
            root.genStack.add(this);
        }

        protected void write(String value) {
            try {
                root.os.write(value.getBytes());
            } catch (IOException ex) {
                throw new CodecException(ex);
            }
        }

        protected void write(char value) {
            try {
                root.os.write(value);
            } catch (IOException ex) {
                throw new CodecException(ex);
            }
        }

        protected void writeNull() {
            write("null");
        }

        protected void writeBool(boolean value) {
            write(Boolean.toString(value));
        }

        protected void writeNumber(byte value) {
            write(String.valueOf(value));
        }

        protected void writeNumber(short value) {
            write(String.valueOf(value));
        }

        protected void writeNumber(int value) {
            write(String.valueOf(value));
        }

        protected void writeNumber(long value) {
            write(String.valueOf(value));
        }

        protected void writeNumber(float value) {
            write(String.valueOf(value));
        }

        protected void writeNumber(double value) {
            write(String.valueOf(value));
        }

        protected void writeNumber(String value) {
            write(value);
        }

        protected void writeString(String value) {
            write('"');
            write(String.valueOf(value));
            write('"');
        }
    }

    public static class JsonGeneratorImpl extends Generator implements JsonGen.JsonGenerator {

        protected JsonGeneratorImpl(Root root) {
            super(root);
        }

        protected JsonGeneratorImpl(Generator gen) {
            this(gen.root);
        }

        @Override
        public JsonGen.JsonGenerator nul() {
            writeNull();
            return this;
        }

        @Override
        public JsonGen.JsonGenerator bool(boolean value) {
            writeBool(value);
            return this;
        }

        @Override
        public JsonGen.JsonGenerator number(String value) {
            writeNumber(value);
            return this;
        }

        @Override
        public JsonGen.JsonGenerator number(byte value) {
            writeNumber(value);
            return this;
        }

        @Override
        public JsonGen.JsonGenerator number(short value) {
            writeNumber(value);
            return this;
        }

        @Override
        public JsonGen.JsonGenerator number(int value) {
            writeNumber(value);
            return this;
        }

        @Override
        public JsonGen.JsonGenerator number(long value) {
            writeNumber(value);
            return this;
        }

        @Override
        public JsonGen.JsonGenerator number(float value) {
            writeNumber(value);
            return this;
        }

        @Override
        public JsonGen.JsonGenerator number(double value) {
            writeNumber(value);
            return this;
        }

        @Override
        public JsonGen.JsonGenerator string(String value) {
            writeString(value);
            return this;
        }

        @Override
        public JsonGen.JsonArrayGenerator array() {
            write('[');
            return new JsonArrayGeneratorImpl(this);
        }

        @Override
        public JsonGen.JsonObjectGenerator object() {
            write('{');
            return new JsonObjectGeneratorImpl(this);
        }
    }

    private static class JsonArrayGeneratorImpl extends Generator implements JsonGen.JsonArrayGenerator {

        boolean comma = false;

        protected JsonArrayGeneratorImpl(Root root) {
            super(root);
        }

        protected JsonArrayGeneratorImpl(Generator gen) {
            this(gen.root);
        }

        @Override
        public JsonGen.JsonArrayGenerator nul() {
            writeNull();
            return this;
        }

        @Override
        public JsonGen.JsonArrayGenerator bool(boolean value) {
            writeBool(value);
            return this;
        }

        @Override
        public JsonGen.JsonArrayGenerator number(String value) {
            writeNumber(value);
            return this;
        }

        @Override
        public JsonGen.JsonArrayGenerator number(byte value) {
            writeNumber(value);
            return this;
        }

        @Override
        public JsonGen.JsonArrayGenerator number(short value) {
            writeNumber(value);
            return this;
        }

        @Override
        public JsonGen.JsonArrayGenerator number(int value) {
            writeNumber(value);
            return this;
        }

        @Override
        public JsonGen.JsonArrayGenerator number(long value) {
            writeNumber(value);
            return this;
        }

        @Override
        public JsonGen.JsonArrayGenerator number(float value) {
            writeNumber(value);
            return this;
        }

        @Override
        public JsonGen.JsonArrayGenerator number(double value) {
            writeNumber(value);
            return this;
        }

        @Override
        public JsonGen.JsonArrayGenerator string(String value) {
            writeString(value);
            return this;
        }

        @Override
        public void arrayEnd() {
            write(']');
            root.pop();
        }

        @Override
        public JsonGen.JsonArrayGenerator array() {
            write('[');
            return new JsonArrayGeneratorImpl(this);
        }

        @Override
        public JsonGen.JsonObjectGenerator object() {
            write('{');
            return new JsonObjectGeneratorImpl(this);
        }
    }

    private static class JsonObjectGeneratorImpl extends Generator implements JsonGen.JsonObjectGenerator {

        protected JsonObjectGeneratorImpl(Root root) {
            super(root);
        }

        protected JsonObjectGeneratorImpl(Generator gen) {
            this(gen.root);
        }

        protected void writeField(String name) {
            write(name);
            write(':');
        }

        @Override
        public JsonGen.JsonObjectGenerator nul(String name) {
            writeField(name);
            writeNull();
            return this;
        }

        @Override
        public JsonGen.JsonObjectGenerator bool(String name, boolean value) {
            writeField(name);
            writeBool(value);
            return this;
        }

        @Override
        public JsonGen.JsonObjectGenerator number(String name, String value) {
            writeField(name);
            writeNumber(value);
            return this;
        }

        @Override
        public JsonGen.JsonObjectGenerator number(String name, byte value) {
            writeField(name);
            writeNumber(value);
            return this;
        }

        @Override
        public JsonGen.JsonObjectGenerator number(String name, short value) {
            writeField(name);
            writeNumber(value);
            return this;
        }

        @Override
        public JsonGen.JsonObjectGenerator number(String name, int value) {
            writeField(name);
            writeNumber(value);
            return this;
        }

        @Override
        public JsonGen.JsonObjectGenerator number(String name, long value) {
            writeField(name);
            writeNumber(value);
            return this;
        }

        @Override
        public JsonGen.JsonObjectGenerator number(String name, float value) {
            writeField(name);
            writeNumber(value);
            return this;
        }

        @Override
        public JsonGen.JsonObjectGenerator number(String name, double value) {
            writeField(name);
            writeNumber(value);
            return this;
        }

        @Override
        public JsonGen.JsonObjectGenerator string(String name, String value) {
            writeField(name);
            writeString(value);
            return this;
        }

        @Override
        public JsonGen.JsonArrayGenerator array(String name) {
            writeField(name);
            write('[');
            return new JsonArrayGeneratorImpl(this);
        }

        @Override
        public JsonGen.JsonObjectGenerator object(String name) {
            writeField(name);
            write('{');
            return new JsonObjectGeneratorImpl(this);
        }

        @Override
        public void objectEnd() {
            write('}');
            root.pop();
        }
    }
}
