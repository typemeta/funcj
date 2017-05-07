package org.funcj.json;

import org.funcj.control.Try;
import org.funcj.data.Chr;
import org.funcj.parser.*;
import org.junit.Test;
import org.openjdk.jmh.annotations.Benchmark;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class JsonTest {
    private static final String test0 = "null";

    private static final String test1 = "true";

    private static final String test2 = "12";

    private static final String test3 = "[]";

    private static final String test4 = "[null,true,0,1.2,\"test\"]";

    private static final String test5 = "{}";

    private static final String test6 = "{\"key\":[1.1,\"value\",true,null]}";

    private static final String[] tests = {
        test0, test1, test2, test3, test4, test5, test6
    };

    @Test
    public void testRoundTrip() {
        Arrays.stream(tests).forEach(JsonTest::roundTrip);
    }

    private static void roundTrip(String json) {
        //System.out.println(" Parsing: " + json);
        final Result<Chr, Node> result = Grammar.parse(json);
        final Node node = result.getOrThrow();

        final String json2 = node.toString();
        //System.out.println(json2);

        assertEquals("Round-tripped JSON", json, json2);
    }

    private static final String json =
        FileUtils.openFile("/example.json")
            .flatMap(FileUtils::loadFile)
            .get();

    @Test
    public void testSuccessParse() throws IOException {
        final Result<Chr, Node> result =
            Grammar.parser.run(
                Input.of(FileUtils.openFile("/example.json").get()));
        final Node node = result.getOrThrow();
        final String json2 = node.toJson(100);

        //System.out.println(node.toString());
        assertEquals("Round-tripped JSON", json, json2);
    }

    @Benchmark
    public void benchSuccessParse() throws IOException {
        final Result<Chr, Node> result = Grammar.parse(json);
        final Node node = result.getOrThrow();
    }
}

abstract class FileUtils {

    static Try<Reader> openFile(String name) {
        final InputStream is =
            Optional.ofNullable(JsonTest.class.getResourceAsStream(name))
                .orElseThrow(() -> new RuntimeException("File '" + name + "' not found"));
        return Try.of(() -> new BufferedReader(new InputStreamReader(is)));
    }

    static Try<String> loadFile(Reader rdr) {
        return Try.of(() -> {
            try (BufferedReader buffer = new BufferedReader(rdr)) {
                return buffer.lines().collect(Collectors.joining("\n"));
            }
        });
    }

}