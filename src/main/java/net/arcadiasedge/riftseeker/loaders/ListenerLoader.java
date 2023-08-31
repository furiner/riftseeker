package net.arcadiasedge.riftseeker.loaders;

import net.arcadiasedge.exodus.loaders.ExodusLoader;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.logging.Level;

public final class ListenerLoader extends ExodusLoader<Listener> {
    public ListenerLoader(JavaPlugin plugin) {
        super(Listener.class, plugin);

        this.basePackage = "net.arcadiasedge.riftseeker.listeners";
    }

    @Override
    protected final boolean filter(final Class<? extends Listener> object) {
        return !Modifier.isAbstract(object.getModifiers());
    }

    @Override
    protected void load(Class<?> object) {
        try {
            if (object.getName().toLowerCase().contains("listener")) {
                Bukkit.getLogger().log(Level.INFO, "Registering listener " + object.getName() + "...");
                Bukkit.getLogger().log(Level.INFO, this.basePackage + "...");
                Bukkit.getLogger().log(Level.INFO, this.plugin.getClass().getPackageName() + "...");
                Bukkit.getServer()
                        .getPluginManager()
                        .registerEvents((Listener) object.newInstance(), this.plugin);
            }

        } catch (final IllegalAccessException | InstantiationException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    protected void unload(Class<? extends Listener> object) {
        // TODO: Implement this
        return;
    }
}