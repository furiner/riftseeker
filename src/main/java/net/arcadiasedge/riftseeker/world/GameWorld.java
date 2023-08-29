package net.arcadiasedge.riftseeker.world;

import net.arcadiasedge.riftseeker.world.locations.GameLocation;
import net.arcadiasedge.riftseeker.world.locations.TestLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;

public class GameWorld {
    public static GameWorld instance = null;
    public Map<String, GameLocation> locations;
    public World world;
    public GameWorld() {
        this.locations = new HashMap<String, GameLocation>();

        // Set the default world
        this.setWorld(Bukkit.getServer().getWorlds().get(0));
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public World getWorld() {
        return world;
    }

    public void addLocation(GameLocation location) {
        this.locations.put(location.getName(), location);
    }

    public GameLocation getLocationFor(Location location) {
        for (var loc : this.locations.values()) {
            if (loc.contains(location)) {
                return loc;
            }
        }

        return null;
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
