package net.arcadiasedge.riftseeker.items.attributes;

import net.arcadiasedge.riftseeker.statistics.StatisticBoost;
import net.arcadiasedge.riftseeker.statistics.BoostType;

import java.util.HashSet;
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
    public float value;

    public ItemAttribute() {
    }

    /**
     * The name of the attribute.
     */
    public String getName() {
        return this.name;
    }

    /**
     * The value of the attribute.
     */
    public float getValue() {
        return this.value;
    }

    public float getFinalValue() {
        float finalValue = this.value;

        return finalValue;
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
    public void setValue(float value) {
        this.value = value;
    }

    /**
     * Returns a string representation of the attribute.
     */
    @Override
    public String toString() {
        return "ItemAttribute(name=" + this.getName() + ", value=" + this.getValue() + ")";
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
