package org.typemeta.funcj.codec.stream;

import org.typemeta.funcj.codec.*;

public interface StreamCodecFormat<
        IN extends StreamCodecFormat.Input<IN>,
        OUT extends StreamCodecFormat.Output<OUT>,
        CFG extends CodecConfig
> extends CodecFormat<IN, OUT, CFG> {

    /**
     * An abstraction for codec input, typically a stream of tokens.
     * @param <IN>      the input type
     */
    interface Input<IN extends Input<IN>> extends AutoCloseable {

        @Override
        default void close() {
        }

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
     * @param <OUT>     the output type
     */
    interface Output<OUT extends Output<OUT>> extends AutoCloseable {

        @Override
        default void close() {
        }

        OUT writeBoolean(boolean value);
        OUT writeChar(char value);
        OUT writeByte(byte value);
        OUT writeShort(short value);
        OUT writeInt(int value);
        OUT writeLong(long value);
        OUT writeFloat(float value);
        OUT writeDouble(double value);
        OUT writeString(String value);
    }

    default <EM extends Enum<EM>> Codec<EM, IN, OUT, CFG> enumCodec(Class<EM> enumType) {
        return new StreamCodec<EM, IN, OUT, CFG>() {
            @Override
            public Class<EM> type() {
                return enumType;
            }

            @Override
            public OUT encode(CodecCoreEx<IN, OUT, CFG> core, EM value, OUT out) {
                return out.writeString(value.name());
            }

            @Override
            public EM decode(CodecCoreEx<IN, OUT, CFG> core, IN in) {
                return EM.valueOf(type(), in.readString());
            }
        };
    }
}
