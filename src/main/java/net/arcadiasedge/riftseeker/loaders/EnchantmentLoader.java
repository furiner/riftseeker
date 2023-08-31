package net.arcadiasedge.riftseeker.loaders;

import net.arcadiasedge.exodus.loaders.ExodusLoader;
import net.arcadiasedge.riftseeker.RiftseekerPlugin;
import net.arcadiasedge.riftseeker.abilities.Ability;
import net.arcadiasedge.riftseeker.items.enchantments.Enchantment;
import net.arcadiasedge.riftseeker.managers.EnchantmentManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;

public class EnchantmentLoader extends ExodusLoader<Enchantment> {
    public EnchantmentLoader(JavaPlugin plugin) {
        super(Enchantment.class, plugin);
        this.basePackage = "net.arcadiasedge.riftseeker.items.enchantments";
    }

    @Override
    protected void load(Class<?> object) {
        try {
            var enchantment = object.getDeclaredConstructor().newInstance();
            if (enchantment instanceof Enchantment) {
                EnchantmentManager cm = RiftseekerPlugin.getInstance().getClassManager("enchantments");
                cm.register(((Enchantment) enchantment).getId(), (Class<Enchantment>) object);
            }

            // force a gc
            enchantment = null;
        } catch (final IllegalAccessException | InstantiationException exception) {
        } catch (NoSuchMethodException e) {
            // no constructor
        } catch (InvocationTargetException e) {
        }
    }
}
