package org.typemeta.funcj.database;

import org.slf4j.*;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import static java.util.stream.Collectors.joining;

abstract class SqlUtils {
    private static final Logger logger = LoggerFactory.getLogger(SqlUtils.class);

    static final String SQL_SEP_TOKEN = ";;";

    static Stream<String> loadResource(String path) {
        final String text =
                Optional.ofNullable(DatabaseExtractorTest.class.getResourceAsStream(path))
                        .map(InputStreamReader::new)
                        .map(is -> {
                            try (BufferedReader br = new BufferedReader(is)) {
                                return br.lines().collect(joining("\n"));
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                        }).orElseThrow(() -> new RuntimeException("Resource '" + path + "' not found"));

        final Iterator<String> iter = new Iterator<String>() {
            int s = 0;
            int e = nextIndex();
            String next = text.substring(0, e);

            private int nextIndex() {
                int e = text.indexOf(SQL_SEP_TOKEN, s);
                return (e == -1) ? text.length() : e;
            }

            @Override
            public boolean hasNext() {
                return next != null;
            }

            @Override
            public String next() {
                final String result = next;
                s = e + SQL_SEP_TOKEN.length();
                if (s > text.length()) {
                    next = null;
                } else {
                    e = nextIndex();
                    next = (e == -1 ? text.substring(s) : text.substring(s, e)).trim();
                    if (next.isEmpty()) {
                        next = null;
                    }
                }
                return result;
            }
        };

        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(
                        iter,
                        Spliterator.ORDERED | Spliterator.NONNULL
                ), false
        );
    }
}
