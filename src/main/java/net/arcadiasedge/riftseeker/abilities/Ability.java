package net.arcadiasedge.riftseeker.abilities;

import net.arcadiasedge.riftseeker.api.partials.ApiAbility;
import net.arcadiasedge.riftseeker.entities.players.GamePlayer;
import net.arcadiasedge.riftseeker.items.Item;

/**
 * An ability that can be used by a player, associated with an item.
 */
public class Ability {
    private final Item item;

    /**
     * The cached ability data from the database.
     */
    public final ApiAbility baseAbility;

    public Ability() {
        this(null, null);
    }

    public Ability(Item item, ApiAbility ability) {
        this.item = item;
        this.baseAbility = ability;
    }

    /**
     * Calculates the damage that this ability will do to any given entity.
     * @param player The player that is using this ability.
     * @return The amount of damage that this ability will do.
     */
    public int calculateDamage(GamePlayer player) {
        // This is meant to be a placeholder for a more complex calculation.
        return baseAbility.damage;
    }
}
