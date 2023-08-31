package net.arcadiasedge.riftseeker;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import net.arcadiasedge.exodus.loaders.Loader;
import net.arcadiasedge.riftseeker.loaders.AbilityLoader;
import net.arcadiasedge.riftseeker.loaders.CommandLoader;
import net.arcadiasedge.riftseeker.loaders.ItemLoader;
import net.arcadiasedge.riftseeker.loaders.ListenerLoader;
import net.arcadiasedge.riftseeker.managers.*;
import net.arcadiasedge.riftseeker.entities.players.GamePlayer;
import net.arcadiasedge.riftseeker.tasks.AsyncGameLoopTask;
import net.arcadiasedge.riftseeker.tasks.GameLoopTask;
import net.arcadiasedge.riftseeker.world.GameWorld;
import net.arcadiasedge.riftseeker.world.locations.TestLocation;
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

        this.managers.put("items", new ItemManager());
        this.managers.put("players", new PlayerManager());
        this.managers.put("abilities", new AbilityManager());
        this.managers.put("sets", new SetEffectManager());
        this.managers.put("enchantments", new EnchantmentManager());
    }

    @Override
    public void onLoad() {
        INSTANCE = this;
        CommandAPI.onLoad(new CommandAPIBukkitConfig(this).verboseOutput(true));

        this.addLoader(new ItemLoader(this));
        this.addLoader(new ListenerLoader(this));
        this.addLoader(new CommandLoader(this));
        this.addLoader(new AbilityLoader(this));
        this.addLoader(new SetEffectLoader(this));
        this.addLoader(new EnchantmentLoader(this));
    }


    @Override
    public void onEnable() {

        CommandAPI.onEnable();

        for (Loader<?> loader : loaders.values()) {
            loader.handle();
        }

        // Add Citizen's Traits.
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(RiftseekerDefaultTrait.class));

        // Create a game world.
        GameWorld.getInstance();

        // Add Test Locations
        GameWorld.getInstance().addLocation(new TestLocation());

        // Register nightmares.
        var loop = new GameLoopTask();
        loop.runTaskTimerAsynchronously(this, 0L, 1L);
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

    public NamespacedKey getKey(String key) {
        return new NamespacedKey(this, key);
    }

    public static RiftseekerPlugin getInstance() {
        return INSTANCE;
    }
}
