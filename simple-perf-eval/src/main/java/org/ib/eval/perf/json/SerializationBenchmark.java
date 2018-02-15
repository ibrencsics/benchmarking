package org.ib.eval.perf.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.ib.eval.perf.json.Sut.LIST1;
import static org.ib.eval.perf.json.Sut.MAP1;

public class SerializationBenchmark {

    private static final Sut SUT1 = new Sut("test1", 1, 1L, new Date(), LIST1, MAP1);
    private static Gson GSON = new GsonBuilder().create();
    private static ObjectMapper OBJECTMAPPER = new ObjectMapper();

    static {
        OBJECTMAPPER.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }

    static final int size = 1000;

    static final Sut[] xs = new Sut[size];

    static {
        for (int i = 0; i < size; i++) {
            xs[i] = new Sut("test", i, 1L, new Date(), LIST1, MAP1);
        }
    }

    @Benchmark
    public void javaSerialization(final Blackhole bh) throws RuntimeException {
        for (Sut object : xs) {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream out = new ObjectOutputStream(baos);
                out.writeObject(object);
                out.close();

                ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
                ObjectInputStream in = new ObjectInputStream(bais);
                Object o = in.readObject();
                bais.close();

                bh.consume(o.toString());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Benchmark
    public void gsonSerialization(final Blackhole bh) {
        for (Sut sut : xs) {
            final Gson gson = new GsonBuilder().create();
            String json = gson.toJson(sut);
            Sut o = gson.fromJson(json, Sut.class);
            bh.consume(o.toString());
        }
    }

    @Benchmark
    public void gsonSerializationCached(final Blackhole bh) {
        for (Sut sut : xs) {
            String json = GSON.toJson(sut);
            Sut o = GSON.fromJson(json, Sut.class);
            bh.consume(o.toString());
        }
    }

    @Benchmark
    public void jacksonSerialization(final Blackhole bh) {
        for (Sut sut : xs) {
            try {
                String json = OBJECTMAPPER.writeValueAsString(sut);
                Sut o = OBJECTMAPPER.readValue(json, Sut.class);
                bh.consume(o.toString());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public Gson newGson() {
        return new GsonBuilder().create();
    }
}
