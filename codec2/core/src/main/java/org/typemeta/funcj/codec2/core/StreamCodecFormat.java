package org.typemeta.funcj.codec2.core;

public interface StreamCodecFormat<
        IN extends StreamCodecFormat.Input,
        OUT extends StreamCodecFormat.Output
> extends CodecFormat<IN, OUT> {

    /**
     * An abstraction for codec input, typically a stream of tokens.
     */
    interface Input {
        boolean readBoolean();
        char readChar();
        byte readByte();
        short readShort();
        int readInt();
        long readLong();
        float readFloat();
        double readDouble();
        String readString();
    }

    /**
     * An abstraction for codec output, typically a stream of tokens.
     */
    interface Output {
        Output writeBoolean(boolean value);
        Output writeChar(char value);
        Output writeByte(byte value);
        Output writeShort(short value);
        Output writeInt(int value);
        Output writeLong(long value);
        Output writeFloat(float value);
        Output writeDouble(double value);
        Output writeString(String value);
    }
}
