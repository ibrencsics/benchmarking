package org.ib.eval.perf.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.infra.Blackhole;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.ib.eval.perf.json.Sut.LIST1;
import static org.ib.eval.perf.json.Sut.MAP1;

// In the module root, compile/run with:
// mvn clean package
// java -jar target/benchmarks.jar 'SerializationBenchmark2.*'

public class SerializationBenchmark2 {

    private static final Sut SUT1 = new Sut("test1", 1, 1L, new Date(), LIST1, MAP1);
    private static Gson GSON = new GsonBuilder().create();
    private static ObjectMapper OBJECTMAPPER = new ObjectMapper();

    static {
        OBJECTMAPPER.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public String javaSerialization() throws RuntimeException {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(baos);
            out.writeObject(SUT1);
            out.close();

            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream in = new ObjectInputStream(bais);
            Object o = in.readObject();
            bais.close();

            return o.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public String gsonSerialization() {
        final Gson gson = new GsonBuilder().create();
        String json = gson.toJson(SUT1);
        Sut o = gson.fromJson(json, Sut.class);
        return o.toString();
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public String gsonSerializationCached() {
        String json = GSON.toJson(SUT1);
        Sut o = GSON.fromJson(json, Sut.class);
        return o.toString();
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void jacksonSerialization(final Blackhole bh) {
        try {
            String json = OBJECTMAPPER.writeValueAsString(SUT1);
            Sut o = OBJECTMAPPER.readValue(json, Sut.class);
            bh.consume(o.toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public Gson newGson() {
        return new GsonBuilder().create();
    }
}
