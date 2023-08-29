package net.arcadiasedge.riftseeker.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class VanillaCreatureEvents implements Listener {
    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL) {
            // Prevent any mobs from naturally being spawned.
            event.setCancelled(true);
        }
    }
}
