package net.arcadiasedge.riftseeker.items;

public enum Rarity {
    COMMON("COMMON"),
    UNCOMMON("UNCOMMON"),
    RARE("RARE"),
    EPIC("EPIC"),
    LEGENDARY("LEGENDARY"),
    DIVINE("DIVINE"), // Endgame items, obtainable by players but very hard to craft, or very rare drops.

    RELIC("RELIC"), // Special items only obtaiinable once, or very rarely, from events. Has equivalent stats to Divine items.
    ASCENDANT("ASCENDANT"), // High prestige items only obtainable/given out by staff. Has equivalent stats to Divine items.
    ETERNAL("ETERNAL"); // Items that are only obtainable by staff, and have stats that are not obtainable by players.

    private final String rarity;

    Rarity(String rarity) {
        this.rarity = rarity;
    }

    public String getValue() {
        return rarity;
    }

    public static Rarity fromString(String rarity) {
        for (Rarity itemRarity : Rarity.values()) {
            if (itemRarity.getValue().equals(rarity)) {
                return itemRarity;
            }
        }

        return null;
    }
}
