package com.freddie.demo.javabase;

import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class TestDemo {
    private final AtomicLong counter = new AtomicLong();

    public void increase() {
        counter.incrementAndGet();
    }

    public void test001() {
        Integer integer = new Integer(1);
    }

    public void test002() {
        Hashtable<String, String> stringStringHashtable = new Hashtable<>();
        Map<String, String> stringStringMap = new HashMap<>();
        Map<String, String> stringStringMapSyn = Collections.synchronizedMap(stringStringMap);
        ConcurrentHashMap<String, String> stringStringConcurrentHashMap = new ConcurrentHashMap<>();
    }
}
