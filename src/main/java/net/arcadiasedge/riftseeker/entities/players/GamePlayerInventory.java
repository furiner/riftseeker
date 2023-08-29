package net.arcadiasedge.riftseeker.entities.players;

import net.arcadiasedge.riftseeker.items.Item;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * The inventory of a {@link GamePlayer}.
 * This class is used to store custom Riftseeker items and armor for a player.
 */
public class GamePlayerInventory {
    private final GamePlayer gamePlayer;

    private Item heldItem;

    /**
     * A map of all the items in the player's inventory.
     */
    public Map<ItemStack, Item> items;
    /**
     * A map of all the armor in the player's inventory.
     */
    public Map<ItemStack, Item> armor;

    public GamePlayerInventory(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
        this.items = new HashMap<>();
    }

    /**
     * Adds an item to the player's inventory.
     * If the item is already in the player's inventory, it will not be added.
     * @param item The item to add.
     */
    public void add(Item item) {
        // Check if the item is already in the player's inventory
        if (!gamePlayer.getEntity().getInventory().contains(item.itemStack)) {
            // Add the item from the player's inventory
            items.put(item.itemStack, item);
            gamePlayer.getEntity().getInventory().addItem(item.itemStack);
        }
    }

    /**
     * Removes an item from the player's inventory.
     * No-op if the item already isn't in the player's inventory.
     * @param item The item to remove.
     */
    public void remove(Item item) {
        this.remove(item.itemStack);
    }

    /**
     * Removes an item from the player's inventory.
     * No-op if the item already isn't in the player's inventory.
     * @param itemStack The item to remove.
     */
    public void remove(ItemStack itemStack) {
        // Check if the item is already in the player's inventory
        if (gamePlayer.getEntity().getInventory().contains(itemStack)) {
            // Remove the item from the player's inventory
            items.remove(itemStack);
            gamePlayer.getEntity().getInventory().removeItem(itemStack);
        }
    }

    /**
     * Fetches a Riftseeker item from the player's inventory.
     * @param item The item to fetch.
     * @return The item, or null if it isn't in the player's inventory.
     */
    public Item get(Item item) {
        return items.get(item.itemStack);
    }

    /**
     * Fetches a Riftseeker item from the player's inventory.
     * @param itemStack The item to fetch.
     * @return The item, or null if it isn't in the player's inventory.
     */
    public Item get(ItemStack itemStack) {
        return items.get(itemStack);
    }

    /**
     * Whether the player's inventory contains the provided Riftseeker item.
     * @param item The item to check for.
     * @return True if the player's inventory contains the item, otherwise false.
     */
    public boolean contains(Item item) {
        return items.containsValue(item);
    }

    /**
     * Whether the player's inventory contains the provided Riftseeker item.
     * @param itemStack The item to check for.
     * @return True if the player's inventory contains the item, otherwise false.
     */
    public boolean contains(ItemStack itemStack) {
        return items.containsKey(itemStack);
    }

    /**
     * Sets the player's held item.
     * @param item The item to set.
     */
    public void setHeldItem(Item item) {
        this.heldItem = item;
    }

    /**
     * Fetches the player's held item.
     * @return The player's held item.
     */
    public Item getHeldItem() {
        return heldItem;
    }
}
