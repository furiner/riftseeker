package net.arcadiasedge.riftseeker.loaders;

import net.arcadiasedge.exodus.loaders.ExodusLoader;
import net.arcadiasedge.riftseeker.RiftseekerPlugin;
import net.arcadiasedge.riftseeker.items.Item;
import net.arcadiasedge.riftseeker.items.sets.SetEffect;
import net.arcadiasedge.riftseeker.managers.ItemManager;
import net.arcadiasedge.riftseeker.managers.SetEffectManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;

public class SetEffectLoader extends ExodusLoader<SetEffect> {
    public SetEffectLoader(JavaPlugin plugin) {
        super(SetEffect.class, plugin);

        this.basePackage = "net.arcadiasedge.riftseeker.items";
    }

    @Override
    protected void load(Class<?> object) {
        try {
            var item = object.getDeclaredConstructor().newInstance();
            if (item instanceof SetEffect) {
                SetEffectManager cm = RiftseekerPlugin.getInstance().getClassManager("sets");
                cm.register(((SetEffect) item).id, (Class<SetEffect>) object);
            }

            // force a gc
            item = null;
        } catch (final IllegalAccessException | InstantiationException exception) {
        } catch (NoSuchMethodException e) {
        } catch (InvocationTargetException e) {
        }
    }
}
