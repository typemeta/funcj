package org.typemeta.funcj.codec.bytes.io;

import org.typemeta.funcj.codec.bytes.ByteTypes;

import java.util.function.Consumer;

public abstract class Debug {
    public static class OutputImpl implements ByteTypes.OutStream {
        private final ByteTypes.OutStream os;
        private final Consumer<String> logger;

        public OutputImpl(ByteTypes.OutStream os, Consumer<String> logger) {
            this.os = os;
            this.logger = logger;
        }

        @Override
        public ByteTypes.OutStream writeBoolean(boolean value) {
            logger.accept("W boolean: " + value);
            return os.writeBoolean(value);
        }

        @Override
        public ByteTypes.OutStream writeChar(char value) {
            logger.accept("W char: " + value);
            return os.writeChar(value);
        }

        @Override
        public ByteTypes.OutStream writeByte(byte value) {
            logger.accept("W byte: " + value);
            return os.writeByte(value);
        }

        @Override
        public ByteTypes.OutStream writeShort(short value) {
            logger.accept("W short: " + value);
            return os.writeShort(value);
        }

        @Override
        public ByteTypes.OutStream writeInt(int value) {
            logger.accept("W int: " + value);
            return os.writeInt(value);
        }

        @Override
        public ByteTypes.OutStream writeLong(long value) {
            logger.accept("long: " + value);
            return os.writeLong(value);
        }

        @Override
        public ByteTypes.OutStream writeFloat(float value) {
            logger.accept("W float: " + value);
            return os.writeFloat(value);
        }

        @Override
        public ByteTypes.OutStream writeDouble(double value) {
            logger.accept("W double: " + value);
            return os.writeDouble(value);
        }

        @Override
        public ByteTypes.OutStream writeString(String value) {
            logger.accept("W String: " + value);
            return os.writeString(value);
        }
    }

    public static class InputImpl implements ByteTypes.InStream {
        private final ByteTypes.InStream is;
        private final Consumer<String> logger;

        public InputImpl(ByteTypes.InStream is, Consumer<String> logger) {
            this.is = is;
            this.logger = logger;
        }

        @Override
        public boolean readBoolean() {
            final boolean value = is.readBoolean();
            logger.accept("R boolean: " + value);
            return value;
        }

        @Override
        public char readChar() {
            final char value = is.readChar();
            logger.accept("R char: " + value);
            return value;
        }

        @Override
        public byte readByte() {
            final byte value = is.readByte();
            logger.accept("R byte: " + value);
            return value;
        }

        @Override
        public short readShort() {
            final short value = is.readShort();
            logger.accept("R short: " + value);
            return value;
        }

        @Override
        public int readInt() {
            final int value = is.readInt();
            logger.accept("R int: " + value);
            return value;
        }

        @Override
        public long readLong() {
            final long value = is.readLong();
            logger.accept("R long: " + value);
            return value;
        }

        @Override
        public float readFloat() {
            final float value = is.readFloat();
            logger.accept("R float: " + value);
            return value;
        }

        @Override
        public double readDouble() {
            final double value = is.readDouble();
            logger.accept("R double: " + value);
            return value;
        }

        @Override
        public String readString() {
            final String value = is.readString();
            logger.accept("R String: " + value);
            return value;
        }
    }
}
