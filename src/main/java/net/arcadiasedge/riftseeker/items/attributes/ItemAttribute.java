package net.arcadiasedge.riftseeker.items.attributes;

import java.util.Set;

/**
 * A representation of an attribute of an item, such as a sword's damage or a
 * piece of armor's defense.
 */
public class ItemAttribute {
    /**
     * The name of the attribute.
     */
    public String name;

    /**
     * The value of the attribute.
     */
    public int value;

    /**
     * The boosts that this attribute has.
     */
    public Set<ItemAttributeBoost> boosts;

    /**
     * The name of the attribute.
     */
    public String getName() {
        return this.name;
    }

    /**
     * The value of the attribute.
     */
    public int getValue() {
        return this.value;
    }

    /**
     * The name of the attribute.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * The value of the attribute.
     */
    public void setValue(int value) {
        this.value = value;
    }

    /**
     * The boosts that this attribute has.
     */
    public Set<ItemAttributeBoost> getBoosts() {
        return this.boosts;
    }

    /**
     * The boosts that this attribute has.
     */
    public void setBoosts(Set<ItemAttributeBoost> boosts) {
        this.boosts = boosts;
    }

    public void addBoost(ItemAttributeBoost boost) {
        this.boosts.add(boost);
    }

    /**
     * Returns a string representation of the attribute.
     */
    @Override
    public String toString() {
        return "ItemAttribute(name=" + this.getName() + ", value=" + this.getValue() + ")";
    }

    /**
     * Returns a hashcode for the attribute.
     */
    @Override
    public int hashCode() {
        int result = 1;
        result = result * 59 + (this.getName() == null ? 43 : this.getName().hashCode());
        result = result * 59 + this.getValue();
        return result;
    }

    /**
     * Checks for equality between this attribute and another object.
     */
    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof ItemAttribute)) return false;
        final ItemAttribute other = (ItemAttribute) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$name = this.getName();
        final Object other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
        if (this.getValue() != other.getValue()) return false;
        return true;
    }

    /**
     * Checks for equality between this attribute and another object.
     */
    protected boolean canEqual(Object other) {
        return other instanceof ItemAttribute;
    }
}
