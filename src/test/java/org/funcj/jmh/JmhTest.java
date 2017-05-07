package org.funcj.jmh;

import org.openjdk.jmh.runner.*;
import org.openjdk.jmh.runner.options.*;

public class JmhTest {

    public static void main(String args[]) throws RunnerException {
        new JmhTest().runJmh();
    }

    public void runJmh() throws RunnerException {
        final Options opt = new OptionsBuilder()
                .jvmArgs("-XX:+UnlockCommercialFeatures")
                .include(".*Test")
                .warmupIterations(20)
                .measurementIterations(20)
                .addProfiler(FlightRecordingProfiler.class)
                .forks(1)
                .build();
           new Runner(opt).run();
    }
}
