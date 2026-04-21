package br.com.omnirent.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public final class Sequence {

    private static final Map<String, AtomicInteger> SEQUENCES = new ConcurrentHashMap<>();

    private Sequence() {}

    public static int next(String key) {
        return SEQUENCES
            .computeIfAbsent(key, k -> new AtomicInteger())
            .incrementAndGet();
    }

    public static String nextString(String prefix) {
        return prefix + "-" + next(prefix);
    }
}