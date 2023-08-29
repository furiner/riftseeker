package net.arcadiasedge.riftseeker.entities.statistics;

enum ChangeType {
    Additive,
    Subtractive,
    Multiplicative
}

public class StatisticsChange {
    public String name;
    public ChangeType type;
    public int value;

    public StatisticsChange(String name, ChangeType type, int value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }
}
