package net.arcadiasedge.riftseeker.entities;

import de.tr7zw.nbtapi.NBT;
import net.arcadiasedge.riftseeker.RiftseekerPlugin;
import net.arcadiasedge.riftseeker.entities.players.GamePlayer;
import net.arcadiasedge.riftseeker.entities.statistics.EntityStatistics;
import net.arcadiasedge.riftseeker.world.GameWorld;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * This is a representation of a game entity, catered towards having MMO-like elements in the game.
 * It is made to be extended, and it's AI is handled individually by each entity.
 */
public abstract class GameEntity<E extends Entity> {
    protected String name;
    protected E entity;
    protected final EntityStatistics statistics = new EntityStatistics(this);

    public GameEntity(E entity) {
        this.entity = entity;
        this.name = entity != null ? entity.getName() : "Unknown";

        GameEntity.entities.add(this);
    }

    /**
     * Gets the name of the entity.
     * @return The name of the entity.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the entity.
     * @param name The name to set for this entity.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the statistics of the entity.
     * @return The statistics of the entity.
     */
    public EntityStatistics getStatistics() {
        return statistics;
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

    /**
     * Damages the entity by the specified amount.
     * This method handles the hit counter and the death animation, as well as de-spawning the entity.
     * @param amount The amount to damage the entity by. This should be calculated by the opponent's statistics. Commonly from {@link GamePlayer#calculateWeaponDamage(GameEntity)}
     * @param isCritical Whether this damage is a critical hit.
     */
    public void damage(float amount, boolean isCritical) {
        var healthStatistic = this.getStatistics().getStatistic("health");

        this.getStatistics().subtract("health", amount);

        if (this instanceof GamePlayer) {
            ((GamePlayer) this).getEntity().setHealth(0);
        } else if (this instanceof GameNPCEntity) {
            var npc = ((GameNPCEntity) this);

            // Create an invisible ArmorStand
            var as = npc.getEntity().getWorld().spawn(new Location(entity.getWorld(), 0, 255, 0), ArmorStand.class);

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
        }

        if (healthStatistic.getCurrent() <= 0) {
            if (this instanceof GameNPCEntity<?>) {
                // Play dying animation
                var npc = ((GameNPCEntity<?>) this);
                npc.getEntity().playEffect(EntityEffect.DEATH);

                Bukkit.getScheduler().runTaskLater(RiftseekerPlugin.getInstance(), () -> {
                    var location = npc.getEntity().getLocation();

                    // Remove the entity
                    npc.getEntity().remove();
                    location.getWorld().spawnParticle(Particle.CLOUD, location, 0);
                }, 10);
            }
        }
    }
}
