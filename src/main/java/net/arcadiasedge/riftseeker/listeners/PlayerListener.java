package net.arcadiasedge.riftseeker.listeners;

import net.arcadiasedge.riftseeker.api.ApiProfile;
import net.arcadiasedge.riftseeker.entities.players.GamePlayer;
import net.arcadiasedge.riftseeker.entities.statuses.TexturePackStatus;
import net.arcadiasedge.riftseeker.world.GameWorld;
import net.arcadiasedge.vespera.paper.StringFormats;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;

public class PlayerListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        ApiProfile profile;
        event.setJoinMessage(null);

        try {
            profile = ApiProfile.fetchOrCreate(event.getPlayer().getUniqueId());
        } catch (Exception e) {
            Bukkit.getLogger().severe("Failed to fetch player's profile from API");
            Bukkit.getLogger().severe(e.getMessage());

            return;
        }

        var gamePlayer = new GamePlayer(profile, event.getPlayer());
        gamePlayer.setup();
        GameWorld.getInstance().addPlayer(event.getPlayer(), gamePlayer);

        // TODO: Load the player's inventory from the database

        // Add the player to the armor content tracker
        PlayerInventoryListener.savedContents.put(event.getPlayer().getUniqueId(), event.getPlayer().getInventory().getArmorContents());

        // Send the player a welcome message
        event.getPlayer().sendMessage(MiniMessage.miniMessage().deserialize("<color:#fcc660>Welcome to <gradient:#7b578f:#6952eb>Riftseeker</gradient>, your gateway to the edge."));
        event.getPlayer().sendMessage(MiniMessage.miniMessage().deserialize("<color:#4a4952><i>https://example.com"));
        event.getPlayer().sendMessage("");
        event.getPlayer().sendMessage(MiniMessage.miniMessage().deserialize("<color:#303030>Profile ID: " + profile.id));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        GameWorld.getInstance().removePlayer(event.getPlayer());

        // Remove the player from the armor content tracker
        PlayerInventoryListener.savedContents.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerResourcePackStatus(PlayerResourcePackStatusEvent event) {
        var gamePlayer = GameWorld.getInstance().getPlayer(event.getPlayer());
        gamePlayer.texturePackStatus = TexturePackStatus.fromStatus(event.getStatus());
    }
}
