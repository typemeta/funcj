package org.funcj.document;

import org.funcj.json.Node;
import org.junit.Test;

import java.util.Arrays;

import static org.funcj.json.Node.*;
import static org.junit.Assert.*;

public class DocumentTest {

    private static final Node testNode =
            object(
                    entry("numbers", array(number(1.2), number(3.4), number(4.5))),
                    entry("strings", array(string("abcd"), string("efgh"), string("ijkl"))),
                    entry("objects", array(
                            object(
                                    entry("a", number(1)),
                                    entry("b", number(2))
                            ),
                            object(
                                    entry("c", number(3)),
                                    entry("d", number(4))
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

    private static void checkJsonNode(Node node, int lines, int width) {
        final String text = node.toJson(width);
        System.out.println(text);
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
