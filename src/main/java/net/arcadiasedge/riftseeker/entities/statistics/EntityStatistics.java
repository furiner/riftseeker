package net.arcadiasedge.riftseeker.entities.statistics;

import net.arcadiasedge.riftseeker.entities.GameEntity;

import java.util.Queue;

/**
 * A representation of a game entity's statistics, such as health, mana, etc.
 */
public class EntityStatistics {
    public GameEntity entity;

    /// BASE STATISTICS
    public int baseHealth;
    public int baseMana;
    public int baseDefense;

    // TRUE STATISTICS
    public int health;

    public Queue<StatisticsChange> changes;


    public EntityStatistics(GameEntity entity) {
        this.entity = entity;
    }

    /**
     * This is a method that actively applies current statistics to the player, such as health, mana, etc.
     * This is meant to be called whenever any statistic is changed; and should be called automatically
     * by any method that changes a statistic.
     *
     * All changes are retroactively applied based off the base statistic values.
     */
    public void apply() {
        // Apply all the pending changes
        while (!changes.isEmpty()) {
            StatisticsChange change = changes.poll();

            if (change.type == ChangeType.Subtractive) {
                change.value = -(Math.abs(change.value));
            }

            switch (change.name) {
                case "health":
                    health += change.value;
                    break;
            }
        }
    }
}
