package org.typemeta.funcj.control;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.junit.*;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(JUnitQuickcheck.class)
public class ValidationTest {
    private static Validation<String, Double> parseDbl(String s) {
        return Validation.of(
            () -> Double.parseDouble(s),
            Exception::getMessage
        );
    }

    private static Validation<String, Integer> parseInt(String s) {
        return Validation.of(
            () -> Integer.parseInt(s),
            Exception::getMessage
        );
    }

    @Test
    public void testAndMapGoodGood() {
        final Validation<String, Double> r =
            parseInt("1234")
                .and(parseDbl("1.2"))
                .map(i -> d -> i+d);
        Assert.assertTrue("Validation is success", r.isSuccess());
    }

    @Test
    public void testAndMapGoodBad() {
        final Validation<String, Double> r =
            parseInt("1234")
                .and(parseDbl("1.2z"))
                .map(i -> d -> i+d);
        Assert.assertFalse("Validation is failure", r.isSuccess());
    }

    @Test
    public void testAndMapBadGood() {
        final Validation<String, Double> r =
            parseInt("1234z")
                .and(parseDbl("1.2"))
                .map(i -> d -> i+d);
        Assert.assertFalse("Validation is failure", r.isSuccess());
    }

    @Test
    public void testAndMapBadBad() {
        final Validation<String, Double> r =
            parseInt("1234z")
                .and(parseDbl("1.2z"))
                .map(i -> d -> i+d);
        Assert.assertFalse("Validation is failure", r.isSuccess());
    }

    private static <String, T> Validation<String, T> failure(String msg) {
        return Validation.failure(msg);
    }

    @Property
    public void isSuccess(char c) {
        assertTrue(Validation.success(c).isSuccess());
        assertFalse(failure("fail").isSuccess());
    }

    @Property
    public void handle (char c) {
        Validation.success(c).handle(l -> {throw new RuntimeException("Unexpected failure value");}, r -> {});
        failure("fail").handle(l -> {}, r -> {throw new RuntimeException("Unexpected success value");});
    }

    @Property
    public void match(char c) {
        assertTrue(Validation.success(c).match(l -> false, r -> true));
        assertFalse(failure("fail").match(l -> false, r -> true));
    }

    @Property
    public void map(char c) {
        assertEquals(Validation.success(String.valueOf(c)), Validation.success(c).map(Object::toString));
        assertEquals(failure("fail"), failure("fail").map(Object::toString));
    }

    @Property
    public void apply(char c) {
        assertEquals(Validation.success(String.valueOf(c)), Validation.success(c).apply(Validation.success(Object::toString)));
        assertEquals(failure("fail"), Validation.success(c).apply(failure("fail")));
        assertEquals(failure("fail"), failure("fail").apply(Validation.success(Object::toString)));
    }
}
