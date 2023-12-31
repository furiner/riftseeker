package net.arcadiasedge.riftseeker.utils.rarity;

import net.arcadiasedge.riftseeker.items.Rarity;
import javax.annotation.Nullable;

/**
 * A class that holds a {@link Rarity} and the associated color tag.
 */
public class ItemRarity {
    public Rarity rarity;
    public String color;

    public ItemRarity(Rarity rarity, String color) {
        this.rarity = rarity;
        this.color = color;
    }

    public String getTag() {
        return "<color:" + color + ">";
    }

    public String getColorTag(@Nullable String value) {
        return "<color:" + color + ">" + rarity.getValue() + (value != null ? (" " + value) : "") + "</color>";
    }
}
