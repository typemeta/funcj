package org.javafp.json;

import org.javafp.parsec4j.Result;
import org.javafp.util.Chr;
import org.junit.Test;
import org.openjdk.jmh.annotations.Benchmark;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class JmhTest {
    private static final String test0 = "null";

    private static final String test1 = "true";

    private static final String test2 = "12.0";

    private static final String test3 = "[]";

    private static final String test4 = "[]";

    private static final String test5 = "{\"key\":[1.0,\"value\",true,null]}";

    private static final String[] tests = {
        test0, test1, test2, test3, test4, test5
    };

    private static String loadFile(String name) throws IOException {
        final InputStream is =
            Optional.ofNullable(JmhTest.class.getResourceAsStream(name))
                    .orElseThrow(() -> new RuntimeException("File '" + name + "' not found"));
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(is))) {
            return buffer.lines().collect(Collectors.joining("\n"));
        }
    }

    @Test
    public void testRoundTrip() {
        Arrays.stream(tests).forEach(JmhTest::roundTrip);
    }

    private static void roundTrip(String json) {
        //System.out.println(" Parsing: " + json);
        final Result<Chr, Node> result = Grammar.parse(json);
        final Node node = result.getOrThrow();

        final String json2 = node.toJson();
        //System.out.println(json2);

        assertEquals(json, json2);
    }

    @Test
    @Benchmark
    public void testSuccessParse() throws IOException {
        final String json = loadFile("/example.json");
        final Result<Chr, Node> result = Grammar.parse(json);
        final Node node = result.getOrThrow();
        //System.out.println(node.toJson());
    }
}
