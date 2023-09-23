package net.arcadiasedge.riftseeker.entities.effects;

import net.arcadiasedge.riftseeker.entities.GameEntity;
import net.arcadiasedge.riftseeker.statistics.StatisticBoost;
import org.bukkit.entity.LivingEntity;

import java.util.List;

/**
 * A status effect that can be applied to a game entity, commonly a player.
 */
public abstract class StatusEffect {
    /**
     * The length of the effect in ticks.
     */
    public int length;

    /**
     * The level of the effect.
     */
    public int level;

    /**
     * The entity that this effect is applied to.
     */
    public GameEntity entity;

    public StatusEffect(int length, int level) {
        this.length = length;
    }

    /**
     * Gets the length of the effect in ticks.
     * @return The length of the effect in ticks.
     */
    public int getLength() {
        return length;
    }

    /**
     * Gets the level of the effect.
     * @return The level of the effect.
     */
    public int getLevel() {
        return level;
    }

    /**
     * Sets the length of the effect in ticks.
     * @param level
     */
    public void setLevel(int level) {
        this.level = level;

        // TODO: Reapply the effect.
    }

    public void setEntity(GameEntity entity) {
        this.entity = entity;
    }

    public GameEntity getEntity() {
        return entity;
    }

    public LivingEntity getLivingEntity() {
        return (LivingEntity) entity.getEntity();
    }

    /**
     * Applies the effect to the entity.
     */
    public abstract List<StatisticBoost> apply();
    public abstract void unapply();
}
