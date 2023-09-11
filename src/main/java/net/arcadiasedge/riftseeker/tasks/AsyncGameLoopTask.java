package net.arcadiasedge.riftseeker.tasks;

import net.arcadiasedge.riftseeker.entities.GameEntity;
import net.arcadiasedge.riftseeker.world.GameWorld;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * This is a representation of the game loop.
 *
 * It nightmarishly gets ran **every tick**, so we need to be careful with what we put in here.
 */
public class AsyncGameLoopTask extends BukkitRunnable {
    private final GameWorld gameWorldInstance;

    public AsyncGameLoopTask(){
        this.gameWorldInstance = GameWorld.getInstance();
    }

    @Override
    public void run() {
        for (GameEntity<?> entity : gameWorldInstance.getEntities()) {
            entity.update();
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
