package net.arcadiasedge.riftseeker.entities.statistics;

import java.util.List;
import java.util.Map;

public class StatisticsSnapshot {
    private final Map<String, Float> values;

    private final List<Object> contributors;

    public StatisticsSnapshot(Map<String, Float> values, List<Object> contributors) {
        this.values = values;
        this.contributors = contributors;
    }

    public float get(String statistic) {
        return values.get(statistic);
    }

    public void set(String statistic, float value) {
        values.put(statistic, value);
    }

    public List<Object> getContributors() {
        return contributors;
    }

}
