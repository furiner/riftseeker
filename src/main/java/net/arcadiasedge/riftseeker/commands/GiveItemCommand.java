package net.arcadiasedge.riftseeker.commands;

import dev.jorel.commandapi.annotations.Command;
import dev.jorel.commandapi.annotations.Default;
import dev.jorel.commandapi.annotations.arguments.AStringArgument;
import net.arcadiasedge.riftseeker.items.Item;
import net.arcadiasedge.riftseeker.entities.players.GamePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

@Command("giveitem")
public class GiveItemCommand extends RiftseekerCommand {
    @Default
    public static void giveItem(Player player, @AStringArgument String item) {
        GamePlayer gamePlayer = GamePlayer.get(player);

        // Get the item from the item ID
        Item createdItem = Item.create(item.toUpperCase());

        if (createdItem == null) {
            player.sendMessage(Component.text("This item does not exist.").color(NamedTextColor.RED));
            return;
        }

        gamePlayer.getInventory().add(createdItem);
    }
}
