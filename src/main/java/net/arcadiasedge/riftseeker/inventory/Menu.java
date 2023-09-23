package net.arcadiasedge.riftseeker.inventory;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

public class Menu {
    private final Inventory inventory;
    public Menu(Component title, InventoryType type) {
        this.inventory = Bukkit.createInventory(null,  type, title);
    }
}
