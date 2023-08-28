package net.arcadiasedge.riftseeker;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import dev.jorel.commandapi.CommandAPIConfig;
import net.arcadiasedge.exodus.loaders.Loader;
import net.arcadiasedge.riftseeker.loaders.CommandLoader;
import net.arcadiasedge.riftseeker.loaders.ItemLoader;
import net.arcadiasedge.riftseeker.loaders.ListenerLoader;
import net.arcadiasedge.riftseeker.managers.ClassManager;
import net.arcadiasedge.riftseeker.managers.ItemManager;
import net.arcadiasedge.riftseeker.managers.Manager;
import net.arcadiasedge.riftseeker.managers.PlayerManager;
import net.arcadiasedge.riftseeker.players.GamePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class RiftseekerPlugin extends JavaPlugin {
    public static RiftseekerPlugin INSTANCE = null;
    public Map<Player, GamePlayer> players;

    public Map<String, Manager<?>> managers;

    public Map<String, Loader<?>> loaders;

    public RiftseekerPlugin() {
        players = new HashMap<>();
        managers = new HashMap<>();
        loaders = new HashMap<>();

        this.addLoader(new ItemLoader(this));
        this.addLoader(new ListenerLoader(this));
        this.addLoader(new CommandLoader(this));
    }

    @Override
    public void onLoad() {
        CommandAPI.onLoad(new CommandAPIBukkitConfig(this).verboseOutput(true));
    }


    @Override
    public void onEnable() {
        INSTANCE = this;

        CommandAPI.onEnable();
        this.managers.put("items", new ItemManager());
        this.managers.put("players", new PlayerManager());

        for (Loader<?> loader : loaders.values()) {
            loader.handle();
        }
    }

    @Override
    public void onDisable() {
        INSTANCE = null;
        CommandAPI.onDisable();
    }

    public <T extends Manager<?>> T getManager(String name) {
        return (T) managers.get(name);
    }

    public <T extends ClassManager<?>> T getClassManager(String name) {
        return (T) managers.get(name);
    }

    /**
     * Adds a loader to the plugin.
     * @param loader The loader to add.
     */
    public void addLoader(Loader loader) {
        this.loaders.put(loader.getClass().getName(), loader);
    }

    public static RiftseekerPlugin getInstance() {
        return INSTANCE;
    }
}
