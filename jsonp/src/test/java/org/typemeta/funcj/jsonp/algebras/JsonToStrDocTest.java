package org.typemeta.funcj.jsonp.algebras;

import org.junit.Test;

import javax.json.Json;
import javax.json.JsonValue;
import java.io.StringReader;
import java.util.Arrays;

import static org.junit.Assert.*;
import static org.typemeta.funcj.jsonp.Example.testValue;

public class JsonToStrDocTest {

    @Test
    public void testJsonToString() {
        final String str = testValue.toString();
        assertEquals("JSON value toString expected length", 131, str.length());

        final JsonValue value2 = Json.createReader(new StringReader(str)).readValue();
        assertEquals("Round-tripped JSON value", testValue, value2);
    }

    @Test
    public void testJsonNode20() {
        checkJsonNode(testValue, 27, 20);
    }

    @Test
    public void testJsonNode40() {
        checkJsonNode(testValue, 10, 40);
    }

    @Test
    public void testJsonNode80() {
        checkJsonNode(testValue, 7, 60);
    }

    private static void checkJsonNode(JsonValue jsv, int lines, int width) {
        final String text = JsonToDoc.toString(jsv, width);
        //System.out.println(text);
        checkSize(text, lines, width);
    }

    private static void checkSize(String text, int expLines, int expWidth) {
        assertFalse("Formatted text is not empty", text.isEmpty());

        final String[] lines = text.split("\n");
        assertEquals("Number of lines", expLines, lines.length);

        Arrays.stream(lines)
                .map(JsonToStrDocTest::stripTrailingSpace)
                .map(String::length)
                .max(Integer::compare)
                .ifPresent(actWidth -> checkWidth(expWidth, actWidth));
    }

    private static void checkWidth(int exp, int act) {
        assertTrue(
                "Actual width (" + act + ") <= expected width (" + exp + ")",
                act <= exp);
    }

    private static String stripTrailingSpace(String s) {
        if (s.isEmpty()) {
            return s;
        } else {
            int i = s.length();
            while(Character.isWhitespace(s.charAt(i-1))) {
                --i;
            }

            return s.substring(0, i);
        }
    }
}
