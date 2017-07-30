package org.funcj.document;

import org.funcj.json.*;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class DocumentTest {

    private static final JSValue testNode =
            JSObject.of(
                    JSObject.field("numbers", JSArray.of(JSNumber.of(1.2), JSNumber.of(3.4), JSNumber.of(4.5))),
                    JSObject.field("strings", JSArray.of(JSString.of("abcd"), JSString.of("efgh"), JSString.of("ijkl"))),
                    JSObject.field("objects", JSArray.of(
                            JSObject.of(
                                    JSObject.field("a", JSNumber.of(1)),
                                    JSObject.field("b", JSNumber.of(2))
                            ),
                            JSObject.of(
                                    JSObject.field("c", JSNumber.of(3)),
                                    JSObject.field("d", JSNumber.of(4))
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
