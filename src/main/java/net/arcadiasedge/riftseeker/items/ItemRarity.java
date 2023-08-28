package net.arcadiasedge.riftseeker.items;

import com.google.gson.annotations.SerializedName;

public enum ItemRarity {
    COMMON("COMMON"),
    UNCOMMON("UNCOMMON"),
    RARE("RARE"),
    EPIC("EPIC"),
    LEGENDARY("LEGENDARY"),
    DIVINE("DIVINE"),
    ENIGMA("ENIGMA"),
    ASCENDANT("ASCENDANT"),
    PRIMEVAL("PRIMEVAL");

    private final String rarity;

    ItemRarity(String rarity) {
        this.rarity = rarity;
    }

    public String getName() {
        return rarity;
    }

    public static ItemRarity fromString(String rarity) {
        for (ItemRarity itemRarity : ItemRarity.values()) {
            if (itemRarity.getName().equals(rarity)) {
                return itemRarity;
            }
        }

        return null;
    }
}
