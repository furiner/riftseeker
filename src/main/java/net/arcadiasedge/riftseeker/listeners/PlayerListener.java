package net.arcadiasedge.riftseeker.listeners;

import net.arcadiasedge.riftseeker.api.ApiProfile;
import net.arcadiasedge.riftseeker.entities.players.GamePlayer;
import net.arcadiasedge.riftseeker.entities.statuses.TexturePackStatus;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        ApiProfile profile;

        try {
            profile = ApiProfile.fetchOrCreate(event.getPlayer().getUniqueId());
        } catch (Exception e) {
            Bukkit.getLogger().severe("Failed to fetch player's profile from API");
            Bukkit.getLogger().severe(e.getMessage());

            return;
        }

        GamePlayer.add(event.getPlayer(), new GamePlayer(profile, event.getPlayer()));

        // Send the player a welcome message
        event.getPlayer().sendMessage(MiniMessage.miniMessage().deserialize("<color:#fcc660>Welcome to <gradient:#7b578f:#6952eb>Riftseeker</gradient>, your gateway to the edge."));
        event.getPlayer().sendMessage(MiniMessage.miniMessage().deserialize("<color:#4a4952><i>https://example.com"));
        event.getPlayer().sendMessage("");
        event.getPlayer().sendMessage(MiniMessage.miniMessage().deserialize("<color:#303030>Profile ID: " + profile.id));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        GamePlayer.remove(event.getPlayer());
    }
}
