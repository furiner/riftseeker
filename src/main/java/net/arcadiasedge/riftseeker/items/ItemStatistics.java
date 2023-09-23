package net.arcadiasedge.riftseeker.items;

import net.arcadiasedge.riftseeker.items.attributes.ItemAttribute;

import java.util.HashMap;
import java.util.Map;

public class ItemStatistics {
    public int enhancementLevel;

    /**
     * A map of the item's attributes.
     */
    public Map<String, ItemAttribute> attributes;

    public ItemStatistics() {
        this.enhancementLevel = 0;
        this.attributes = new HashMap<>();
    }

    public ItemStatistics(int enhancementLevel) {
        this.enhancementLevel = enhancementLevel;
    }

    public ItemStatistics(int enhancementLevel, Map<String, ItemAttribute> attributes) {
        this.enhancementLevel = enhancementLevel;
        this.attributes = attributes;
    }

    public int getEnhancementLevel() {
        return this.enhancementLevel;
    }

    public void setEnhancementLevel(int enhancementLevel) {
        this.enhancementLevel = enhancementLevel;
    }

    public Map<String, ItemAttribute> getAttributes() {
        return this.attributes;
    }

    public void setAttributes(Map<String, ItemAttribute> attributes) {
        this.attributes = attributes;
    }

    public void setAttribute(ItemAttribute attribute) {
        this.attributes.put(attribute.getName(), attribute);
    }
}
