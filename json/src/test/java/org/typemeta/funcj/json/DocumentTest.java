package org.typemeta.funcj.json;

import org.junit.Test;
import org.typemeta.funcj.json.model.JSValue;

import java.util.Arrays;

import static org.junit.Assert.*;
import static org.typemeta.funcj.json.model.JSAPI.*;

public class DocumentTest {

    private static final JSValue testNode =
            obj(
                    field("numbers", arr(num(1.2), num(3.4), num(4.5))),
                    field("strings", arr(str("abcd"), str("efgh"), str("ijkl"))),
                    field("objects", arr(
                            obj(
                                    field("a", num(1)),
                                    field("b", num(2))
                            ),
                            obj(
                                    field("c", num(3)),
                                    field("d", num(4))
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
        final String text = node.toString(width);
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
