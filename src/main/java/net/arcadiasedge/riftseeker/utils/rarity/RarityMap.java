package net.arcadiasedge.riftseeker.utils.rarity;

import net.arcadiasedge.riftseeker.items.Rarity;

public final class RarityMap {
    public static final ItemRarity ETERNAL = new ItemRarity(Rarity.ETERNAL, "#4E03FA");
    public static final ItemRarity ASCENDANT = new ItemRarity(Rarity.ASCENDANT, "#FF0A40");
    public static final ItemRarity RELIC = new ItemRarity(Rarity.RELIC, "#FC036C");
    public static final ItemRarity DIVINE = new ItemRarity(Rarity.DIVINE, "#0378FE");
    public static final ItemRarity LEGENDARY = new ItemRarity(Rarity.LEGENDARY, "#FFA600");
    public static final ItemRarity EPIC = new ItemRarity(Rarity.EPIC, "#AA00A6");
    public static final ItemRarity RARE = new ItemRarity(Rarity.RARE, "#55F9FF");
    public static final ItemRarity UNCOMMON = new ItemRarity(Rarity.UNCOMMON, "#55FF50");
    public static final ItemRarity COMMON = new ItemRarity(Rarity.COMMON, "#FAFAFA");

    public static ItemRarity fromRarity(Rarity rarity) {
        return switch (rarity) {
            default -> COMMON;
            case UNCOMMON -> UNCOMMON;
            case RARE -> RARE;
            case EPIC -> EPIC;
            case LEGENDARY -> LEGENDARY;
            case DIVINE -> DIVINE;
            case RELIC -> RELIC;
            case ASCENDANT -> ASCENDANT;
            case ETERNAL -> ETERNAL;
        };
    }
}
