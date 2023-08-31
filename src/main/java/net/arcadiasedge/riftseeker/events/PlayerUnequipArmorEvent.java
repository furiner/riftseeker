package net.arcadiasedge.riftseeker.events;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

@Getter
@Setter
public class PlayerUnequipArmorEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final Player player;
    private ItemStack itemStack;
    private EquipmentSlot equipmentSlot;

    public PlayerUnequipArmorEvent(Player player, ItemStack itemStack, EquipmentSlot equipmentSlot) {
        this.player = player;
        this.itemStack = itemStack;
    }

    @Override
    public HandlerList getHandlers() {
        return PlayerUnequipArmorEvent.HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return PlayerUnequipArmorEvent.HANDLER_LIST;
    }
}
