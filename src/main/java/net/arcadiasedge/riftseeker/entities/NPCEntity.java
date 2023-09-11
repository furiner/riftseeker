package net.arcadiasedge.riftseeker.entities;

import de.tr7zw.nbtapi.NBT;
import net.arcadiasedge.riftseeker.RiftseekerPlugin;
import net.arcadiasedge.riftseeker.entities.players.GamePlayer;
import net.arcadiasedge.riftseeker.entities.traits.RiftseekerDefaultTrait;
import net.arcadiasedge.riftseeker.manufacturers.ItemManufacturer;
import net.arcadiasedge.riftseeker.utils.RNG;
import net.arcadiasedge.riftseeker.world.GameWorld;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.MemoryNPCDataStore;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.*;

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
public abstract class NPCEntity<E extends Entity> extends GameEntity<E> {
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

    private Map<GameEntity<?>, Float> damagers;

    public NPCEntity(EntityType type, String name) {
        super(null);

        this.npc = NPCEntity.getRegistry().createNPC(type, name);
        this.level = 1;
        this.type = AggressionType.Passive;
        this.damagers = new HashMap<>();

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
     * Gets the Citizens NPC backing this entity.
     * @return The Citizens NPC.
     */
    public NPC getNPC() {
        return npc;
    }

    public Map<String, Float> getLootTable() {
        return new LinkedHashMap<>();
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

    public void onHit(GameEntity<?> damager, float amount, boolean isCritical) {
        if (damagers.containsKey(damager)) {
            damagers.put(damager, damagers.get(damager) + amount);
        } else {
            damagers.put(damager, amount);
        }
    }

    public void onDeath(GameEntity<?> killer) {
        getEntity().playEffect(EntityEffect.DEATH);

        Bukkit.getScheduler().runTaskLater(RiftseekerPlugin.getInstance(), () -> {
            var location = getEntity().getLocation();

            // Remove the entity
            getEntity().remove();
            location.getWorld().spawnParticle(Particle.CLOUD, location, 0);

        }, 10);

        for (var set : damagers.entrySet()) {
            var damager = (GamePlayer) set.getKey();
            var damage = set.getValue();

            if (!GameWorld.getInstance().getEntities().contains(damager)) {
                continue;
            }

            var percentage = damage / getStatistics().getStatistic("health").getBaseTotal();

            if (percentage > 0.10) {
                // reward loot
                var lootTable = getLootTable();
                var reward = RNG.fromWeightedChance(lootTable);
                var item = ItemManufacturer.create(reward, damager);

                // TODO: spawn item on the ground instead?
                damager.getInventory().add(item);
            }
        }
    }

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

    @Override
    public void damage(GameEntity<?> damager, float amount, boolean isCritical) {
        if (amount <= 0) {
            return;
        }

        var healthStatistic = this.getStatistics().getStatistic("health");
        this.getStatistics().subtract("health", amount);

        // Create an invisible ArmorStand
        var as = this.entity.getWorld().spawn(new Location(entity.getWorld(), 0, 255, 0), ArmorStand.class);

        NBT.modify(as, nbt -> {
            nbt.setBoolean("Invisible", true);
            nbt.setBoolean("Invulnerable", true);
            nbt.setBoolean("NoGravity", true);
            nbt.setBoolean("Marker", true);
            nbt.setBoolean("CustomNameVisible", true);

            Component component;

            if (isCritical == true) {
                // This is a critical hit.
                component = MiniMessage.miniMessage().deserialize("<white>✧ <color:#73c3f5>" + String.format(Locale.US, "%,.0f", amount) + " <white>✧");
            } else {
                component = MiniMessage.miniMessage().deserialize("<gray>" + String.format(Locale.US, "%,.0f", amount));
            }

            nbt.setString("CustomName", GsonComponentSerializer.gson().serialize(component));
        });

        // Move the armor stand to a random location around the entity
        as.teleport(npc.getEntity().getLocation().add(Math.random() * 2 - 1, 0.5 + Math.random() * 2 - 1, Math.random() * 2 - 1));

        // Remove it after a few seconds
        RiftseekerPlugin.getInstance().getServer().getScheduler().runTaskLater(RiftseekerPlugin.getInstance(), as::remove, 20);

        if (healthStatistic.getCurrent() - amount <= 0) {
            this.onDeath(damager);
        } else {
            this.onHit(damager, amount, isCritical);
        }
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
