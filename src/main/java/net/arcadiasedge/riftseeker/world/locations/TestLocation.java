package net.arcadiasedge.riftseeker.world.locations;

import net.arcadiasedge.riftseeker.world.GameWorld;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;

public class TestLocation extends GameLocation {
    public TestLocation() {
        super("TEST_LOCATION");

        var world = GameWorld.getInstance().getWorld();

        this.setDisplayName(MiniMessage.miniMessage().deserialize("<gradient:#ff0000:#00ff00>Test Location</gradient>"));

        // Add a few points of interest
        this.points.add(new Location(world, -5.0, -60.0, -5.0));
        this.points.add(new Location(world, -5.0, -60.0, 5.0));
        this.points.add(new Location(world, 5.0, -60.0, -5.0));
        this.points.add(new Location(world, 5.0, -60.0, 5.0));
        this.points.add(new Location(world, -5.0, 256.0, -5.0));
        this.points.add(new Location(world, -5.0, 256.0, 5.0));
        this.points.add(new Location(world, 5.0, 256.0, -5.0));
        this.points.add(new Location(world, 5.0, 256.0, 5.0));

        this.setPriority(1);
    }
}
