package org.typemeta.funcj.codec.json.io;

import org.typemeta.funcj.control.Try;
import org.typemeta.funcj.util.Exceptions;

import java.io.*;
import java.util.Optional;

public abstract class FileUtils {

    public static Try<BufferedReader> openResource(String name) {
        return Try.of(() -> {
            final InputStream is =
                    Optional.ofNullable(FileUtils.class.getResourceAsStream(name))
                            .orElseThrow(() -> new RuntimeException("File '" + name + "' not found"));
            return new BufferedReader(new InputStreamReader(is));
        });
    }

    public static String read(Reader rdr) {
        try {
            final StringBuilder sb = new StringBuilder();
            final char[] buff = new char[1024];
            int l = rdr.read(buff);
            while (l > 0) {
                for (int i = 0; i < l; ++i) {
                    sb.append(buff[i]);
                }

                l = rdr.read(buff);
            }

            return sb.toString();
        } catch (IOException ex) {
            return Exceptions.throwUnchecked(ex);
        }
    }
}
