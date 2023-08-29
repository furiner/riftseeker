package net.arcadiasedge.riftseeker.entities;

import net.arcadiasedge.riftseeker.entities.statistics.EntityStatistics;
import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a representation of a game entity, catered towards having MMO-like elements in the game.
 * It is made to be extended, and it's AI is handled individually by each entity.
 */
public abstract class GameEntity<E extends Entity> {
    public static final List<GameEntity> entities = new ArrayList<>();

    protected final String name;
    protected E entity;
    protected final EntityStatistics statistics = new EntityStatistics(this);

    public GameEntity(E entity) {
        this.entity = entity;
        this.name = entity.getName();

        GameEntity.entities.add(this);
    }

    /**
     * This is a method that is called every tick, and is meant to be used for AI.
     *
     * Ultimately, this method should be used to update the entity's statistics, and
     * to make the entity do things. This method should be called by the game loop.
     * Any method that changes a statistic should call {@link EntityStatistics#apply()}.
     *
     * Additionally, any entity that is created should ideally have the NoAI tag set to
     * true, and the AI should be handled by this method. The only exception is if the
     * entity is already handled by the server, or a plugin such as Citizens.
     *
     * @see EntityStatistics
     * @see GameNPCEntity
     */
    public abstract void update();

    /**
     * This is a function that is called when the entity is first created.
     *
     * This function should be used to set up the entity, such as setting up
     * the entity's statistics, and setting up the entity's AI.
     */
    public abstract void setup();

    public E getEntity() {
        return entity;
    }
}
