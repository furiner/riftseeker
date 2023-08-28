package net.arcadiasedge.riftseeker.items;

import com.google.common.base.CaseFormat;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteItemNBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import net.arcadiasedge.riftseeker.RiftseekerPlugin;
import net.arcadiasedge.riftseeker.api.ApiItem;
import net.arcadiasedge.riftseeker.api.partials.ApiAbility;
import net.arcadiasedge.riftseeker.items.attributes.ItemAttribute;
import net.arcadiasedge.riftseeker.managers.ItemManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Function;

public class Item {
    public String id;

    public ApiItem baseItem;

    public ItemStack itemStack;

    public Map<String, ItemAttribute> attributes;

    public List<ApiAbility> abilities;

    public Item() {
        this("UNKNOWN");
    }

    public Item(String id) {
        this.id = id;
        this.attributes = new HashMap<>();
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public void setApiItem(ApiItem apiItem) {
        this.baseItem = apiItem;

        // Assign the item's attributes
        for (var entry : apiItem.attributes.entrySet()) {
            var attribute = new ItemAttribute();
            attribute.name = entry.getKey();
            attribute.value = entry.getValue();

            this.attributes.put(entry.getKey(), attribute);
        }
    }

    public String serialize() {
        return NBT.itemStackToNBT(itemStack).toString();
    }

    public static Item create(String id) {
        ApiItem apiItem;

        try {
            apiItem = ApiItem.fetch(id);
        } catch (IOException e) {
            return null;
        }

        if (apiItem == null) {
            return null;
        }

        ItemManager manager = RiftseekerPlugin.getInstance().getManager("items");
        var itemClass = manager.get(id);

        if (itemClass == null) {
            return null;
        }

        Item item;

        try {
            item = itemClass.getDeclaredConstructor().newInstance();
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new RuntimeException(e);
        }



        // Assign the new item stack & data
        item.setApiItem(apiItem);
        item.itemStack = new ItemStack(Material.matchMaterial(apiItem.material));

        constructNbtData(item);

        return item;
    }

    public static Item from(ItemStack item) throws IOException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        var nbtItem = NBT.get(item, nbt -> {
            if (nbt.hasTag("tag")) {
                return nbt.getCompound("tag");
            } else {
                return nbt;
            }
        });

        if (!nbtItem.hasTag("riftseeker")) {
            return null;
        }

        // Get the item's ID
        var id = nbtItem.getCompound("riftseeker").getString("id");

        // Get the item's class
        ItemManager manager = RiftseekerPlugin.getInstance().getManager("items");
        var itemClass = manager.get(id);

        Item itemInstance = itemClass.getDeclaredConstructor().newInstance();

        // Assign the item's data
        itemInstance.setItemStack(item);
        itemInstance.setApiItem(ApiItem.fetch(itemInstance.id));

        return itemInstance;
    }

    private static void constructNbtData(Item item) {
        NBT.modify(item.itemStack, nbtItem -> {
            var riftseekerCompound = nbtItem.getOrCreateCompound("riftseeker");

            // Add inner item data
            riftseekerCompound.setString("id", item.id);
            riftseekerCompound.setString("rarity", item.baseItem.getRarity().getName());
            riftseekerCompound.setString("uuid", UUID.randomUUID().toString());
            riftseekerCompound.setLong("dateObtained", System.currentTimeMillis());

            // Set the item's damage to 0, and set it as unbreakable
            nbtItem.setBoolean("Unbreakable", true);
            nbtItem.setInteger("Damage", 0);
            nbtItem.setByte("HideFlags", (byte) 255);

            constructLore(item, nbtItem);
        });
    }

    private static void constructLore(Item item, ReadWriteNBT nbtItem) {
        var display = nbtItem.getOrCreateCompound("display");
        Function<Component, String> serialize = (c) -> GsonComponentSerializer.gson().serialize(c.decoration(TextDecoration.ITALIC, false));

        // Name
        {
            var nameComponent = Component.text(item.baseItem.name)
                    .color(colorForRarity(item.baseItem.getRarity()));
            display.setString("Name", serialize.apply(nameComponent));
        }

        // Lore
        {
            var minimessage = MiniMessage.miniMessage();
            var componentMap = new ArrayList<String>();

            // Item Type & Rarity
            var rarityComponent = Component.text("")
                    .append(Component.text(item.baseItem.getRarity() + " " + item.baseItem.type)
                            .color(colorForRarity(item.baseItem.getRarity()))
                            .decoration(TextDecoration.BOLD, true));

            componentMap.add(serialize.apply(rarityComponent));
            componentMap.add(serialize.apply(Component.text(" ")));

            // TODO: Enchantments

            // Description
            if (item.baseItem.lore != null) {
                var lines = wrapText(item.baseItem.lore).split("<br>");
                for (var line : lines) {
                    componentMap.add(serialize.apply(minimessage.deserialize("<gray>" + line)));
                }

                componentMap.add(serialize.apply(Component.text(" ")));
            }

            // TODO: Abilities

            // Attributes
            componentMap.add(serialize.apply(minimessage.deserialize("<dark_gray>Attributes")));
            for (Map.Entry<String, ItemAttribute> entry : item.attributes.entrySet()) {
                var attribute = entry.getValue();
                var uppercase = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, entry.getKey());
                var valueString = attribute.value > 0 ? "+" + attribute.value : String.valueOf(attribute.value);
                componentMap.add(serialize.apply(minimessage.deserialize("<gray>" + uppercase + ": <blue>" + valueString)));
            }

            var lore = display.getStringList("Lore");

            for (var component : componentMap) {
                System.out.println(component);
                lore.add(component);
            }
        }
    }

    public static TextColor colorForRarity(ItemRarity rarity) {
        // TODO
        switch (rarity) {
            // Normal Items
            case COMMON -> {
                return TextColor.color(255, 255, 255);
            }

            case UNCOMMON -> {
                return TextColor.color(111, 247, 125);
            }

            case RARE -> {
                return TextColor.color(111, 122, 247);
            }

            case EPIC -> {
                return TextColor.color(215, 111, 247);
            }

            case LEGENDARY -> {
                return TextColor.color(247, 218, 111);
            }

            case DIVINE -> {
                return TextColor.color(111, 211, 247);
            }

            // Event/Special Items
            case ENIGMA -> {
                return TextColor.color(247, 111, 186);
            }

            case ASCENDANT -> {
                return TextColor.color(247, 111, 111);
            }

            // Admin Items
            case PRIMEVAL -> {
                return TextColor.color(185, 105, 224);
            }
        }

        return NamedTextColor.WHITE;
    }

    private static String wrapText(String original) {
        return wrapText(original, 50);
    }

    private static String wrapText(String original, int maxWidth) {
        var newString = "";
        var currentLine = new StringBuilder();

        for (var word : original.split(" ")) {
            if (currentLine.length() + word.length() > maxWidth) {
                newString += currentLine.toString() + "<br>";
                currentLine = new StringBuilder();
            }

            currentLine.append(word).append(" ");
        }

        return newString + currentLine;
    }
}
