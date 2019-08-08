package org.typemeta.funcj.json.algebras;

import org.junit.Test;
import org.typemeta.funcj.json.comb.JsonCombParser;
import org.typemeta.funcj.json.model.JsValue;

import java.util.Arrays;

import static org.junit.Assert.*;
import static org.typemeta.funcj.json.TestData.testValue;

public class JsonToStrDocTest {

    @Test
    public void testJsonToString() {
        final String str = testValue.toString();
        assertEquals("JSON value toString expected length", 131, str.length());

        final JsValue value2 = JsonCombParser.parse(str).getOrThrow();
        assertEquals("Round-tripped JSON value", testValue, value2);
    }

    @Test
    public void testJsonNode20() {
        checkJsonNode(testValue, 27, 20);
    }

    @Test
    public void testJsonNode40() {
        checkJsonNode(testValue, 14, 40);
    }

    @Test
    public void testJsonNode80() {
        checkJsonNode(testValue, 7, 60);
    }

    private static void checkJsonNode(JsValue jsv, int lines, int width) {
        final String text = JsonToDoc.toString(jsv, JsValue.Formatter.DEFAULT_INDENT_SIZE, width);
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
