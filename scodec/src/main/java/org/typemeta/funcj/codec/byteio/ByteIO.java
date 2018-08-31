package org.typemeta.funcj.codec.byteio;

import java.io.*;

/**
 * ByteIO wraps either a DataInput or a DataOutput.
 */
public abstract class ByteIO {
    static ByteIO.Input ofInputStream(InputStream is) {
        return new Input(new DataInputStream(is));
    }

    static ByteIO.Input ofDataInput(DataInput input) {
        return new Input(input);
        //return new Input(new DebugDataInput(System.out, input));
    }

    static ByteIO.Output ofOutputStream(OutputStream os) {
        return new Output(new DataOutputStream(os));
    }

    static ByteIO.Output ofDataOutput(DataOutput output) {
        return new Output(output);
        //return new Output(new DebugDataOutput(System.out, output));
    }

    final static class Input {
        private final DataInput input;

        Input(DataInput input) {
            this.input = input;
        }

        public DataInput input() {
            return input;
        }

        public String readString() throws IOException {
            final int len = input.readInt();
            final char[] charArray = new char[len];
            for (int i = 0; i < len; ++i) {
                charArray[i] = input.readChar();
            }
            return new String(charArray);
        }
    }

    final static class Output {
        private final DataOutput output;

        Output(DataOutput output) {
            this.output = output;
        }

        public DataOutput output() {
            return output;
        }

        public void writeString(String s) throws IOException {
            output.writeInt(s.length());
            output.writeChars(s);
        }
    }
}
/*
class DebugDataOutput implements DataOutput {
    final PrintStream debug;
    final DataOutput dataOutput;

    DebugDataOutput(PrintStream debug, DataOutput dataOutput) {
        this.debug = debug;
        this.dataOutput = dataOutput;
    }

    private void dumpStack() {
        StackTraceElement[] cause = Thread.currentThread().getStackTrace();
        for (int i = 3; i < 11; i++) {
            debug.println("    " + cause[i]);
        }
    }

    @Override
    public void write(int b) throws IOException {
        debug.println("Output: " + b);
        dataOutput.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        debug.println("Output: " + b);
        dataOutput.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        debug.println("Output: " + new String(b, off, len));
        dataOutput.write(b, off, len);
    }

    @Override
    public void writeBoolean(boolean v) throws IOException {
        debug.println("Output: " + v);
        dumpStack();
        dataOutput.writeBoolean(v);
    }

    @Override
    public void writeByte(int v) throws IOException {
        debug.println("Output: " + v);
        dataOutput.writeByte(v);
    }

    @Override
    public void writeShort(int v) throws IOException {
        debug.println("Output: " + v);
        dataOutput.writeShort(v);
    }

    @Override
    public void writeChar(int v) throws IOException {
        debug.println("Output: " + v);
        dataOutput.writeChar(v);
    }

    @Override
    public void writeInt(int v) throws IOException {
        debug.println("Output: " + v);
        dataOutput.writeInt(v);
    }

    @Override
    public void writeLong(long v) throws IOException {
        debug.println("Output: " + v);
        dataOutput.writeLong(v);
    }

    @Override
    public void writeFloat(float v) throws IOException {
        debug.println("Output: " + v);
        dataOutput.writeFloat(v);
    }

    @Override
    public void writeDouble(double v) throws IOException {
        debug.println("Output: " + v);
        dataOutput.writeDouble(v);
    }

    @Override
    public void writeBytes(String s) throws IOException {
        debug.println("Output: " + s);
        dataOutput.writeBytes(s);
    }

    @Override
    public void writeChars(String s) throws IOException {
        debug.println("Output: " + s);
        dataOutput.writeChars(s);
    }

    @Override
    public void writeUTF(String s) throws IOException {
        debug.println("Output: " + s);
        dataOutput.writeUTF(s);
    }
}

class DebugDataInput implements DataInput {
    final PrintStream debug;
    final DataInput dataInput;

    DebugDataInput(PrintStream debug, DataInput dataInput) {
        this.debug = debug;
        this.dataInput = dataInput;
    }

    private void dumpStack() {
        StackTraceElement[] cause = Thread.currentThread().getStackTrace();
        for (int i = 3; i < 11; i++) {
            debug.println("    " + cause[i]);
        }
    }

    @Override
    public void readFully(byte[] b) throws IOException {
        debug.println("Input: " + b);
        dumpStack();
        dataInput.readFully(b);
    }

    @Override
    public void readFully(byte[] b, int off, int len) throws IOException {
        debug.println("Input: " + new String(b, off, len));
        dumpStack();
        dataInput.readFully(b);
    }

    @Override
    public int skipBytes(int n) throws IOException {
        debug.println("Input: " + n);
        return dataInput.skipBytes(n);
    }

    @Override
    public boolean readBoolean() throws IOException {
        boolean b = dataInput.readBoolean();
        debug.println("Input: " + b);
        dumpStack();
        return b;
    }

    @Override
    public byte readByte() throws IOException {
        byte b = dataInput.readByte();
        debug.println("Input: " + b);
        dumpStack();
        return b;
    }

    @Override
    public int readUnsignedByte() throws IOException {
        int b = dataInput.readUnsignedByte();
        debug.println("Input: " + b);
        dumpStack();
        return b;
    }

    @Override
    public short readShort() throws IOException {
        short b = dataInput.readShort();
        debug.println("Input: " + b);
        dumpStack();
        return b;
    }

    @Override
    public int readUnsignedShort() throws IOException {
        int b = dataInput.readUnsignedShort();
        debug.println("Input: " + b);
        dumpStack();
        return b;
    }

    @Override
    public char readChar() throws IOException {
        char b = dataInput.readChar();
        debug.println("Input: " + b);
        dumpStack();
        return b;
    }

    @Override
    public int readInt() throws IOException {
        int b = dataInput.readInt();
        debug.println("Input: " + b);
        dumpStack();
        return b;
    }

    @Override
    public long readLong() throws IOException {
        long b = dataInput.readLong();
        debug.println("Input: " + b);
        dumpStack();
        return b;
    }

    @Override
    public float readFloat() throws IOException {
        float b = dataInput.readFloat();
        debug.println("Input: " + b);
        dumpStack();
        return b;
    }

    @Override
    public double readDouble() throws IOException {
        double b = dataInput.readDouble();
        debug.println("Input: " + b);
        dumpStack();
        return b;
    }

    @Override
    public String readLine() throws IOException {
        String b = dataInput.readLine();
        debug.println("Input: " + b);
        dumpStack();
        return b;
    }

    @Override
    public String readUTF() throws IOException {
        String b = dataInput.readUTF();
        debug.println("Input: " + b);
        dumpStack();
        return b;
    }
}
*/