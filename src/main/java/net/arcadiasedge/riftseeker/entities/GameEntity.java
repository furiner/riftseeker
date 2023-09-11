package net.arcadiasedge.riftseeker.entities;

import de.tr7zw.nbtapi.NBT;
import net.arcadiasedge.riftseeker.RiftseekerPlugin;
import net.arcadiasedge.riftseeker.entities.effects.StatusEffect;
import net.arcadiasedge.riftseeker.entities.players.GamePlayer;
import net.arcadiasedge.riftseeker.entities.statistics.GameStatistics;
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
import org.bukkit.scheduler.BukkitRunnable;

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
    protected final GameStatistics<GameEntity<E>> statistics = new GameStatistics<>(this);

    protected List<StatusEffect> effects;

    public GameEntity(E entity) {
        this.entity = entity;
        this.name = entity != null ? entity.getName() : "Unknown";
        this.effects = new ArrayList<>();

        GameWorld.getInstance().getEntities().add(this);
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
    public GameStatistics<GameEntity<E>> getStatistics() {
        return statistics;
    }

    /**
     * This is a method that is called every tick, and is meant to be used for AI.
     *
     * Ultimately, this method should be used to update the entity's statistics, and
     * to make the entity do things. This method should be called by the game loop.
     * Any method that changes a statistic should call {@link GameStatistics#apply()}.
     *
     * Additionally, any entity that is created should ideally have the NoAI tag set to
     * true, and the AI should be handled by this method. The only exception is if the
     * entity is already handled by the server, or a plugin such as Citizens.
     *
     * @see GameStatistics
     * @see NPCEntity
     */
    public abstract void update();

    /**
     * This is a function that is called when the entity is first created.
     *
     * This function should be used to set up the entity, such as setting up
     * the entity's statistics, and setting up the entity's AI.
     */
    public abstract void setup();

    /**
     * Gets the entity that this object represents.
     * @return The entity that this object represents.
     */
    public E getEntity() {
        return entity;
    }

    /**
     * Damages the entity by the specified amount.
     * This method handles the hit counter and the death animation, as well as de-spawning the entity.
     * @param amount The amount to damage the entity by. This should be calculated by the opponent's statistics. Commonly from {@link GamePlayer#calculateWeaponDamage(GameEntity)}
     * @param isCritical Whether this damage is a critical hit.
     */
    public void damage(GameEntity<?> damager, float amount, boolean isCritical) {
        return;
    }

    public void giveEffect(StatusEffect effect) {
        effects.add(effect);
        effect.setEntity(this);

        reapplyEffect(effect);

        new BukkitRunnable() {
            @Override
            public void run() {
                effects.remove(effect);
                effect.unapply();

                // Remove each boost this effect gave
                for (var statistic : getStatistics().getValues()) {
                    if (statistic.hasBoost(effect)) {
                        statistic.removeBoosts(effect);
                    }
                }
            }
        }.runTaskLater(RiftseekerPlugin.getInstance(), effect.getLength());
    }

    public boolean hasEffect(Class<? extends StatusEffect> effect) {
        for (StatusEffect e : effects) {
            if (e.getClass().equals(effect)) {
                return true;
            }
        }

        return false;
    }

    public void modifyEffect(StatusEffect effect) {
        for (StatusEffect e : effects) {
            if (e.getClass().equals(effect.getClass())) {
                e.setLevel(effect.getLevel());
                reapplyEffect(e);
                return;
            }
        }
    }

    private void reapplyEffect(StatusEffect effect) {
        // Remove each boost this effect gave
        for (var statistic : this.getStatistics().getValues()) {
            if (statistic.hasBoost(effect)) {
                statistic.removeBoosts(effect);
            }
        }

        var boosts = effect.apply();

        // Apply each boost this effect gives
        for (var boost : boosts) {
            this.getStatistics().getStatistic(boost.statistic).addBoost(effect, boost);
        }
    }
}
