package org.typemeta.funcj.codec.xml.io;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.typemeta.funcj.codec.xml.io.XmlUtils.escapeTextChar;

public class XmlUtilsTest {
    private static void checkCleanString(String s) {
        assertEquals(s, escapeTextChar(s));
    }

    @Test
    public void testCleanStrings() {
        Arrays.stream(new String[]{
                "", "ABCD01234", "\n\r\t ", "  A  B  "
        }).forEach(XmlUtilsTest::checkCleanString);
    }


    @Test
    public void testDirtyStrings() {
        assertEquals("&amp;", escapeTextChar("&"));
        assertEquals("&amp; ", escapeTextChar("& "));
        assertEquals(" &amp;", escapeTextChar(" &"));
        assertEquals(" &amp; ", escapeTextChar(" & "));
        assertEquals("&amp;&amp;", escapeTextChar("&&"));
        assertEquals("A&amp;B", escapeTextChar("A&B"));
        assertEquals("A&amp;B&amp;", escapeTextChar("A&B&"));
        assertEquals("&amp;A&amp;", escapeTextChar("&A&"));
        assertEquals("&amp;&lt;&gt;", escapeTextChar("&<>"));
    }
}
