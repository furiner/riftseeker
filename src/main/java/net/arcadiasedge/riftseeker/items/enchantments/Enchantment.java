package net.arcadiasedge.riftseeker.items.enchantments;

import net.arcadiasedge.riftseeker.entities.GameEntity;
import net.arcadiasedge.riftseeker.items.Item;
import net.arcadiasedge.riftseeker.items.ItemType;
import net.arcadiasedge.riftseeker.statistics.StatisticBoost;
import net.arcadiasedge.riftseeker.utils.RomanFormatter;
import net.kyori.adventure.text.Component;

import java.util.List;

/**
 * A class that represents an enchantment that can be applied to an item.
 * This class is abstract and must be extended to be used.
 */
public abstract class Enchantment {
    private final String id;
    private final String name;
    private int level;
    private int maxLevel;
    private ItemType itemType;

    public Enchantment() {
        this.name = this.id = "UNKNOWN";
        this.level = 0;
    }

    public Enchantment(String id, String name) {
        this.id = id;
        this.name = name;
        this.level = 1;
        this.maxLevel = 10; // Standard
    }

    /**
     * Gets the display name of the enchantment, including the level which is formatted as a roman numeral.
     * @return A string that should be shown to the player.
     */
    public String getDisplayName() {
        // turn level into roman numerals
        var romanNumeral = RomanFormatter.toRoman(level);
        return name + " " + romanNumeral;
    }

    /**
     * Gets the level of the enchantment.
     * @return The level of the enchantment.
     */
    public int getLevel() {
        return level;
    }

    /**
     * Sets the level of the enchantment.
     * @param level The level to set the enchantment to.
     */
    public void setLevel(int level) {
        this.level = level;
    }

    /**
     * Gets the maximum level of the enchantment.
     * @return The maximum level of the enchantment.
     */
    public int getMaxLevel() {
        return maxLevel;
    }

    /**
     * Sets the maximum level of the enchantment.
     * @param maxLevel The maximum level to set the enchantment to.
     */
    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    /**
     * Gets the type of item that this enchantment is applied to.
     * @return The type of item that this enchantment is applied to.
     */
    public ItemType getItemType() {
        return itemType;
    }

    /**
     * Sets the type of item that this enchantment is applied to.
     * @param type The type of item that this enchantment is applied to.
     */
    public void setItemType(ItemType type) {
        this.itemType = type;
    }

    /**
     * Gets the ID of the enchantment.
     * @return The ID of the enchantment.
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the name of the enchantment.
     * @return The name of the enchantment.
     */
    public String getName() {
        return name;
    }

    /**
     * Called when the enchantment is applied to an item.
     * This is where you should add any statistic boosts that the enchantment provides when applied.
     * @param item The item that the enchantment is being applied to.
     * @return A list of statistic boosts that the enchantment provides.
     */
    public abstract List<StatisticBoost> onApply(Item item);

    /**
     * Called when player attacks an entity with an item that has this enchantment.
     * This is where you should add any effects that the enchantment provides when attacking an entity.
     * @param entity The entity that was attacked.
     * @param damage The amount of damage that is going to be dealt to the entity.
     */
    public abstract void onHit(GameEntity<?> entity, float damage);

    public abstract boolean canEnchant(Item item);
}
