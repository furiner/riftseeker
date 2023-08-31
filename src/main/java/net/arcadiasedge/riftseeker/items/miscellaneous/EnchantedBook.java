package net.arcadiasedge.riftseeker.items.miscellaneous;

import net.arcadiasedge.riftseeker.RiftseekerPlugin;
import net.arcadiasedge.riftseeker.items.Item;
import net.arcadiasedge.riftseeker.managers.EnchantmentManager;

/**
 * A book that holds an enchantment.
 */
public class EnchantedBook extends Item {
    public String enchantment;

    public EnchantedBook() {
        super();

        this.enchantment = "UNKNOWN";
    }

    public void setEnchantment(String enchantment) {
        EnchantmentManager em = RiftseekerPlugin.getInstance().getManager("enchantments");
    }
}
