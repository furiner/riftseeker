package net.arcadiasedge.riftseeker.items;

import org.bukkit.inventory.EquipmentSlot;

public enum GameEquipmentSlot {
    BOOTS,
    LEGGINGS,
    CHESTPLATE,
    HELMET;

    public static GameEquipmentSlot fromEquipmentSlot(EquipmentSlot slot) {
        switch (slot) {
            case FEET:
                return GameEquipmentSlot.BOOTS;
            case LEGS:
                return GameEquipmentSlot.LEGGINGS;
            case CHEST:
                return GameEquipmentSlot.CHESTPLATE;
            case HEAD:
                return GameEquipmentSlot.HELMET;
            default:
                return null;
        }
    }
}
