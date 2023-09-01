package net.arcadiasedge.riftseeker.world;

import net.arcadiasedge.riftseeker.entities.GameEntity;
import net.arcadiasedge.riftseeker.entities.players.GamePlayer;
import net.arcadiasedge.riftseeker.world.locations.GameLocation;
import net.arcadiasedge.riftseeker.world.locations.TestLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A singleton representation of the game world, containing all locations, entities, and players.
 *
 * The in-game Minecraft world is represented by a {@link World} object, which is a singleton
 * object that is created by the server. This class is a singleton that gets created by this
 * plugin, and should always be used over a normal world in most circumstances.
 */
public class GameWorld {
    private World world;
    private static GameWorld instance = null;

    private final Map<String, GameLocation> locations;
    private final List<GameEntity<?>> entities;
    /**
     * A static map of all players in this instance, mapped to their respective GamePlayer object.
     */
    public final Map<Player, GamePlayer> players;

    public GameWorld() {
        this.locations = new HashMap<>();
        this.entities = new ArrayList<>();
        this.players = new HashMap<>();

        // Set the default world
        this.setWorld(Bukkit.getServer().getWorlds().get(0));
    }

    /**
     * Sets the world that this GameWorld represents.
     * @param world The world to represent.
     */
    public void setWorld(World world) {
        this.world = world;
    }

    /**
     * Gets the world that this GameWorld represents.
     * @return The world that this GameWorld represents.
     */
    public World getWorld() {
        return world;
    }

    /**
     * Adds a zone to this GameWorld.
     * @param location The zone to add.
     */
    public void addLocation(GameLocation location) {
        this.locations.put(location.getName(), location);
    }

    /**
     * Gets a zone by the specified location in the world.
     * @param location The location to check the zone for.
     * @return The zone that contains the specified location, or null if no zone contains the location.
     */
    public GameLocation getLocationFor(Location location) {
        for (var loc : this.locations.values()) {
            if (loc.contains(location)) {
                return loc;
            }
        }

        return null;
    }

    /**
     * Gets a list of entities in this GameWorld.
     * @return A list of entities in this GameWorld.
     */
    public List<GameEntity<?>> getEntities() {
        return entities;
    }

    /**
     * Gets a {@link GameEntity} by the specified {@link Entity}
     * @param entity The entity to get the GameEntity for.
     * @return The GameEntity that represents the specified entity, or null if no GameEntity exists for the specified entity.
     */
    public GameEntity<?> getEntity(Entity entity) {
        for (GameEntity<?> gameEntity : entities) {
            if (gameEntity.getEntity().equals(entity)) {
                return gameEntity;
            }
        }

        return null;
    }

    /**
     * Gets a {@link GamePlayer} by the specified {@link Player}
     * @param player The player to get the GamePlayer for.
     * @return The GamePlayer that represents the specified player, or null if no GamePlayer exists for the specified player.
     */
    public GamePlayer getPlayer(Player player) {
        return players.get(player);
    }

    /**
     * Adds a player to this GameWorld.
     * @param player The underlying Minecraft player.
     * @param gamePlayer The Riftseeker player to associate with the Minecraft player.
     */
    public void addPlayer(Player player, GamePlayer gamePlayer) {
        players.put(player, gamePlayer);
    }

    /**
     * Removes a player from this GameWorld.
     * @param player The player to remove.
     */
    public void removePlayer(Player player) {
        players.remove(player);
    }

    public static GameWorld getInstance() {
        if (instance == null) {
            instance = new GameWorld();
        }

        return instance;
    }

    public static class UnknownLocation extends GameLocation {
        public static UnknownLocation UNKNOWN_LOCATION = new UnknownLocation();
        public UnknownLocation() {
            super("UNKNOWN_LOCATION");

            this.setShowTitle(false);
            this.setPriority(-1);
        }
    }
}
