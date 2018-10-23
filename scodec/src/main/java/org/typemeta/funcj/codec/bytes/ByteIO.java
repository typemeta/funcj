package org.typemeta.funcj.codec.bytes;

import org.typemeta.funcj.codec.CodecException;

import java.io.*;

public abstract class ByteIO {
    static Input ofInputStream(InputStream is) {
        return new InputImpl(new DataInputStream(is));
    }

    static Input ofDataInput(DataInput input) {
        return new InputImpl(input);
    }

    static Output ofOutputStream(OutputStream os) {
        return new OutputImpl(new DataOutputStream(os));
    }

    static Output ofDataOutput(DataOutput output) {
        return new OutputImpl(output);
    }

    interface Input {
        boolean readBoolean();

        byte readByte();

        char readChar();

        short readShort();

        int readInt();

        long readLong();

        float readFloat();

        double readDouble();

        String readString();
    }

    interface Output {
        Output writeBoolean(boolean v);

        Output writeByte(int v);

        Output writeChar(int v);

        Output writeShort(int v);

        Output writeInt(int v);

        Output writeLong(long v);

        Output writeFloat(float v);

        Output writeDouble(double v);

        Output writeString(String s);
    }

    public static final class InputImpl implements Input {
        private final DataInput input;

        InputImpl(DataInput input) {
            this.input = input;
        }

        @Override
        public boolean readBoolean() {
            try {
                return input.readBoolean();
            } catch (IOException ex) {
                throw new CodecException(ex);
            }
        }

        @Override
        public byte readByte() {
            try {
                return input.readByte();
            } catch (IOException ex) {
                throw new CodecException(ex);
            }
        }

        @Override
        public char readChar() {
            try {
                return input.readChar();
            } catch (IOException ex) {
                throw new CodecException(ex);
            }
        }

        @Override
        public short readShort() {
            try {
                return input.readShort();
            } catch (IOException ex) {
                throw new CodecException(ex);
            }
        }

        @Override
        public int readInt() {
            try {
                return input.readInt();
            } catch (IOException ex) {
                throw new CodecException(ex);
            }
        }

        @Override
        public long readLong() {
            try {
                return input.readLong();
            } catch (IOException ex) {
                throw new CodecException(ex);
            }
        }

        @Override
        public float readFloat() {
            try {
                return input.readFloat();
            } catch (IOException ex) {
                throw new CodecException(ex);
            }
        }

        @Override
        public double readDouble() {
            try {
                return input.readDouble();
            } catch (IOException ex) {
                throw new CodecException(ex);
            }
        }

        public String readString() {
            try {
                final int len = input.readInt();
                final char[] charArray = new char[len];
                for (int i = 0; i < len; ++i) {
                    charArray[i] = input.readChar();
                }
                return new String(charArray);
            } catch (IOException ex) {
                throw new CodecException(ex);
            }
        }
    }

    public static final class OutputImpl implements Output {
        private final DataOutput output;

        OutputImpl(DataOutput output) {
            this.output = output;
        }

        @Override
        public Output writeBoolean(boolean v) {
            try {
                output.writeBoolean(v);
                return this;
            } catch (IOException ex) {
                throw new CodecException(ex);
            }
        }

        @Override
        public Output writeByte(int v) {
            try {
                output.writeByte(v);
                return this;
            } catch (IOException ex) {
                throw new CodecException(ex);
            }
        }

        @Override
        public Output writeChar(int v) {
            try {
                output.writeChar(v);
                return this;
            } catch (IOException ex) {
                throw new CodecException(ex);
            }
        }

        @Override
        public Output writeShort(int v) {
            try {
                output.writeShort(v);
                return this;
            } catch (IOException ex) {
                throw new CodecException(ex);
            }
        }

        @Override
        public Output writeInt(int v) {
            try {
                output.writeInt(v);
                return this;
            } catch (IOException ex) {
                throw new CodecException(ex);
            }
        }

        @Override
        public Output writeLong(long v) {
            try {
                output.writeLong(v);
                return this;
            } catch (IOException ex) {
                throw new CodecException(ex);
            }
        }

        @Override
        public Output writeFloat(float v) {
            try {
                output.writeFloat(v);
                return this;
            } catch (IOException ex) {
                throw new CodecException(ex);
            }
        }

        @Override
        public Output writeDouble(double v) {
            try {
                output.writeDouble(v);
                return this;
            } catch (IOException ex) {
                throw new CodecException(ex);
            }
        }

        public Output writeString(String s) {
            try {
                output.writeInt(s.length());
                output.writeChars(s);
                return this;
            } catch (IOException ex) {
                throw new CodecException(ex);
            }
        }
    }
}
