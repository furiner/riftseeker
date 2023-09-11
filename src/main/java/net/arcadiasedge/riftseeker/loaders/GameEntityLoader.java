package net.arcadiasedge.riftseeker.loaders;

import net.arcadiasedge.riftseeker.RiftseekerPlugin;
import net.arcadiasedge.riftseeker.entities.GameEntity;
import net.arcadiasedge.riftseeker.managers.ClassManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Modifier;

public class GameEntityLoader extends RiftseekerLoader<GameEntity> {
    public GameEntityLoader(JavaPlugin plugin) {
        super(GameEntity.class, "net.arcadiasedge.riftseeker.entities", RiftseekerPlugin.getInstance().getClassManager("entities"));
    }

    @Override
    protected void load(Class<?> object) {
        var baseClass = ClassManager.getBaseClass(object);

        if (baseClass.equals(subType)) {
            if (object.getSimpleName().equals("GamePlayer") || Modifier.isAbstract(object.getModifiers())) {
                return;
            }
            // Remove the word "Game" from the beginning
            var id = object.getSimpleName();

            if (id.startsWith("Game")) {
                id = id.substring(4);
            }

            // Split the name by capital letters, readd them with underscores
            id = object.getSimpleName().replaceAll("([A-Z])", "_$1").toUpperCase();

            // Replace N_P_C with NPC
            if (id.contains("N_P_C")) {
                id = id.replace("N_P_C", "NPC");
            }

            if (id.endsWith("_ENTITY")) {
                id = id.substring(0, id.length() - 7);
            }

            // remove the first underscore
            id = id.substring(1);

            System.out.println("Loading entity " + id);
            System.out.println(object.getSimpleName());

            manager.entries.put(id, (Class<GameEntity>) object);
        }
    }
}
