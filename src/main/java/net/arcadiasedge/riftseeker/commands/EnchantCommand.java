package net.arcadiasedge.riftseeker.commands;

import dev.jorel.commandapi.annotations.Command;
import dev.jorel.commandapi.annotations.Default;
import dev.jorel.commandapi.annotations.arguments.AIntegerArgument;
import dev.jorel.commandapi.annotations.arguments.AStringArgument;
import net.arcadiasedge.riftseeker.RiftseekerPlugin;
import net.arcadiasedge.riftseeker.entities.players.GamePlayer;
import net.arcadiasedge.riftseeker.items.Item;
import net.arcadiasedge.riftseeker.items.enchantments.Enchantment;
import net.arcadiasedge.riftseeker.managers.EnchantmentManager;
import net.arcadiasedge.riftseeker.manufacturers.ItemManufacturer;
import net.arcadiasedge.riftseeker.world.GameWorld;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

@Command("enchant")
public class EnchantCommand extends RiftseekerCommand {
    private static Item heldItem;

    @Default
    public static void enchantItem(Player player, @AStringArgument String enchant, @AIntegerArgument int level) {
        GamePlayer gamePlayer = GameWorld.getInstance().getPlayer(player);

        if (gamePlayer.getInventory().getHeld() == null) {
            player.sendMessage(Component.text("You must be holding an item to enchant it.").color(NamedTextColor.RED));
            return;
        }

        EnchantmentManager em = RiftseekerPlugin.getInstance().getClassManager("enchantments");
        Enchantment enchantment;

        try {
            enchantment = em.create(enchant.toUpperCase());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (enchantment == null) {
            player.sendMessage(Component.text("This enchantment does not exist.").color(NamedTextColor.RED));
            return;
        }

        var heldItem = gamePlayer.getInventory().getHeld();
        enchantment.setLevel(level);

        if (!enchantment.canApply(heldItem)) {
            player.sendMessage(Component.text("This enchantment cannot be applied to this item.").color(NamedTextColor.RED));
            return;
        }

        // Check if the item already has the enchantment.
        Enchantment toRemove = null;
        for (var heldEnchantment : heldItem.enchantments) {
            if (heldEnchantment.getId() == enchantment.getId() && heldEnchantment.getLevel() == enchantment.getLevel()) {
                player.sendMessage(Component.text("This item already has this enchantment.").color(NamedTextColor.RED));
                return;
            }

            // Remove the enchantment if it exists.
            if (heldEnchantment.getId() == enchantment.getId()) {
                // TODO: find a better way to do this. GamePlayer should not be responsible for remiving enchantments.
                // Actually, the inventory should be responsible for this.
                toRemove = heldEnchantment;
                break;
            }
        }

        if (toRemove != null) {
            heldItem.removeEnchantment(toRemove);
        }

        heldItem.addEnchantment(enchantment);

        // Update the item's nbt.
        ItemManufacturer.constructNbtData(heldItem, gamePlayer);
    }
}