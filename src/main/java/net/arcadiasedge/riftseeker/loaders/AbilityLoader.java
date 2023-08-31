package net.arcadiasedge.riftseeker.loaders;

import net.arcadiasedge.exodus.loaders.ExodusLoader;
import net.arcadiasedge.riftseeker.RiftseekerPlugin;
import net.arcadiasedge.riftseeker.abilities.Ability;
import net.arcadiasedge.riftseeker.items.Item;
import net.arcadiasedge.riftseeker.managers.AbilityManager;
import net.arcadiasedge.riftseeker.managers.ItemManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;

public class AbilityLoader extends RiftseekerLoader<Ability> {
    public AbilityLoader(JavaPlugin plugin) {
        super(Ability.class, "net.arcadiasedge.riftseeker.abilities", RiftseekerPlugin.getInstance().getClassManager("abilities"));
    }
}
