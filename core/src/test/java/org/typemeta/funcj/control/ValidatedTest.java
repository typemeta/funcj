package org.typemeta.funcj.control;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.junit.*;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(JUnitQuickcheck.class)
public class ValidatedTest {
    private static Validated<String, Double> parseDbl(String s) {
        return Validated.of(
            () -> Double.parseDouble(s),
            Exception::getMessage
        );
    }

    private static Validated<String, Integer> parseInt(String s) {
        return Validated.of(
            () -> Integer.parseInt(s),
            Exception::getMessage
        );
    }

    @Test
    public void testAndMapGoodGood() {
        final Validated<String, Double> r =
            parseInt("1234")
                .and(parseDbl("1.2"))
                .map(i -> d -> i+d);
        Assert.assertTrue("Validated is success", r.isSuccess());
    }

    @Test
    public void testAndMapGoodBad() {
        final Validated<String, Double> r =
            parseInt("1234")
                .and(parseDbl("1.2z"))
                .map(i -> d -> i+d);
        Assert.assertFalse("Validated is failure", r.isSuccess());
    }

    @Test
    public void testAndMapBadGood() {
        final Validated<String, Double> r =
            parseInt("1234z")
                .and(parseDbl("1.2"))
                .map(i -> d -> i+d);
        Assert.assertFalse("Validated is failure", r.isSuccess());
    }

    @Test
    public void testAndMapBadBad() {
        final Validated<String, Double> r =
            parseInt("1234z")
                .and(parseDbl("1.2z"))
                .map(i -> d -> i+d);
        Assert.assertFalse("Validated is failure", r.isSuccess());
    }

    private static <String, T> Validated<String, T> failure(String msg) {
        return Validated.failure(msg);
    }

    @Property
    public void isSuccess(char c) {
        assertTrue(Validated.success(c).isSuccess());
        assertFalse(failure("fail").isSuccess());
    }

    @Property
    public void handle (char c) {
        Validated.success(c).handle(l -> {throw new RuntimeException("Unexpected failure value");}, r -> {});
        failure("fail").handle(l -> {}, r -> {throw new RuntimeException("Unexpected success value");});
    }

    @Property
    public void match(char c) {
        assertTrue(Validated.success(c).match(l -> false, r -> true));
        assertFalse(failure("fail").match(l -> false, r -> true));
    }

    @Property
    public void map(char c) {
        assertEquals(Validated.success(String.valueOf(c)), Validated.success(c).map(Object::toString));
        assertEquals(failure("fail"), failure("fail").map(Object::toString));
    }

    @Property
    public void apply(char c) {
        assertEquals(Validated.success(String.valueOf(c)), Validated.success(c).apply(Validated.success(Object::toString)));
        assertEquals(failure("fail"), Validated.success(c).apply(failure("fail")));
        assertEquals(failure("fail"), failure("fail").apply(Validated.success(Object::toString)));
    }
}
