package org.typemeta.funcj.codec;

import org.immutables.value.Value;
import org.junit.*;
import org.typemeta.funcj.codec.json.JsonCodecCore;

import java.io.*;
import java.util.*;

import static org.typemeta.funcj.codec.TestBase.printSizes;

@Value.Enclosing
public class ImmutablesTest {

    @Value.Immutable
    interface Something {
        String name();
        List<Integer> numbers();
    }

    @Value.Immutable
    interface SomeType {
        Optional<Integer> number();
        Map<String, Something> things();
    }

    static Something createSomething(String name, int x, int y) {
        return ImmutableImmutablesTest.Something.builder()
                .name(name)
                .addNumbers(x)
                .addNumbers(y)
                .build();
    }

    static SomeType createSomeType(String name, int n) {
        if (n >= 0) {
            return ImmutableImmutablesTest.SomeType.builder()
                    .number(n)
                    .putThings(name, createSomething(name + "-thing", n * 2, n * 3))
                    .build();
        } else {
            return ImmutableImmutablesTest.SomeType.builder()
                    .putThings(name, createSomething(name + "-thing", n * 2, n * 3))
                    .build();
        }
    }

    @Test
    public void test() {
        roundTrip(createSomeType("test1a", 3));
        roundTrip(createSomeType("test1b", -5));
    }

    void roundTrip(Object value) {
        final JsonCodecCore codec = Codecs.jsonCodec();
//        codec.registerArgArrayCtor(
//                ImmutableImmutablesTest.Something.class,
//                args ->
//                        ImmutableImmutablesTest.Something.builder()
//                                .name((String)args[0])
//                                .numbers((List<Integer>)args[1])
//                                .build()
//        );
        codec.registerArgArrayCtor(
                ImmutableImmutablesTest.SomeType.class,
                args ->
                        ImmutableImmutablesTest.SomeType.builder()
                                .number(Optional.ofNullable((Integer)args[0]))
                                .things((Map<String, ImmutablesTest.Something>)args[1])
                                .build()
        );

        codec.registerCodec(ImmutableImmutablesTest.Something.class)
                .field("name", Something::name, String.class)
                .field("numbers", Something::numbers, List.class)
                .map((n, ns) ->
                        ImmutableImmutablesTest.Something.builder()
                                .name(n)
                                .numbers(ns)
                                .build()
                );

        final StringWriter sw = new StringWriter();

        codec.encode(value, sw);

        if (TestBase.printData) {
            System.out.println(sw);
        }

        final String data = sw.toString();

        if (printSizes) {
            System.out.println("Encoded JSON " + value.getClass().getSimpleName() + " data size = " + data.length() + " chars");
        }

        final StringReader sr = new StringReader(data);

        final Object value2 = codec.decode(sr);

        if (!TestBase.printData && !value.equals(value2)) {
            System.out.println(sw);
        }

        Assert.assertEquals(value, value2);
    }
}
