package org.javafp.data;

import org.junit.*;

public class UnitTest {
    @Test
    public void test() {
        Assert.assertTrue(
            "String representation contains the word UNIT",
            Unit.UNIT.toString().contains("UNIT"));
    }
}
