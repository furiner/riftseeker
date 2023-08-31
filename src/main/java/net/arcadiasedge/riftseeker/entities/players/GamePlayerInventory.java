package net.arcadiasedge.riftseeker.entities.players;

import de.tr7zw.nbtapi.NBT;
import net.arcadiasedge.riftseeker.RiftseekerPlugin;
import net.arcadiasedge.riftseeker.data.RiftseekerDataTypes;
import net.arcadiasedge.riftseeker.items.GameEquipmentSlot;
import net.arcadiasedge.riftseeker.items.Item;
import net.arcadiasedge.riftseeker.items.ItemType;
import net.arcadiasedge.riftseeker.items.sets.SetEffect;
import net.arcadiasedge.riftseeker.world.GameWorld;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.*;

/**
 * The inventory of a {@link GamePlayer}.
 * This class is used to store custom Riftseeker items and armor for a player.
 */
public class GamePlayerInventory {
    private final GamePlayer player;

    private Item held;

    private Map<GameEquipmentSlot, Item> equipment;

    private Map<String, SetEffect> setEffects;

    /**
     * A map of all the items in the player's inventory.
     */
    public Map<UUID, Item> items;

    public GamePlayerInventory(GamePlayer player) {
        this.player = player;
        this.items = new HashMap<>();
        this.setEffects = new HashMap<>();
        this.equipment = new HashMap<>();
    }

    /**
     * Adds an item to the player's inventory.
     * If the item is already in the player's inventory, it will be replaced.
     *
     * @param item The item to add.
     */
    public void add(Item item) {
        if (item == null) {
            return;
        }

        this.items.put(item.uuid, item);

        if (item.itemStack != null) {
            var inventory = player.getEntity().getInventory();

            if (!inventory.contains(item.itemStack)) {
                inventory.addItem(item.itemStack);
            }
        }
    }

    public void remove(Item item) {
        this.remove(item.uuid);
    }

    public void remove(UUID uuid) {
        // Get the item from the player's inventory
        Item item = items.get(uuid);

        if (item == null) {
            return;
        }

        if (item.baseItem.getType() == ItemType.EQUIPMENT) {
            // Check each armor slot to see if the item is equipped
            for (var entry : equipment.entrySet()) {
                if (entry.getValue() == item) {
                    // Remove the item from the player's equipment
                    equipment.remove(entry.getKey());
                    player.onUnequipArmor(item, entry.getKey());
                }
            }
        } else {
            // Remove the reference if it's a held item
            if (held == item) {
                player.onSwapItem(this.held, null);
                held = null;
            }
        }

        this.items.remove(uuid);

        // Check if the item is in the player's actual inventory
        if (item.itemStack != null) {
            var inventory = player.getEntity().getInventory();

            if (inventory.contains(item.itemStack)) {
                inventory.remove(item.itemStack);
            }
        }
    }

    public void remove(ItemStack itemStack) {
        Item item = this.get(itemStack);

        if (item != null) {
            this.remove(item.uuid);
        }
    }

    /**
     * Fetches a Riftseeker item from the player's inventory.
     *
     * @param item The item to fetch.
     * @return The item, or null if it isn't in the player's inventory.
     */
    public Item get(Item item) {
        return items.get(item.uuid);
    }

    /**
     * Fetches a Riftseeker item from the player's inventory.
     *
     * @param uuid The item to fetch.
     * @return The item, or null if it isn't in the player's inventory.
     */
    public Item get(UUID uuid) {
        return this.items.get(uuid);
    }

    public Item get(ItemStack itemStack) {
        if (itemStack == null) {
            return null;
        }

        System.out.println("Item stack: " + itemStack);

        // Get the item's UUID
        ItemMeta itemMeta = itemStack.getItemMeta();
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();

        if (container.has(RiftseekerPlugin.getInstance().getKey("item-uuid"), RiftseekerDataTypes.UUID)) {
            UUID uuid = container.get(RiftseekerPlugin.getInstance().getKey("item-uuid"), RiftseekerDataTypes.UUID);
            Item item = this.get(uuid);

            if (item == null) {
                return null;
            }

            if (item.itemStack != itemStack) {
                item.setItemStack(itemStack);
            }

            return item;
        } else {
            return null;
        }
    }

    /**
     * Sets the player's held item.
     *
     * @param item The item to set.
     */
    public void setHeld(Item item) {
        this.held = item;
    }

    /**
     * Fetches the player's held item.
     *
     * @return The player's held item.
     */
    public Item getHeld() {
        return held;
    }

    public Collection<Item> getItems() {
        return items.values();
    }

    /**
     * Fetches the item associated with the given equipment slot.
     * @param slot The equipment slot to fetch the item from.
     * @return The item in the given slot.
     */
    public Item getEquipmentPiece(GameEquipmentSlot slot) {
        return equipment.get(slot);
    }

    /**
     * Fetches the item associated with the given equipment slot.
     * @param slot The equipment slot to fetch the item from.
     * @return The item in the given slot.
     */
    public Item getEquipmentPiece(EquipmentSlot slot) {
        return equipment.get(GameEquipmentSlot.fromEquipmentSlot(slot));
    }

    /**
     * Gets a collection of all the items in the player's equipment.
     * @return A collection of all the items in the player's equipment.
     */
    public Collection<Item> getEquipment() {
        return equipment.values();
    }

    /**
     * Sets an item in the specified equipment slot for the player.
     * @param slot The slot to set the item in.
     * @param item The item to set.
     */
    public void setEquipmentPiece(GameEquipmentSlot slot, Item item) {
        equipment.put(slot, item);
    }

    /**
     * Sets an item in the specified equipment slot for the player.
     * @param slot The slot to set the item in.
     * @param item The item to set.
     */
    public void setEquipmentPiece(EquipmentSlot slot, Item item) {
        equipment.put(GameEquipmentSlot.fromEquipmentSlot(slot), item);
    }

    /**
     * Fetches a map of all the set effects the player has.
     * @return A map of all the set effects the player has.
     */
    public Map<String, SetEffect> getSetEffects() {
        return setEffects;
    }

    /**
     * Fetches a specific set effect from the player.
     * @param id The ID of the set effect to fetch from the player.
     * @return The set effect, or null if the player doesn't have it.
     */
    public SetEffect getSetEffect(String id) {
        return setEffects.get(id);
    }

    /**
     * Sets a set effect for the player.
     * @param setEffect The set effect to set.
     */
    public void setSetEffect(SetEffect setEffect) {
        this.setEffects.put(setEffect.id, setEffect);
    }

    /**
     * Removes a set effect from the player.
     * @param id The ID of the set effect to remove.
     */
    public void removeSetEffect(String id) {
        this.setEffects.remove(id);
    }
}
