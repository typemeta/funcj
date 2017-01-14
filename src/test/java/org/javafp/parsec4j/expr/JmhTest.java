package org.javafp.parsec4j.expr;

import org.javafp.parsec4j.*;
import org.junit.*;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.*;
import org.openjdk.jmh.runner.options.*;

import java.io.IOException;

public class JmhTest {

//    public static void main(String[] args) throws IOException, RunnerException {
//        System.in.read();
//        new JmhTest().runJmh();
//    }
    @Test
    public void runJmh() throws RunnerException, IOException {
        final Options opt = new OptionsBuilder()
            .jvmArgs("-XX:+UnlockCommercialFeatures")
                .include(JmhTest.class.getSimpleName())
                .warmupIterations(10)
                .measurementIterations(10)
                .forks(1)
                .build();
           new Runner(opt).run();
    }

    private static final P<Character, Model.Expr> parser = Grammar.parser;

    public static void main(String[] args) throws IOException {
        System.in.read();
        while (true) {
            System.out.println(testGood());
            System.out.println(testBad());
        }
    }

    @Benchmark
    public static String testGood() {
        return parser.run(Input.of("3*-max(4%+(5bp+-x),-2bp)-1")).toString();
    }

    @Benchmark
    public static String testBad() {
        return parser.run(Input.of("3*-max(4%+(5bp+-x)x,-2bp)-1z")).toString();
    }
}
