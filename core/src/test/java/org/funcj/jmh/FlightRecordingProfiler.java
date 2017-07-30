package org.funcj.jmh;


import org.openjdk.jmh.infra.BenchmarkParams;
import org.openjdk.jmh.profile.ExternalProfiler;
import org.openjdk.jmh.results.*;
import org.openjdk.jmh.util.FileUtils;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.util.*;

public class FlightRecordingProfiler implements ExternalProfiler {
    private String startFlightRecordingOptions = "duration=60s,name=profile,settings=profile,";
    private String flightRecorderOptions = "samplethreads=true,";
    private static final String SAVE_FLIGHT_OUTPUT_TO = System.getProperty("jmh.jfr.saveTo", ".");
    private final String jfrData = FileUtils.tempFile("jfrData").getAbsolutePath();
    private static final boolean IS_SUPPORTED = ManagementFactory.getRuntimeMXBean().getInputArguments().contains("-XX:+UnlockCommercialFeatures");
    static int currentId;

    public FlightRecordingProfiler() throws IOException {
    }

    public Collection<String> addJVMInvokeOptions(BenchmarkParams params) {
        return Collections.emptyList();
    }

    public Collection<String> addJVMOptions(BenchmarkParams params) {
        this.startFlightRecordingOptions = this.startFlightRecordingOptions + "filename=" + this.jfrData;
        this.flightRecorderOptions = this.flightRecorderOptions + "settings=" + params.getJvm().replace("bin/java", "lib/jfr/profile.jfc");
        return Arrays.asList(new String[]{"-XX:+FlightRecorder", "-XX:StartFlightRecording=" + this.startFlightRecordingOptions, "-XX:FlightRecorderOptions=" + this.flightRecorderOptions});
    }

    public void beforeTrial(BenchmarkParams benchmarkParams) {
    }

    @Override
    public Collection<? extends Result> afterTrial(BenchmarkResult benchmarkResult, long pid, File stdOut, File stdErr) {
        String target = SAVE_FLIGHT_OUTPUT_TO + "/" + benchmarkResult.getParams().id() + "-" + currentId++ + ".jfr";
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        try {
            FileUtils.copy(this.jfrData, target);
            pw.println("Flight Recording output saved to " + target);
        } catch (IOException var8) {
            pw.println("Unable to save flight output to " + target);
        }

        pw.flush();
        pw.close();
        FlightRecordingProfiler.NoResult r = new FlightRecordingProfiler.NoResult(sw.toString());
        return Collections.singleton(r);
    }

    public boolean allowPrintOut() {
        return true;
    }

    public boolean allowPrintErr() {
        return false;
    }

    public boolean checkSupport(List<String> msgs) {
        msgs.add("Commercial features of the JVM need to be enabled for this profiler.");
        return IS_SUPPORTED;
    }

    public String label() {
        return "jfr";
    }

    public String getDescription() {
        return "Java Flight Recording profiler runs for every benchmark.";
    }

    private class NoResult extends Result<FlightRecordingProfiler.NoResult> {
        private final String output;

        public NoResult(String output) {
            super(ResultRole.SECONDARY, "JFR", of(0.0D / 0.0), "N/A", AggregationPolicy.SUM);
            this.output = output;
        }

        protected Aggregator<FlightRecordingProfiler.NoResult> getThreadAggregator() {
            return new FlightRecordingProfiler.NoResult.NoResultAggregator();
        }

        protected Aggregator<FlightRecordingProfiler.NoResult> getIterationAggregator() {
            return new FlightRecordingProfiler.NoResult.NoResultAggregator();
        }

        public String extendedInfo(String label) {
            return "JFR Messages:\n--------------------------------------------\n" + this.output;
        }

        private class NoResultAggregator implements Aggregator<FlightRecordingProfiler.NoResult> {
            private NoResultAggregator() {
            }

            public NoResult aggregate(Collection<FlightRecordingProfiler.NoResult> results) {
                String output = "";

                FlightRecordingProfiler.NoResult r;
                for(Iterator var3 = results.iterator(); var3.hasNext(); output = output + r.output) {
                    r = (FlightRecordingProfiler.NoResult)var3.next();
                }

                return FlightRecordingProfiler.this.new NoResult(output);
            }
        }
    }
}
