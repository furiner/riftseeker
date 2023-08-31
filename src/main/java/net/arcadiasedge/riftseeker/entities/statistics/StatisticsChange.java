package net.arcadiasedge.riftseeker.entities.statistics;

enum ChangeType {
    Additive,
    Subtractive,
    Multiplicative
}

public class StatisticsChange {
    public String statistic;
    public ChangeType type;
    public int value;

    public StatisticsChange(String statistic, ChangeType type, float value) {
        this.statistic = statistic;
        this.type = type;
        this.value = value;
    }
}
