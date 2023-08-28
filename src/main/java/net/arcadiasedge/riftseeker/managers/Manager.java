package net.arcadiasedge.riftseeker.managers;

import java.util.HashMap;
import java.util.Map;

public class Manager<T> {
    public Map<String, T> entries = new HashMap<>();

    public T get(String name) {
        return entries.get(name);
    }

    public void register(String name, T entry) {
        entries.put(name, entry);
    }
}

