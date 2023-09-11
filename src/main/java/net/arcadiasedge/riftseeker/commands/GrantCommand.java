package net.arcadiasedge.riftseeker.commands;

import dev.jorel.commandapi.annotations.Command;
import dev.jorel.commandapi.annotations.Default;
import dev.jorel.commandapi.annotations.arguments.APlayerArgument;
import dev.jorel.commandapi.annotations.arguments.AStringArgument;
import net.arcadiasedge.riftseeker.entities.players.GamePlayer;
import net.arcadiasedge.riftseeker.items.Item;
import net.arcadiasedge.riftseeker.manufacturers.ItemManufacturer;
import net.arcadiasedge.riftseeker.world.GameWorld;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

@Command("grant")
public class GrantCommand extends RiftseekerCommand {
    @Default
    public static void grantItem(Player player, @APlayerArgument Player target, @AStringArgument String item) {
        GamePlayer receiver = GameWorld.getInstance().getPlayer(target);
        GamePlayer sender = GameWorld.getInstance().getPlayer(player);

        if (receiver == null) {
            player.sendMessage(Component.text("This player is not online.").color(NamedTextColor.RED));
            return;
        }

        // Get the item from the item ID
        Item createdItem = ItemManufacturer.grant(item, receiver, sender);

        if (createdItem == null) {
            player.sendMessage(Component.text("This item does not exist.").color(NamedTextColor.RED));
            return;
        }

        receiver.getInventory().add(createdItem);
    }
}
