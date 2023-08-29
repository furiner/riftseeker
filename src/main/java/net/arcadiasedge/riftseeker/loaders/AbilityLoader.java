package net.arcadiasedge.riftseeker.loaders;

import net.arcadiasedge.exodus.loaders.ExodusLoader;
import net.arcadiasedge.riftseeker.RiftseekerPlugin;
import net.arcadiasedge.riftseeker.abilities.Ability;
import net.arcadiasedge.riftseeker.items.Item;
import net.arcadiasedge.riftseeker.managers.AbilityManager;
import net.arcadiasedge.riftseeker.managers.ItemManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;

public class AbilityLoader extends ExodusLoader<Ability> {
    public AbilityLoader(JavaPlugin plugin) {
        super(Ability.class, plugin);

        this.basePackage = "net.arcadiasedge.riftseeker.abilities";
    }

    @Override
    protected void load(Class<?> object) {
        try {
            var ability = object.getDeclaredConstructor().newInstance();
            if (ability instanceof Ability) {
                System.out.println("Ability: " + object.getName());
                System.out.println("Safe Name: " + object.getSimpleName().replace("Ability", ""));
                AbilityManager cm = RiftseekerPlugin.getInstance().getClassManager("abilities");
                // Remove the word "Ability" from the end of the class name if it exists (e.g. "FireballAbility" -> "Fireball")
                cm.register(object.getSimpleName().replace("Ability", ""), (Class<Ability>) object);
            }

            // force a gc
            ability = null;
        } catch (final IllegalAccessException | InstantiationException exception) {
            exception.printStackTrace();
        } catch (NoSuchMethodException e) {
            // no constructor
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
