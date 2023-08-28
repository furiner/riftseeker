package net.arcadiasedge.riftseeker.players;

import net.arcadiasedge.riftseeker.RiftseekerPlugin;
import net.arcadiasedge.riftseeker.api.ApiProfile;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class GamePlayer {
    public static Map<Player, GamePlayer> players = new HashMap<>();

    public Player player;

    public ApiProfile profile;

    public GamePlayerInventory inventory;

    public GamePlayer(ApiProfile profile, Player player) {
        this.profile = profile;
        this.player = player;
        this.inventory = new GamePlayerInventory(this);
    }

    public GamePlayerInventory getInventory() {
        return inventory;
    }

    public Player getPlayer() {
        return player;
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
}
