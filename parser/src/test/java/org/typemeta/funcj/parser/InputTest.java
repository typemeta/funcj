package org.typemeta.funcj.parser;

import org.junit.Test;
import org.typemeta.funcj.data.Chr;

import java.io.CharArrayReader;

import static org.junit.Assert.*;

public class InputTest {
    private static final char[] charData = "ABCDEFGH".toCharArray();

    @Test
    public void testStringInput() {
        testInput(Input.of(charData));
    }

    @Test
    public void testReaderInput() {
        testInput(Input.of(new CharArrayReader(charData)));
    }

    private void testInput(Input<Chr> input) {
        Input<Chr> curr = input;

        for (char c : charData) {
            assertFalse("", curr.isEof());
            assertEquals("", c, curr.get().charValue());


            assertFalse("", curr.isEof());
            assertEquals("", c, curr.get().charValue());

            final Input<Chr> next = curr.next();

            curr.next();

            curr = next;
        }

        assertTrue("", curr.isEof());
    }
}
