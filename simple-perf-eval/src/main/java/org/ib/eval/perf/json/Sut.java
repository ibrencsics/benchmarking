package org.ib.eval.perf.json;

import java.io.Serializable;
import java.util.*;

class Sut implements Serializable {

    public static final List<String> LIST1 = new ArrayList<>();
    public static final Map<String, String> MAP1 = new HashMap<>();
    static {
        LIST1.add("apple");
        LIST1.add("pear");
        LIST1.add("orange");

        MAP1.put("one", "apple");
        MAP1.put("two", "pear");
        MAP1.put("three", "orange");
    }

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
