package org.funcj.util;

import org.funcj.util.ClassType;
import org.junit.Test;

import static org.junit.Assert.*;

public class ClassTypeTest {
    @Test
    public void testOptSuccess() {
        final Object test = "test";
        ClassType.of(String.class)
            .cast(test)
            .filter("test"::equals)
            .orElseThrow(IllegalStateException::new);
    }

    @Test
    public void testOptFail() {
        final Object test = 1.234;
        ClassType.of(String.class)
            .cast(test)
            .ifPresent(IllegalStateException::new);
    }

    @Test
    public void testSuccess() {
        final Object test = "test";
        assertTrue(
            ClassType.of(String.class)
                .match(test, "test"::equals, x -> false)
        );
    }

    @Test
    public void testFail() {
        final Object test = 1.234;
        assertFalse(
            ClassType.of(String.class)
                .match(test, "test"::equals, x -> false)
        );
    }
}
