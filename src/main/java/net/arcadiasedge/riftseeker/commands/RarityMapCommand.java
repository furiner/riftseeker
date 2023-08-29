package net.arcadiasedge.riftseeker.commands;

import dev.jorel.commandapi.annotations.Command;
import dev.jorel.commandapi.annotations.Default;
import net.arcadiasedge.riftseeker.utils.ColorMap;
import net.arcadiasedge.riftseeker.utils.rarity.RarityMap;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

@Command("raritymap")
public class RarityMapCommand extends RiftseekerCommand {
    @Default
    public static void rarityMap(Player player) {
        var minimessage = MiniMessage.miniMessage();

        player.sendMessage(minimessage.deserialize("<br> Rarity Map<br><color:" + ColorMap.GRAY_1 + "><i>This is a map of every rarity in the game, used for testing colors & texture packs.<br>Format: (Rarity) (Item Name)<br>"));
        player.sendMessage(minimessage.deserialize("    " + RarityMap.COMMON.getTag() + "<bold>COMMON</bold> Item"));
        player.sendMessage(minimessage.deserialize("    " + RarityMap.UNCOMMON.getTag() + "<bold>UNCOMMON</bold> Item"));
        player.sendMessage(minimessage.deserialize("    " + RarityMap.RARE.getTag() + "<bold>RARE</bold> Item"));
        player.sendMessage(minimessage.deserialize("    " + RarityMap.EPIC.getTag() + "<bold>EPIC</bold> Item"));
        player.sendMessage(minimessage.deserialize("    " + RarityMap.LEGENDARY.getTag() + "<bold>LEGENDARY</bold> Item<br>"));
        player.sendMessage(minimessage.deserialize("    " + RarityMap.DIVINE.getTag() + "<bold>DIVINE</bold> Item"));
        player.sendMessage(minimessage.deserialize("    " + RarityMap.RELIC.getTag() + "<bold>RELIC</bold> Item"));
        player.sendMessage(minimessage.deserialize("    " + RarityMap.ASCENDANT.getTag() + "<bold>ASCENDANT</bold> Item"));
        player.sendMessage(minimessage.deserialize("    " + RarityMap.ETERNAL.getTag() + "<bold>ETERNAL</bold> Item<br>"));
    }
}
