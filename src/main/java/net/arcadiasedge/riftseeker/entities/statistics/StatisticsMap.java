package net.arcadiasedge.riftseeker.entities.statistics;

import net.arcadiasedge.riftseeker.entities.GameEntity;
import net.arcadiasedge.riftseeker.statistics.BoostType;
import net.arcadiasedge.riftseeker.statistics.StatisticBoost;

import java.util.*;

/**
 * This is a representation of a particular statistics value, such as health, mana, strength, etc.
 *
 * It is used to rigidly define base values, and to keep track of all contributors to a statistic;
 * as well as any additional boosts that may be applied to the statistic, such as buffs, equipment, etc.
 */
public class StatisticsMap {
    /**
     * The name of this statistic, such as "health", "mana", "strength", etc.
     */
    public String name;

    /**
     * The entity that owns this statistic.
     */
    public GameEntity<?> owner;

    /**
     * Contributors to the base value of this statistic, such as equipment, buffs, etc.
     */
    public Map<Object, Float> contributors;

    /**
     * Boosts to this statistic, such as buffs, equipment, enchantments, etc.
     */
    public Map<Object, List<StatisticBoost>> boosts;

    /**
     * The current value of this statistic.
     */
    public float current;

    /**
     * Whether this statistic has a limit or not. Commonly used for health and mana.
     */
    public boolean limitless;

    public StatisticsMap(String name) {
        this(name, new HashMap<>(), false);
    }

    public StatisticsMap(String name, boolean noLimit) {
        this(name, new HashMap<>(), noLimit);
    }

    public StatisticsMap(String name, Map<Object, Float> contributors, boolean noLimit) {
        this.name = name;
        this.contributors = contributors;
        this.current = 0.0f;
        this.limitless = noLimit;

        this.boosts = new HashMap<>();
    }

    /**
     * Adds the given value to the current value of this statistic.
     * This change is applied immediately, and is not affected by boosts.
     * @param value The value to add to the current value.
     */
    public void add(float value) {
        current += value;

        if (current < 0.0f) current = 0.0f;
        if (current > getBaseTotal() && (limitless == false)) current = getBaseTotal();
    }

    /**
     * Subtracts the given value from the current value of this statistic.
     * This change is applied immediately, and is not affected by boosts.
     * @param value The value to subtract from the current value.
     */
    public void subtract(float value) {
        current -= value;

        if (current < 0.0f) current = 0.0f;
        if (current > getBaseTotal()) current = getBaseTotal();
    }

    /**
     * Fetches the value of the given contributor.
     * @param contributor An object instance that is a contributor to this statistic.
     * @return The value of the given contributor.
     */
    public float getContributorValue(Object contributor) {
        return contributors.get(contributor);
    }

    /**
     * Whether this statistic has the given contributor.
     * @param contributor An object instance that is a contributor to this statistic.
     * @return True if this statistic has the given contributor, otherwise false.
     */
    public boolean hasContributor(Object contributor) {
        return contributors.containsKey(contributor);
    }

    /**
     * Sets a contributor to this statistic, and sets its value.
     * This method also updates the current value of this statistic.
     *
     * If the contributor already exists, the old value is subtracted from the current value
     * before the new value is added, and is removed from the map.
     * @param contributor The object instance that is a contributor to this statistic.
     * @param value The value of the given contributor.
     */
    public void setContributorValue(Object contributor, float value) {
        if (contributors.containsKey(contributor)) {
            // Subtract the old value from the current
            current -= contributors.get(contributor);
        }

        // Set the new value
        contributors.put(contributor, value);

        // add to the current the new value
        current += value;
    }

    /**
     * Removes the given contributor from this statistic.
     * This method will subtract the value of the given contributor from the current value.
     * @param contributor The object instance that is a contributor to this statistic.
     */
    public void removeContributor(Object contributor) {
        // remove the contributor from the map
        float value = contributors.remove(contributor);

        // subtract the value from the current
        current -= value;
    }

    /**
     * Gets the name of this statistic.
     * @return The name of this statistic.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets a list of boosts to this statistic. Coming from the given source.
     * @param source The object instance that is the source of the boosts.
     * @param boosts A list of boosts to set to this statistic.
     */
    public void setBoosts(Object source, List<StatisticBoost> boosts) {
        this.boosts.put(source, boosts);
    }

    /**
     * Adds a boost to this statistic given the source of the boost.
     * If the source already exists, the boost is added to the list of boosts,
     * if not, a new list is created with the boost added to it.
     * @param source The object instance that is the source of the boost.
     * @param boost The boost to add to this statistic.
     */
    public void addBoost(Object source, StatisticBoost boost) {
        if (boosts.containsKey(source)) {
            boosts.get(source).add(boost);
        } else {
            boosts.put(source, new ArrayList<>(List.of(boost)));
        }
    }

    /**
     * Fetches whether this statistic has boost(s) from the given source.
     * @param source The object instance to check for boosts.
     * @return True if this statistic has boost(s) from the given source, otherwise false.
     */
    public boolean hasBoost(Object source) {
        return boosts.containsKey(source);
    }

    /**
     * Removes the given boost from this statistic, given the source of the boost.
     * @param source The object instance that is the source of the boost.
     * @param boost The boost to remove from this statistic.
     */
    public void removeBoost(Object source, StatisticBoost boost) {
        if (boosts.containsKey(source)) {
            boosts.get(source).remove(boost);
        }
    }

    /**
     * Removes all boosts from this statistic, given the source of the boosts.
     * @param source The object instance that is the source of the boosts.
     */
    public void removeBoosts(Object source) {
        if (boosts.containsKey(source)) {
            boosts.remove(source);
        }
    }

    /**
     * Gets the current value of this statistic.
     * @return The current value of this statistic.
     */
    public float getCurrent() {
        return current;
    }

    /**
     * The base total of this statistic, calculated from all contributors.
     * This value is not affected by boosts.
     * @return The base total of this statistic.
     */
    public float getBaseTotal() {
        float total = 0.0f;

        for (float value : contributors.values()) {
            total += value;
        }

        return total;
    }

    /**
     * The final total of this statistic, calculated from all contributors and boosts.
     * @return The final total of this statistic.
     */
    public float getFinalTotal() {
        float finalValue = this.getBaseTotal();

        for (var boost : boosts.values()) {
            for (var b : boost) {
                finalValue += b.getValue(this.owner, this);
            }
        }

        return finalValue;
    }
}
