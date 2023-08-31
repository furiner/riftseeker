package net.arcadiasedge.riftseeker.loaders;

import net.arcadiasedge.exodus.loaders.ExodusLoader;
import net.arcadiasedge.riftseeker.RiftseekerPlugin;
import net.arcadiasedge.riftseeker.managers.ClassManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;

public class RiftseekerLoader<T> extends ExodusLoader<T> {
    public ClassManager<T> manager;

    public RiftseekerLoader(Class<T> subType, String packageName, ClassManager<T> manager) {
        super(subType, RiftseekerPlugin.getInstance());

        this.basePackage = packageName;
        this.manager = manager;
    }

    @Override
    protected void load(Class<?> object) {
        var baseClass = ClassManager.getBaseClass(object);

        if (baseClass.equals(subType)) {
            try {
                var constructor = object.getConstructor();
                var instance = (T)constructor.newInstance();

                if (instance != null) {
                    // Check if it has an id field
                    var idField = object.getDeclaredField("id");

                    if (idField != null) {
                        var id = (String)idField.get(instance);

                        manager.entries.put(id, (Class<T>) object);
                    }
                }
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                     InvocationTargetException e) {
                // Also an error, but we can still use it
                var id = object.getSimpleName().replace(baseClass.getSimpleName(), "");

                System.out.println("Error loading " + id + ": " + e.getMessage());

                manager.entries.put(id, (Class<T>) object);
            } catch (NoSuchFieldException e) {
                var id = object.getSimpleName().replace(baseClass.getSimpleName(), "");

                System.out.println("Error loading " + id + ": " + e.getMessage());

                manager.entries.put(id, (Class<T>) object);
            }
        }
    }
}
