package net.arcadiasedge.riftseeker.loaders;

import net.arcadiasedge.exodus.loaders.ExodusLoader;
import net.arcadiasedge.riftseeker.RiftseekerPlugin;
import net.arcadiasedge.riftseeker.items.Item;
import net.arcadiasedge.riftseeker.managers.ItemManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;

public class ItemLoader extends ExodusLoader<Item> {
    public ItemLoader(JavaPlugin plugin) {
        super(Item.class, plugin);

        this.basePackage = "net.arcadiasedge.riftseeker.items";
    }

    @Override
    protected void load(Class<?> object) {
        try {
            var item = object.getDeclaredConstructor().newInstance();
            if (item instanceof Item) {
                ItemManager cm = RiftseekerPlugin.getInstance().getClassManager("items");
                cm.register(((Item) item).id, (Class<Item>) object);
            }

            // force a gc
            item = null;
        } catch (final IllegalAccessException | InstantiationException exception) {
        } catch (NoSuchMethodException e) {
            // no constructor
        } catch (InvocationTargetException e) {
        }
    }
}
