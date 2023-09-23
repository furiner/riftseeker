package net.arcadiasedge.riftseeker.items;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import net.arcadiasedge.riftseeker.RiftseekerPlugin;
import net.arcadiasedge.riftseeker.abilities.Ability;
import net.arcadiasedge.riftseeker.api.ApiItem;
import net.arcadiasedge.riftseeker.entities.players.GamePlayer;
import net.arcadiasedge.riftseeker.entities.projectiles.ArrowEntity;
import net.arcadiasedge.riftseeker.items.attributes.ItemAttribute;
import net.arcadiasedge.riftseeker.items.enchantments.Enchantment;
import net.arcadiasedge.riftseeker.managers.AbilityManager;
import net.arcadiasedge.riftseeker.utils.rarity.RarityMap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * An item that is represented in Riftseeker.
 * This class is used to represent both items that are stored in the database, and items that are in the player's inventory.
 * Attributes and abilities are stored in this class, and are used to modify the item's stats and behavior.
 */
public class Item {
    /**
     * The ID of the item. Commonly used to identify the item in the database.
     */
    public String id;

    /**
     * The cached API item that this item is based on.
     * Attributes such as the item's name, material, lore, and others are stored here.
     */
    public ApiItem baseItem;

    /**
     * The item stack that this item belongs to.
     */
    public ItemStack itemStack;

    /**
     * A map of the item's attributes.
     */
    public Map<String, ItemAttribute> attributes;

    public ItemStatistics statistics;

    /**
     * A list of the item's abilities.
     */
    public List<Ability> abilities;

    /**
     * A list of the item's enchantments.
     */
    public List<Enchantment> enchantments;

    /**
     * The special identifier of the item. Primarily, this is used to seperate item,
     * and flesh out clones/duplicates of the same item.
     */
    public UUID uuid;

    public Item() {
        this("UNKNOWN");
    }

    public Item(String id) {
        this.id = id;
        this.attributes = new HashMap<>();
        this.statistics = new ItemStatistics();
        this.abilities = new ArrayList<>();
        this.enchantments = new ArrayList<>();
        this.uuid = UUID.randomUUID();
    }

    public void onUse(GamePlayer user) {
        return;
    }

    public void onConsumed(GamePlayer user) {
        return;
    }

    // TODO: Generalize this and make GameProjectile instead; as snowballs are a thing.
    public void onArrowShoot(ArrowEntity arrow) {
        return;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    /**
     * Assigns the physical item that this item belongs to.
     * @param itemStack The item stack that this item belongs to.
     */
    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public String getId() {
        return id == null ? this.baseItem.id : id;
    }

    public UUID getUuid() {
        return uuid;
    }

    /**
     * Assigns the database item that this item is based on. This method will also assign the item's attributes and abilities.
     * @param apiItem The database item that this item is based on.
     */
    public void setApiItem(ApiItem apiItem) {
        this.baseItem = apiItem;

        // Assign the item's attributes
        for (var entry : apiItem.attributes.entrySet()) {
            this.attributes.put(entry.getKey(), new ItemAttribute(entry.getKey(), entry.getValue()));
            this.statistics.setAttribute(new ItemAttribute(entry.getKey(), entry.getValue()));
        }

        // Assign the item's abilities
        for (var ability : apiItem.abilities) {
            AbilityManager manager = RiftseekerPlugin.getInstance().getManager("abilities");
            try {
                this.abilities.add(manager.create(ability.name.replace(" ", ""), this, ability));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void addEnchantment(Enchantment enchantment) {
        this.enchantments.add(enchantment);

        enchantment.setItem(this);
    }

    public void removeEnchantment(Enchantment enchantment) {
        this.enchantments.remove(enchantment);
    }

    public List<Ability> getAbilities() {
        return abilities;
    }

    public Component getDisplayName() {
        var rarity = RarityMap.fromRarity(this.baseItem.getRarity());
        Component nameComponent = MiniMessage.miniMessage().deserialize(rarity.getTag() + this.baseItem.name);

        return nameComponent.decoration(TextDecoration.ITALIC, false);
    }

    public List<Component> setExtraLore(ReadWriteNBT nbt) {
        return new ArrayList<>();
    }

    /**
     * Serializes the item's NBT data.
     * @return The serialized NBT data.
     */
    public String serialize() {
        return NBT.itemStackToNBT(itemStack).toString();
    }

    public boolean equals(Item item) {
        return item.id.equals(this.id) && item.uuid.equals(this.uuid);
    }
    public boolean equals(ItemStack item) {
        return NBT.get(item, nbt -> {
            if (nbt.hasTag("riftseeker")) {
                var tag = nbt.getCompound("riftseeker");

                if (tag.hasTag("id") && tag.hasTag("uuid")) {
                    return tag.getString("id").equals(this.id) && tag.getString("uuid").equals(this.uuid.toString());
                } else {
                    return false;
                }
            } else {
                return false;
            }
        });
    }

}
