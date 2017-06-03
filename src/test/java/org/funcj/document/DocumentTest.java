package org.funcj.document;

import org.funcj.json.*;
import org.funcj.json.Json;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class DocumentTest {

    private static final JSValue testNode =
            Json.object(
                    Json.entry("numbers", Json.array(Json.number(1.2), Json.number(3.4), Json.number(4.5))),
                    Json.entry("strings", Json.array(Json.string("abcd"), Json.string("efgh"), Json.string("ijkl"))),
                    Json.entry("objects", Json.array(
                            Json.object(
                                    Json.entry("a", Json.number(1)),
                                    Json.entry("b", Json.number(2))
                            ),
                            Json.object(
                                    Json.entry("c", Json.number(3)),
                                    Json.entry("d", Json.number(4))
                            )
                    ))
            );

    @Test
    public void testJsonNode20() {
        checkJsonNode(testNode, 22, 20);
    }

    @Test
    public void testJsonNode40() {
        checkJsonNode(testNode, 12, 40);
    }

    @Test
    public void testJsonNode80() {
        checkJsonNode(testNode, 5, 60);
    }

    private static void checkJsonNode(JSValue node, int lines, int width) {
        final String text = node.toJson(width);
        //System.out.println(text);
        checkSize(text, lines, width);
    }

    private static void checkSize(String text, int expLines, int expWidth) {
        assertFalse("Formatted text is not empty", text.isEmpty());

        final String[] lines = text.split("\n");
        assertEquals("Number of lines", expLines, lines.length);

        Arrays.stream(lines)
                .map(DocumentTest::stripTrailingSpace)
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
