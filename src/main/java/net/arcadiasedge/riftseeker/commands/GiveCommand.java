package net.arcadiasedge.riftseeker.commands;

import dev.jorel.commandapi.annotations.Command;
import dev.jorel.commandapi.annotations.Default;
import dev.jorel.commandapi.annotations.arguments.AIntegerArgument;
import dev.jorel.commandapi.annotations.arguments.APlayerArgument;
import dev.jorel.commandapi.annotations.arguments.AStringArgument;
import net.arcadiasedge.riftseeker.items.Item;
import net.arcadiasedge.riftseeker.entities.players.GamePlayer;
import net.arcadiasedge.riftseeker.world.GameWorld;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Command("give")
public class GiveCommand extends RiftseekerCommand {
    @Default
    public static void giveItem(Player player, @APlayerArgument Player target, @AStringArgument String item) {
        GamePlayer gamePlayer = GameWorld.getInstance().getPlayer(target);

        if (gamePlayer == null) {
            player.sendMessage(Component.text("This player is not online.").color(NamedTextColor.RED));
            return;
        }

        // Get the item from the item ID
        Item createdItem = Item.create(item.toUpperCase(), gamePlayer);

        if (createdItem == null) {
            player.sendMessage(Component.text("This item does not exist.").color(NamedTextColor.RED));
            return;
        }

        // TODO: Handle multiple item creation.
        // Sort through the player's inventory
        /*List<Item> createdItems = new ArrayList<>();
        Item currentItem = createdItem;
        int amountCreated = 0;

        for (var inventoryItem : gamePlayer.getInventory().getItems()) {
            if (currentItem.getId().equals(inventoryItem.getId())) {
                // Check the item's stack size
                var itemCount = inventoryItem.itemStack.getAmount();
                var maxStack = inventoryItem.getApiItem().properties.maxStack;



                var difference = currentItem.getApiItem().properties.maxStack - itemCount;
            }
        }

        if (amountCreated < amount) {
            // Create a new item
            while (amountCreated < amount) {
                createdItem = Item.create(item.toUpperCase(), gamePlayer);

                createdItems.add(createdItem);
                amountCreated++;
            }
        }

        createdItem = null;*/

        gamePlayer.getInventory().add(createdItem);
    }
}
