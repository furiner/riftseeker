package net.arcadiasedge.riftseeker.tasks;

import net.arcadiasedge.riftseeker.entities.GameEntity;
import net.arcadiasedge.riftseeker.entities.NPCEntity;
import net.arcadiasedge.riftseeker.world.GameWorld;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * This is a representation of the game loop.
 *
 * It nightmarishly gets ran **every tick**, so we need to be careful with what we put in here.
 */
public class GameLoopTask extends BukkitRunnable {
    @Override
    public void run() {
        for (GameEntity<?> entity : GameWorld.getInstance().getEntities()){
            var appliedChanges = entity.getStatistics().apply();

            if (appliedChanges.size() > 0) {
                // Changes were applied, update the entity.
                if (entity instanceof NPCEntity<?>) {
                    ((NPCEntity<?>) entity).assignDisplayName();
                } else {
                    // TODO: Find shit to put here
                }
            }
        }
        /*for (GameEntity entity : GameEntity.entities) {
            if (entity instanceof GamePlayer) {
                entity.getEntity().getBoundingBox();

                // Player related stuff
                var location = GameWorld.getInstance().getLocationFor(entity.getEntity().getLocation());

                if (location != null && location != ((GamePlayer) entity).location) {
                    if (location.getShowTitle()) {
                        entity.getEntity().sendMessage(location.displayName);
                    }

                    ((GamePlayer) entity).location = location;
                } else if (location == null && ((GamePlayer) entity).location != GameWorld.UnknownLocation.UNKNOWN_LOCATION) {
                    ((GamePlayer) entity).location = GameWorld.UnknownLocation.UNKNOWN_LOCATION;
                }
            } else {

            }
        }*/
    }
}
