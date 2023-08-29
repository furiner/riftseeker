package net.arcadiasedge.riftseeker.entities.players;

import net.arcadiasedge.riftseeker.api.ApiProfile;
import net.arcadiasedge.riftseeker.entities.GameEntity;
import net.arcadiasedge.riftseeker.entities.statuses.TexturePackStatus;
import net.arcadiasedge.riftseeker.world.GameWorld;
import net.arcadiasedge.riftseeker.world.locations.GameLocation;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * A representation of a player in the game, catered towards having MMO-like elements in the game.
 * This class is used to store player data, such as their inventory, online profile, among other things.
 */
public class GamePlayer extends GameEntity<Player> {
    /**
     * A static map of all players in this instance, mapped to their respective GamePlayer object.
     */
    public static Map<Player, GamePlayer> players = new HashMap<>();

    /**
     * The cached database profile of the player.
     * This should not be used to get player's statistics, as it is not updated in real-time.
     * Instead, use {@link GamePlayer#statistics} to get the player's statistics.
     */
    public ApiProfile profile;

    /**
     * The player's current inventory, which is used to store custom Riftseeker items and armor.
     */
    public GamePlayerInventory inventory;

    /**
     * The texture pack status of the player.
     * When they are loaded, custom textures, models, and shaders will be applied to enhance the player's experience.
     */
    public TexturePackStatus texturePackStatus;

    public GameLocation location;

    public GamePlayer(ApiProfile profile, Player player) {
        super(player);

        this.profile = profile;
        this.inventory = new GamePlayerInventory(this);
        this.texturePackStatus = TexturePackStatus.Loading;
        this.location = GameWorld.UnknownLocation.UNKNOWN_LOCATION;
    }

    public GamePlayerInventory getInventory() {
        return inventory;
    }

    public ApiProfile getProfile() {
        return profile;
    }

    public static GamePlayer get(Player player) {
        return players.get(player);
    }

    public static void add(Player player, GamePlayer gamePlayer) {
        players.put(player, gamePlayer);
    }

    public static void remove(Player player) {
        players.remove(player);
    }

    @Override
    public void update() {
        // Ultimately, this is a no-op, but it's here for consistency
        // and incase we ever need to do something with the player
        // entity.
    }

    @Override
    public void setup() {
        // This is a no-op, but it's here for consistency
        // and incase we ever need to do something with the player
        // entity.

        // Setup is largely handled by the GamePlayer constructor,
        // and other events.
        return;
    }
}
