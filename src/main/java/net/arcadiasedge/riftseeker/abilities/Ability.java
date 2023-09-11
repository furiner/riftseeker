package net.arcadiasedge.riftseeker.abilities;

import net.arcadiasedge.riftseeker.api.partials.ApiAbility;
import net.arcadiasedge.riftseeker.entities.GameEntity;
import net.arcadiasedge.riftseeker.entities.players.GamePlayer;
import net.arcadiasedge.riftseeker.entities.statistics.StatisticsMap;
import net.arcadiasedge.riftseeker.items.DamageType;
import net.arcadiasedge.riftseeker.items.Item;
import org.bukkit.event.inventory.ClickType;

import java.util.List;
import java.util.concurrent.Future;

/**
 * An ability that can be used by a player, associated with an item.
 */
public abstract class Ability {
    private Item item;

    /**
     * The cached ability data from the database.
     */
    public ApiAbility baseAbility;

    private ApplyType type;

    private int cooldown;

    public Ability() {
        this(null, null);
    }

    public Ability(Item item, ApiAbility ability) {
        this.item = item;
        this.baseAbility = ability;
        this.type = ApplyType.NORMAL;
        this.cooldown = 0;
    }

    /**
     * Gets the ID of this ability from the cached API data.
     * @return The ID of this ability.
     */
    public String getId() {
        return baseAbility.id;
    }

    /**
     * Gets the name of this ability.
     * @return The name of this ability.
     */
    public String getName() {
        return baseAbility.name;
    }

    /**
     * Gets the item associated with this ability.
     * @return The item associated with this ability.
     */
    public Item getItem() {
        return item;
    }

    /**
     * Sets the item that should be associated with this ability.
     * @param item The item that will be associated with this ability.
     */
    public void setItem(Item item) {
        this.item = item;
    }

    /**
     * Gets the type of this ability, more-so where and when
     * it should be applied.
     * @return
     */
    public ApplyType getType() {
        return this.type;
    }

    /**
     * Gets the trigger type of this ability.
     * @return The trigger type of this ability.
     */
    public ClickType getTrigger() {
        return ClickType.valueOf(baseAbility.button.toUpperCase());
    }

    /**
     * Sets the cached API data for this ability.
     * @param ability The API data that should be cached.
     */
    public void setApiAbility(ApiAbility ability) {
        this.baseAbility = ability;
    }

    public int getCooldown() {
        return cooldown;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    /**
     * Calculates the base damage that this ability can do, based off of the player's stats.
     * This is primarily for display and lower level calculations; and isn't
     * particularly targetting any specific entity.
     */
    public float calculateBaseDamage(GamePlayer player) {
        // This is meant to be a placeholder for a more complex calculation.
        var snapshot = player.getStatistics().consumeSnapshot(this);

        if (item.baseItem.properties.getDamageType() == null) {
            return 0;
        }

        float scaling;

        if (item.baseItem.properties.getDamageType() == DamageType.PHYSICAL) {
            scaling = snapshot.get("strength");
        } else if (item.baseItem.properties.getDamageType() == DamageType.MAGICAL) {
            scaling = snapshot.get("intelligence");
        } else {
            scaling = snapshot.get("dexterity");
        }

        return baseAbility.damage * (2.0f + scaling / 100.0f);
    }

    /**
     * Calculates the damage that this ability will do to any given entity.
     * @param player The player that is using this ability.
     * @return The amount of damage that this ability will do.
     */
    public abstract float calculateDamage(GamePlayer player, GameEntity<?> target);

    /**
     * This is an asynchronous method that will be called when the ability is used.
     *
     * It is meant to be used for any effects that are not directly related to
     * the damage calculation; and should be used to gather any targets that
     * will be affected by the ability.
     *
     * The reason it's asynchronous is because it may take some time to gather
     * the targets, and we don't want to block the main thread.
     * @param player
     * @return
     */
    public abstract List<GameEntity<?>> execute(GamePlayer player);

    /**
     * Called upon when the ability hits an entity.
     *
     * This is meant to be used for calculating the damage that the ability
     * will do to the entity, and for any other effects that are directly
     * related to the damage calculation.
     *
     * @param player The player that is using this ability.
     * @param target The entity that is being hit by this ability.
     * @param index The index of the target in the list of targets. If this ability is an AOE, this should be used to calculate the damage.
     * @return The amount of damage that this ability will do.
     */
    public abstract float onEntityHit(GamePlayer player, GameEntity<?> target, int index);
}
