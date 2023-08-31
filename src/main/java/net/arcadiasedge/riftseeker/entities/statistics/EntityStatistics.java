package net.arcadiasedge.riftseeker.entities.statistics;

import net.arcadiasedge.riftseeker.entities.GameEntity;

import java.util.*;

/**
 * A representation of a game entity's statistics, such as health, mana, etc.
 */
public class EntityStatistics {
    /**
     * The associated entity that these statistics belong to.
     */
    public GameEntity<?> entity;

    public Queue<StatisticsChange> changes;

    /**
     * The statistics that this entity has.
     * Entities can have any number of statistics, and they can be modified by any number of sources.
     * More commonly, the statistics that exists are health, mana, strength, intelligence, etc.
     * For a full list of statistics, see {@link #EntityStatistics(GameEntity)}.
     */
    public Map<String, StatisticsMap> statistics;

    /**
     * A map of snapshots of the statistics.
     * This is used to keep track of the statistics at a given point in time, such as when a player
     * shoots a projectile, then switches weapons, and then the projectile hits the target.
     * <br><br>
     * This keeps track of the statistics at the time the projectile was shot, so that the damage
     * will be calculated based off the statistics at the time the projectile was shot, and not
     * when it hit the target.
     * <br><br>
     * There can only be one snapshot per source, as to ensure that the statistics are not
     * modified by multiple of the same source at the same time.
     */
    public Map<Object, StatisticsSnapshot> snapshots;

    public EntityStatistics(GameEntity entity) {
        this.entity = entity;
        this.changes = new LinkedList<>();
        this.statistics = new HashMap<>();
        this.snapshots = new HashMap<>();

        // Add base statistics
        statistics.put("health", new StatisticsMap("health"));
        statistics.put("damage", new StatisticsMap("damage", true));
        statistics.put("true_damage", new StatisticsMap("true_damage", true));
        statistics.put("strength", new StatisticsMap("strength", true));
        statistics.put("intelligence", new StatisticsMap("intelligence", true));
        statistics.put("dexterity", new StatisticsMap("dexterity", true));
        statistics.put("defense", new StatisticsMap("defense", true));
        statistics.put("crit_damage", new StatisticsMap("crit_damage", true));
        statistics.put("crit_chance", new StatisticsMap("crit_chance", true));

        for (var statistic : statistics.values()) {
            statistic.owner = entity;
        }
    }

    /**
     * Gets the statistic with the given name.
     * @param name The name of the statistic to get.
     * @return A {@link StatisticsMap} object representing the statistic.
     */
    public StatisticsMap getStatistic(String name) {
        return statistics.get(name);
    }

    /**
     * Gets a collection of all the statistics that this entity has.
     * @return A collection of all the statistics that this entity has.
     */
    public Collection<StatisticsMap> getValues() {
        return statistics.values();
    }

    /**
     * Returns the current value of the statistic with the given name.
     * @param name The name of the statistic to get the current value of.
     * @return The current value of the statistic with the given name.
     */
    public float getStatisticValue(String name) {
        if (!statistics.containsKey(name)) {
            return 0.0f;
        }

        return statistics.get(name).current;
    }


    /**
     * Sets the base value of the statistic with the given name.
     * This will set a contributor value with the entity as the contributor.
     * @param name The name of the statistic to set the base value of.
     * @param value The value to set the base value to.
     */
    public void setBaseStatistic(String name, float value) {
        StatisticsMap statistic = statistics.get(name);

        statistic.setContributorValue(entity, value);
    }

    /**
     * Adds the given value to the statistic with the given name.
     * Note that this will not immediately apply the change; it will be applied at the start of the next game tick.
     * @param statistic The name of the statistic to add the value to.
     * @param value The value to add to the statistic.
     */
    public void add(String statistic, float value) {
        changes.add(new StatisticsChange(statistic, ChangeType.Additive, value));
    }

    /**
     * Subtracts the given value from the statistic with the given name.
     * Note that this will not immediately apply the change; it will be applied at the start of the next game tick.
     * @param statistic The name of the statistic to subtract the value from.
     * @param value The value to subtract from the statistic.
     */
    public void subtract(String statistic, float value) {
        changes.add(new StatisticsChange(statistic, ChangeType.Subtractive, value));
    }

    /**
     * This is a method that actively applies current statistics to the player, such as health, mana, etc.
     * This is meant to be called whenever any statistic is changed; and should be called automatically
     * by any method that changes a statistic.
     *
     * All changes are retroactively applied based off the base statistic values.
     */
    public List<StatisticsChange> apply() {
        if (changes.isEmpty()) {
            return new ArrayList<>();
        }

        // Apply all the pending changes
        var appliedChanges = new ArrayList<StatisticsChange>();

        while (!changes.isEmpty()) {
            StatisticsChange change = changes.poll();

            // Get the statistic
            StatisticsMap statistic = statistics.get(change.statistic);

            // Apply the change
            if (change.type == ChangeType.Additive) {
                statistic.add(change.value);
            } else if (change.type == ChangeType.Subtractive) {
                statistic.subtract(change.value);
            } else if (change.type == ChangeType.Multiplicative) {
                statistic.add(statistic.getBaseTotal() * change.value);
            }

            appliedChanges.add(change);
        }

        return appliedChanges;
    }

    /**
     * Takes a snapshot of the current statistics at the time of calling this method.
     * This will overwrite any existing snapshot for the given source.
     * <br><br>
     * This is used to keep track of the statistics at a given point in time, and prevents
     * a player from modifying the statistics while they are being used (e.g. when a player
     * switches weapons, and the projectile hits the target, the damage will be calculated
     * as if the player was still using the weapon that shot the projectile).
     * @param source The source of the snapshot.
     */
    public void takeSnapshot(Object source) {
        if (snapshots.containsKey(source)) {
            snapshots.remove(source);
        }

        var stats = new HashMap<String, Float>();

        for (var statistic : statistics.values()) {
            if (statistic.getName() == "health") {
                stats.put(statistic.name, statistic.getCurrent());
            } else {
                stats.put(statistic.name, statistic.getFinalTotal());
            }
        }

        var newSnapshot = new StatisticsSnapshot(stats);
        snapshots.put(source, newSnapshot);
    }

    /**
     * Consumes the snapshot for the given source, and returns it.
     * This method will remove the snapshot from the statistics.
     * @param source The source of the snapshot to consume.
     * @return The snapshot for the given source.
     */
    public StatisticsSnapshot consumeSnapshot(Object source) {
        if (!snapshots.containsKey(source)) {
            // Make a snapshot of the current statistics
            takeSnapshot(source);
            return consumeSnapshot(source);
        }

        var snapshot = snapshots.get(source);
        snapshots.remove(source);

        return snapshot;
    }

    /**
     * Entirely removes the snapshot for the given source.
     * @param source The source of the snapshot to remove.
     */
    public void voidSnapshot(Object source) {
        if (snapshots.containsKey(source)) {
            snapshots.remove(source);
        }
    }
}
