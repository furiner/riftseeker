package net.arcadiasedge.riftseeker.entities.effects;

import net.arcadiasedge.riftseeker.entities.GameEntity;

/**
 * A status effect that can be applied to a game entity, commonly a player.
 */
public abstract class StatusEffect {
    /**
     * The amount of armor pieces that the entity has applied.
     */
    public int length;

    public StatusEffect(int length) {
        this.length = length;
    }

    /**
     * Applies the effect to the entity.
     * @param entity The entity to apply the effect to.
     */
    public abstract void apply(GameEntity<?> entity);
}
