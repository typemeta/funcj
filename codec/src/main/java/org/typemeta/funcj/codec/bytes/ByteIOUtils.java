package org.typemeta.funcj.codec.bytes;

import java.io.*;

public abstract class ByteIOUtils {
    public String readString(DataInput in) throws IOException {
        final int len = in.readInt();
        final char[] charArray = new char[len];
        for (int i = 0; i < len; ++i) {
            charArray[i] = in.readChar();
        }
        return new String(charArray);
    }

    public void writeString(String s, DataOutput out) throws IOException {
        out.writeInt(s.length());
        out.writeChars(s);
    }
}
