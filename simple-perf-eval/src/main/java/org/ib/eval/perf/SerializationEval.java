package org.ib.eval.perf;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class SerializationEval {

    private static final List<String> LIST1 = new ArrayList<>();
    private static final Map<String, String> MAP1 = new HashMap<>();
    static {
        LIST1.add("apple");
        LIST1.add("pear");
        LIST1.add("orange");

        MAP1.put("one", "apple");
        MAP1.put("two", "pear");
        MAP1.put("three", "orange");
    }

    private static final Sut SUT1 = new Sut("test1", 1, 1L, new Date(), LIST1, MAP1);
    private static Gson GSON = new GsonBuilder().create();
    private static ObjectMapper OBJECTMAPPER = new ObjectMapper();

    static {
        OBJECTMAPPER.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }

    public static void main(String[] args) throws Exception {
        for (int i=0; i<10; i++) {
            test(SerializationEval::javaSerialization, "java");
        }
        for (int i=0; i<10; i++) {
            test(sut -> gsonSerialization(sut, GSON), "gson");
        }
        for (int i=0; i<10; i++) {
            test(sut -> gsonSerialization(sut, new GsonBuilder().create()), "gson nocache");
        }
        for (int i=0; i<10; i++) {
            test(sut -> jacksonSerialization(sut), "jackson");
        }
    }

    private static void test(Consumer<Sut> consumer, String label) throws Exception {
        long pre = System.nanoTime();
        for (int i=0; i<1000; i++) {
            Sut sut = new Sut("test", i, 1L, new Date(), LIST1, MAP1);
            consumer.accept(sut);
        }
        long post = System.nanoTime();
        System.out.println(label + ": " + (post-pre)/1000000 + " ms");
    }

    private static String javaSerialization(Object object) throws RuntimeException {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(baos);
            out.writeObject(object);
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

    private static String gsonSerialization(Sut sut, Gson gson) {
        String json = gson.toJson(sut);
        Sut o = gson.fromJson(json, Sut.class);
        return o.toString();
    }

    private static String jacksonSerialization(Sut sut) {
        try {
            String json =  OBJECTMAPPER.writeValueAsString(sut);
//            System.out.println(json);
            Sut o = OBJECTMAPPER.readValue(json, Sut.class);
//            System.out.println(o);
            return o.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private static class Sut implements Serializable {
        String strParam;
        Integer intParam;
        Long longParam;
        Date dateParam;
        List<String> listParam;
        Map<String, String> mapParam;

        public Sut() {
        }

        public Sut(final String strParam, final Integer intParam, final Long longParam, final Date dateParam, final List<String> listParam, final Map<String, String> mapParam) {
            this.strParam = strParam;
            this.intParam = intParam;
            this.longParam = longParam;
            this.dateParam = dateParam;
            this.listParam = listParam;
            this.mapParam = mapParam;
        }

        @Override
        public String toString() {
            return "Sut{" +
                "strParam='" + strParam + '\'' +
                ", intParam=" + intParam +
                ", longParam=" + longParam +
                ", dateParam=" + dateParam +
                ", listParam=" + listParam +
                ", mapParam=" + mapParam +
                '}';
        }
    }
}
