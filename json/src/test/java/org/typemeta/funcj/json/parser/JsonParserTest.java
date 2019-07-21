package org.typemeta.funcj.json.parser;

import org.junit.Test;
import org.typemeta.funcj.control.Try;
import org.typemeta.funcj.data.Unit;
import org.typemeta.funcj.json.model.*;
import org.typemeta.funcj.tuples.Tuple2;

import javax.json.*;
import java.io.*;
import java.util.*;

import static java.util.stream.Collectors.*;
import static org.junit.Assert.*;

public class JsonParserTest {
    private static final String test0 = "[null]";

    private static final String test1 = "[true, false ]";

    private static final String test2 = "[ 12 ]";

    private static final String test3 = " [] ";

    private static final String test4 = "[ null  , true,0, 1.2 ,\"test\" ] ";

    private static final String test5 = "{}";

    private static final String test6 = "{\"key\": [1.1,\"value\" ,true,null], \"key2 \" : { }}";

    private static final String test7 = "[\"A\\uabcdB\"]";

    private static final String test8 = "[\"\\u0123\"]";

    private static final String test9 = "[\"\\ucafe\\ubabe\\uab98\\ufcde\\ubcda\\uef4a\\b\\f\\n\\r\\t\"]";

    private static final String test10 = "{\"ids\":[1.2,\"test\",null,true]}";

    private static final String test11 =
            "[1.0E7 ,1.2E7, -1.2E7, 1.2E-7, -1.2E-7 , 12.34E58," + " -12.34e58, -12.34567890e-058 ,1.234567890E+34]";

    private static final String[] tests = {
            test0, test1, test2, test3, test4, test5, test6, test7, test8, test9, test10, test11
    };

    @Test
    public void testParse() {
        Arrays.stream(tests).forEach(FileUtils::checkParse);
    }

    private static final String json =
            FileUtils.openResource("/example.json")
                    .map(FileUtils::read)
                    .orElseThrow();

    @Test
    public void testSuccessParse() {
        final JsValue node = JsonParser.parse(json);
        final String json2 = node.toString(100);

        //System.out.println(node.toString());
        assertEquals("Round-tripped JSON", json, json2);
    }

    @Test
    public void testJsonSuite() {
        FileUtils.openDir("json").orElseThrow().stream()
                .filter(t2 -> t2._1.equals("fail10.json"))
                .forEach(t2 -> t2.map2(FileUtils::read)
                        .applyFrom(FileUtils::roundTrip));
    }
}

abstract class FileUtils {

    static Try<BufferedReader> openResource(String name) {
        return Try.of(() -> {
            final InputStream is =
                    Optional.ofNullable(FileUtils.class.getResourceAsStream(name))
                            .orElseThrow(() -> new RuntimeException("File '" + name + "' not found"));
            return new BufferedReader(new InputStreamReader(is));
        });
    }

    static Try<List<Tuple2<String, BufferedReader>>> openDir(String name) {
        final String dir = "/" + name + "/";

        return Try.sequence(
                openResource(dir)
                        .orElseThrow()
                        .lines()
                        .map(file -> openResource(dir + file)
                                .map(br -> Tuple2.of(file, br)))
                        .collect(toList())
        );
    }

    static String read(BufferedReader rdr) {
        return rdr.lines().collect(joining("\n"));
    }

    static Unit roundTrip(String name, String json) {
        if (!name.equals("fail10.json") && !checkParse(json)) {
            throw new RuntimeException(name + " failed to match after parsing");
        }

        final Try<JsValue> result = Try.of(() -> JsonParser.parse(json));

        if (name.startsWith("fail")) {
            assertFalse("Parse expected to fail: " + name, result.isSuccess());
        } else {
            assertTrue("Parse expected to succeed: " + name, result.isSuccess());

            final JsValue node = result.orElseThrow();
            final String json2 = node.toString(100);

            final JsValue node2 = JsonParser.parse(json);

            //System.out.println(node.toString());
            assertEquals("Round-tripped JSON: " + name, node, node2);
        }

        return Unit.UNIT;
    }

    static boolean checkParse(String json) {
        final Try<JsonStructure> expTry = Try.of(() -> {
            final StringReader rdr = new StringReader(json);
            final JsonReader jr = Json.createReader(rdr);
            final JsonStructure js = jr.read();
            if (rdr.read() != -1) {
                throw null;
            }
            return js;
        });
        final Try<JsValue> actTry = Try.of(() -> JsonParser.parse(json));

        if (expTry.isSuccess() != actTry.isSuccess()) {
            return false;
        }

        expTry.forEach(exp -> Compare.compare("/", exp, actTry.orElseThrow()));

        return true;
    }

    private static class Compare {

        private static final double EPS = 1e-16;

        static void compare(String path, JsonValue exp, JsValue act) {
            if (exp instanceof JsonObject) {
                if (act instanceof JsObject && compare(path, (JsonObject)exp, (JsObject)act)) {
                    return;
                }
            } else if (exp instanceof JsonArray) {
                if (act instanceof JsArray && compare(path, (JsonArray)exp, (JsArray)act)) {
                    return;
                }
            } else if (exp instanceof JsonString) {
                if (act instanceof JsString && compare(path, (JsonString)exp, (JsString)act)) {
                    return;
                }
            } else if (exp instanceof JsonNumber) {
                if (act instanceof JsNumber && compare(path, (JsonNumber)exp, (JsNumber)act)) {
                    return;
                }
            } else if (exp.equals(JsonValue.NULL)) {
                if (act instanceof JsNull) {
                    return;
                }
            } else if (exp.equals(JsonValue.TRUE)) {
                if (act.equals(JsBool.TRUE)) {
                    return;
                }
            } else if (exp.equals(JsonValue.FALSE)) {
                if (act.equals(JsBool.FALSE)) {
                    return;
                }
            }

            throw new RuntimeException(
                    "Mismatch at " + path +
                            "\nExpected: " + exp +
                            "\nActual: " + act
            );
        }

        static boolean compare(String path, JsonObject exp, JsObject act) {
            final Set<String> keys = exp.keySet();
            if (keys.equals(act.keySet())) {
                for (String key : keys) {
                    compare(path + "/" + key, exp.get(key), act.get(key));
                }
                return true;
            } else {
                throw new RuntimeException("Keys don't match at " + path);
            }
        }

        static boolean compare(String path, JsonArray exp, JsArray act) {
            if (exp.size() == act.size()) {
                for (int i = 0; i < exp.size(); ++i) {
                    compare(path + "[" + i + "]", exp.get(i), act.get(i));
                }
                return true;
            } else {
                throw new RuntimeException("Number of elements don't match at " + path);
            }
        }

        static boolean compare(String path, JsonString exp, JsString act) {
            return exp.getString().equals(act.value());
        }

        static boolean compare(String path, JsonNumber exp, JsNumber act) {
            return Math.abs(exp.doubleValue() - act.value()) < EPS;
        }
    }

}
