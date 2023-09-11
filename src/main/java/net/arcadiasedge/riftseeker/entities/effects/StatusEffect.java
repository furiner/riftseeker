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

    public int level;

    public GameEntity entity;

    public StatusEffect(int length, int level) {
        this.length = length;
    }

    public int getLength() {
        return length;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
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
