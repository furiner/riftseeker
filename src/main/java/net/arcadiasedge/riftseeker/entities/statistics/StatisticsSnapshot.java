package net.arcadiasedge.riftseeker.entities.statistics;

import java.util.Map;

public class StatisticsSnapshot {
    private final Map<String, Float> values;

    public StatisticsSnapshot(Map<String, Float> values) {
        this.values = values;
    }

    public float get(String statistic) {
        return values.get(statistic);
    }

    public void set(String statistic, float value) {
        values.put(statistic, value);
    }

    public void getAll(Map<String, Float> map) {
        map.putAll(values);
    }
}
