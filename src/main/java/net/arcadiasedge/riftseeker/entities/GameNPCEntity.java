package net.arcadiasedge.riftseeker.entities;

import net.arcadiasedge.riftseeker.entities.traits.RiftseekerDefaultTrait;
import net.arcadiasedge.riftseeker.world.GameWorld;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.MemoryNPCDataStore;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

/**
 * A game entity backed by a Citizens NPC.
 *
 * Most of the time, you'll be using this as a mere baseline for more complex NPCs, as
 * Citizens fundamentally relies on its own classes to handle the actual NPC logic.
 *
 * Primarily, it does so by creating a {@see Trait}. Traits are the primary way to
 * extend the functionality of an NPC, and are the recommended way to do so.
 * @param <E>
 */
public abstract class GameNPCEntity<E extends Entity> extends GameEntity<E> {
    public static final NPCRegistry registry = CitizensAPI.createAnonymousNPCRegistry(new MemoryNPCDataStore());

    /**
     * The Citizens NPC backing this entity.
     */
    public NPC npc;

    /**
     * The type of aggression this entity has.
     */
    public AggressionType type;

    /**
     * The level of this entity.
     */
    public int level;

    public GameNPCEntity(EntityType type, String name) {
        super(null);

        this.npc = GameNPCEntity.getRegistry().createNPC(type, name);
        this.level = 1;
        this.type = AggressionType.Passive;

        this.setName(name);
        this.npc.getOrAddTrait(RiftseekerDefaultTrait.class);

        // Set basic health value so it isn't just 0
        this.getStatistics().getStatistic("health").setContributorValue(this, 10000);
    }

    /**
     * Sets the aggression type of this entity.
     * @param type The type of aggression.
     */
    public void setType(AggressionType type) {
        this.type = type;
    }

    /**
     * Gets the aggression type of this entity.
     * @return The type of aggression.
     */
    public AggressionType getType() {
        return type;
    }

    /**
     * This method is called when the entity is spawned. It should be used to set up
     * the entity's traits and other Citizens-related things. It should also be used
     * to set up the entity's statistics, if it will have any.
     *
     * This method is called automatically when the entity is truly spawned, so you
     * won't need to call it yourself. However, you should still override it.
     */
    public abstract void onSpawn();

    /**
     * Spawns the entity at the given location.
     * @param location The location to spawn the entity at.
     */
    public void spawn(Location location) {
        this.setup();
        this.assignDisplayName();

        GameWorld.getInstance().getEntities().add(this);

        this.npc.spawn(location);
        this.entity = (E) this.npc.getEntity();
    }

    /**
     * De-spawns the entity.
     */
    public void despawn() {
        if (this.npc.isSpawned()) {
            this.npc.despawn();
        }

        GameWorld.getInstance().getEntities().remove(this);
    }

    /**
     * Gets the Citizens NPC backing this entity.
     * @return The Citizens NPC.
     */
    public NPC getNPC() {
        return npc;
    }

    /**
     * Assigns the display name of the entity based on its type and statistics.
     * This method is called automatically when the entity is spawned, so you
     * won't need to call it yourself.
     *
     * This method is also automatically called when the entity's statistics change, so if you
     * have a statistic that affects the display name, you might want to override
     * this method.
     */
    public void assignDisplayName() {
        var nameColor = switch (this.getType()) {
            case Passive -> "<green>";
            case Hostile -> "<red>";
            case Neutral -> "<yellow>";
        };

        var healthStatistic = this.getStatistics().getStatistic("health");

        this.npc.setName("<gray>Lv" + level + ". " + nameColor + this.getName() + " <red>❤ " + String.format(Locale.US, "%,.0f", healthStatistic.getCurrent()) + "<gray>/<red>" + String.format(Locale.US, "%,.0f", healthStatistic.getFinalTotal()));
    }

    /**
     * Gets the Citizens NPC registry.
     * @return The registry.
     */
    public static NPCRegistry getRegistry() {
        return registry;
    }
}
