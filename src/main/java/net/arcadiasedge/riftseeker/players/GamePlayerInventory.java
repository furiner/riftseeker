package net.arcadiasedge.riftseeker.players;

import net.arcadiasedge.riftseeker.items.Item;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class GamePlayerInventory {
    private final GamePlayer gamePlayer;

    private Item heldItem;

    public Map<ItemStack, Item> items;
    public Map<ItemStack, Item> armor;

    public GamePlayerInventory(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
        this.items = new HashMap<>();
    }

    public void add(Item item) {
        // Check if the item is already in the player's inventory
        if (!gamePlayer.getPlayer().getInventory().contains(item.itemStack)) {
            // Add the item from the player's inventory
            items.put(item.itemStack, item);
            gamePlayer.getPlayer().getInventory().addItem(item.itemStack);
        }
    }

    public void remove(Item item) {
        this.remove(item.itemStack);
    }

    public void remove(ItemStack itemStack) {
        // Check if the item is already in the player's inventory
        if (gamePlayer.getPlayer().getInventory().contains(itemStack)) {
            // Remove the item from the player's inventory
            items.remove(itemStack);
            gamePlayer.getPlayer().getInventory().removeItem(itemStack);
        }
    }

    public Item get(Item item) {
        return items.get(item.itemStack);
    }

    public Item get(ItemStack itemStack) {
        return items.get(itemStack);
    }

    public boolean contains(Item item) {
        return items.containsValue(item);
    }

    public boolean contains(ItemStack itemStack) {
        return items.containsKey(itemStack);
    }

    public void setHeldItem(Item item) {
        this.heldItem = item;
    }

    public Item getHeldItem() {
        return heldItem;
    }
}
