package org.typemeta.funcj.codec.jsons;

import org.typemeta.funcj.control.Try;

import java.io.*;
import java.util.Optional;

abstract class FileUtils {

    static Try<BufferedReader> openResource(String name) {
        return Try.of(() -> {
            final InputStream is =
                    Optional.ofNullable(FileUtils.class.getResourceAsStream(name))
                            .orElseThrow(() -> new RuntimeException("File '" + name + "' not found"));
            return new BufferedReader(new InputStreamReader(is));
        });
    }
}
