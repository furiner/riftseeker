package net.arcadiasedge.riftseeker.events;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class PlayerEquipArmorEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final Player player;
    private ItemStack itemStack;
    private EquipmentSlot equipmentSlot;

    public PlayerEquipArmorEvent(Player player, ItemStack itemStack, EquipmentSlot equipmentSlot) {
        this.player = player;
        this.itemStack = itemStack;
        this.equipmentSlot = equipmentSlot;
    }

    public Player getPlayer() {
        return this.player;
    }

    public ItemStack getItemStack() {
        return this.itemStack;
    }

    public EquipmentSlot getEquipmentSlot() {
        return this.equipmentSlot;
    }

    @Override
    public HandlerList getHandlers() {
        return PlayerEquipArmorEvent.HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return PlayerEquipArmorEvent.HANDLER_LIST;
    }
}
