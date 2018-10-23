package org.typemeta.funcj.codec.xmls.io;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.typemeta.funcj.codec.xmls.io.XmlUtils.escapeXml;

public class XmlUtilsTest {
    private static void checkCleanString(String s) {
        assertEquals(s, escapeXml(s));
    }

    @Test
    public void testCleanStrings() {
        Arrays.stream(new String[]{
                "", "ABCD01234", "\n\r\t ", "  A  B  "
        }).forEach(XmlUtilsTest::checkCleanString);
    }


    @Test
    public void testDirtyStrings() {
        assertEquals("&amp;", escapeXml("&"));
        assertEquals("&amp; ", escapeXml("& "));
        assertEquals(" &amp;", escapeXml(" &"));
        assertEquals(" &amp; ", escapeXml(" & "));
        assertEquals("&amp;&amp;", escapeXml("&&"));
        assertEquals("A&amp;B", escapeXml("A&B"));
        assertEquals("A&amp;B&amp;", escapeXml("A&B&"));
        assertEquals("&amp;A&amp;", escapeXml("&A&"));
        assertEquals("&amp;&lt;&gt;", escapeXml("&<>"));
    }
}
