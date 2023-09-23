package net.arcadiasedge.riftseeker.manufacturers;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import net.arcadiasedge.riftseeker.RiftseekerPlugin;
import net.arcadiasedge.riftseeker.api.ApiItem;
import net.arcadiasedge.riftseeker.data.RiftseekerDataTypes;
import net.arcadiasedge.riftseeker.entities.players.GamePlayer;
import net.arcadiasedge.riftseeker.items.DamageType;
import net.arcadiasedge.riftseeker.items.Item;
import net.arcadiasedge.riftseeker.items.attributes.ItemAttribute;
import net.arcadiasedge.riftseeker.items.sets.SetEffect;
import net.arcadiasedge.riftseeker.managers.ItemManager;
import net.arcadiasedge.riftseeker.managers.SetEffectManager;
import net.arcadiasedge.riftseeker.utils.ColorMap;
import net.arcadiasedge.riftseeker.utils.rarity.RarityMap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;

public class ItemManufacturer {
    /**
     * Creates a new item from the ID and the owner.
     * This method will fetch the item's data from the database, and will create a new item stack.
     * @param id The ID of the item.
     * @param holder The owner of the item.
     * @return The new item.
     */
    public static Item create(String id, GamePlayer holder) {
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

        ItemStack itemStack = new ItemStack(Objects.requireNonNull(Material.matchMaterial(apiItem.material)));
        ItemMeta meta = itemStack.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();

        item.itemStack = itemStack;
        item.setApiItem(apiItem);

        if (item.baseItem.getMaxStack() == 1) {
            // Set a UUID for the item, as there can only be one of this item in a stack.
            container.set(RiftseekerPlugin.getInstance().getKey("item-uuid"), RiftseekerDataTypes.UUID, item.uuid);
            itemStack.setItemMeta(meta);
        }

        constructNbtData(item, holder);
        return item;
    }

    /**
     * Fetches an already existing item from the item stack. If this item is not a Riftseeker item, then this method will return null.
     * @param item The item stack to fetch the item from.
     * @param holder The owner of the item.
     * @return The item, or null if the item is not a Riftseeker item.
     */
    public static Item from(ItemStack item, GamePlayer holder) throws IOException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
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

        // Reset display data, and re-construct it.
        // This is done to ensure that the display reflects on the holder's resource pack settings.
        NBT.modify(item, nbt -> {
            nbt.removeKey("display");

            // Get keys while we're here.
            itemInstance.uuid = UUID.fromString(nbt.getCompound("riftseeker").getString("uuid"));
            constructLore(itemInstance, nbt, holder);
        });

        return itemInstance;
    }

    public static Item grant(String id, GamePlayer holder, GamePlayer sender) {
        Item item = create(id, holder);

        if (item == null) {
            return null;
        }

        NBT.modify(item.getItemStack(), nbtItem -> {
            var riftseekerCompound = nbtItem.getOrCreateCompound("riftseeker");

            riftseekerCompound.setString("sender", GsonComponentSerializer.gson().serialize(
                    MiniMessage.miniMessage().deserialize(
                            String.format(
                                    "<color:%s>[%s] %s",
                                    sender.getProfile().getApiProfile().player.group.color.toLowerCase(),
                                    sender.getProfile().getApiProfile().player.group.prefix,
                                    sender.getEntity().getName()
                            )
                    )
            ));

            riftseekerCompound.setString("receiver", GsonComponentSerializer.gson().serialize(
                    MiniMessage.miniMessage().deserialize(
                            String.format(
                                    "<color:%s>[%s] %s",
                                    holder.getProfile().getApiProfile().player.group.color.toLowerCase(),
                                    sender.getProfile().getApiProfile().player.group.prefix,
                                    holder.getEntity().getName()
                            )
                    )
            ));

            nbtItem.removeKey("display");
            riftseekerCompound.setLong("timestamp", System.currentTimeMillis());
            constructLore(item, nbtItem, holder);
        });

        return item;
    }

    public static void constructNbtData(Item item, GamePlayer holder) {
        NBT.modify(item.itemStack, nbtItem -> {
            var riftseekerCompound = nbtItem.getOrCreateCompound("riftseeker");

            // Add inner item data
            riftseekerCompound.setString("id", item.id);
            riftseekerCompound.setString("type", item.baseItem.getType().getValue());
            riftseekerCompound.setString("rarity", item.baseItem.getRarity().getValue());
            riftseekerCompound.setString("uuid", item.uuid.toString());
            riftseekerCompound.setLong("dateObtained", System.currentTimeMillis());

            // Set the item's damage to 0, and set it as unbreakable
            nbtItem.setBoolean("Unbreakable", true);
            nbtItem.setInteger("Damage", 0);
            nbtItem.setByte("HideFlags", (byte) 255);

            if (item.enchantments.size() > 0) {
                var enchantments = riftseekerCompound.getOrCreateCompound("enchantments");
                enchantments.clearNBT();

                item.enchantments.forEach((enchantment) -> {
                    enchantments.setInteger(enchantment.getId(), enchantment.getLevel());
                });

                // Add the protection enchantment to make it shimmer
                var actualEnchantments = nbtItem.getCompoundList("Enchantments");
                var ench = actualEnchantments.addCompound();

                ench.setString("id", "minecraft:protection");
                ench.setInteger("lvl", 1);
            }

            constructLore(item, nbtItem, holder);
        });
    }

    public static void constructLore(Item item, ReadWriteNBT nbtItem, GamePlayer holder) {
        var minimessage = MiniMessage.miniMessage();
        var riftseekerCompound = nbtItem.getCompound("riftseeker");
        var display = nbtItem.getOrCreateCompound("display");
        var rarity = RarityMap.fromRarity(item.baseItem.getRarity());
        Function<Component, String> serialize = (c) -> GsonComponentSerializer.gson().serialize(c.decoration(TextDecoration.ITALIC, false));

        // Reset display data
        display.removeKey("Lore");

        // Name
        {
            Component nameComponent = minimessage.deserialize(rarity.getTag() + item.baseItem.name);
            display.setString("Name", serialize.apply(nameComponent));
        }

        // Lore
        {
            var componentMap = new ArrayList<String>();

            // Item Kind & Rarity
            Component rarityComponent = minimessage.deserialize("<bold>" + rarity.getColorTag(item.baseItem.kind));
            componentMap.add(serialize.apply(rarityComponent));
            componentMap.add(serialize.apply(Component.text(" ")));

            // Enchantments
            if (item.enchantments.size() > 0) {
                // the real way to do it:
                var lines = new ArrayList<String>();
                var line = Component.text("");
                var len = 0;

                for (var enchant : item.enchantments) {
                    if (len == 3) {
                        lines.add(serialize.apply(line));
                        line = Component.text("");
                        len = 0;
                    }

                    len++;
                    if (item.enchantments.get(item.enchantments.size() - 1) == enchant) {
                        line = enchant.getLevel() == enchant.getMaxLevel()
                                ? line.append(MiniMessage.miniMessage().deserialize("<gold>" + enchant.getDisplayName()))
                                : line.append(MiniMessage.miniMessage().deserialize("<gray>" + enchant.getDisplayName()));

                        lines.add(serialize.apply(line));
                        break;
                    } else {
                        line = enchant.getLevel() == enchant.getMaxLevel()
                                ? line.append(MiniMessage.miniMessage().deserialize("<gold>" + enchant.getDisplayName() + ", "))
                                : line.append(MiniMessage.miniMessage().deserialize("<gray>" + enchant.getDisplayName() + ", "));
                    }
                }

                componentMap.addAll(lines);
                componentMap.add(serialize.apply(Component.text(" ")));
            }

            // Description
            if (item.baseItem.lore != null && item.baseItem.lore != "") {
                var lines = wrapText(item.baseItem.lore).split("<br>");
                for (var line : lines) {
                    componentMap.add(serialize.apply(minimessage.deserialize("<gray>" + line)));
                }

                componentMap.add(serialize.apply(Component.text(" ")));
            }

            // Abilities
            for (var ability : item.abilities) {
                componentMap.add(serialize.apply(minimessage.deserialize("<gold>Item Ability: " + ability.baseAbility.name + " <gray>- <white>" + ability.baseAbility.button + " CLICK")));

                var lines = wrapText(ability.baseAbility.lore).split("<br>");
                for (var line : lines) {
                    componentMap.add(serialize.apply(minimessage.deserialize("<color:"+ ColorMap.GRAY_1+">" + line,
                            Placeholder.parsed("damage", "<aqua>" + String.format("%,.0f", ability.calculateBaseDamage(holder)) + "<color:"+ColorMap.GRAY_1+">")
                    )));
                }

                componentMap.add(serialize.apply(minimessage.deserialize("<color:"+ColorMap.DARK_GRAY_1+">Costs <aqua>" + String.format("%,.0f", ability.baseAbility.cost) + "<color:"+ColorMap.DARK_GRAY_1+"> mana.")));
                componentMap.add(serialize.apply(Component.text(" ")));
            }

            // Set Effects
            if (item.baseItem.setEffect != null) {
                var count = 0;
                var maxCount = 0;

                if (nbtItem.hasTag("SetCount")) {
                    count = nbtItem.getInteger("SetCount");
                }

                // get set effect
                SetEffectManager sem = RiftseekerPlugin.getInstance().getManager("sets");

                 try {
                     SetEffect setEffect = sem.create(item.baseItem.setEffect.id);;

                     maxCount = setEffect.getRequiredPieces().size();
                 } catch (Exception e) {
                     e.printStackTrace();
                 }

                componentMap.add(serialize.apply(minimessage.deserialize("<gold>Set Effect: " + item.baseItem.setEffect.name + " <gray>- <white>" + count + "/" + maxCount)));
                componentMap.add(serialize.apply(minimessage.deserialize("<gray>" + item.baseItem.setEffect.lore + "</gray>")));
                componentMap.add(serialize.apply(Component.text(" ")));
            }

            if (riftseekerCompound.hasTag("sender")) {
                Date date = new Date(riftseekerCompound.getLong("timestamp"));
                // Day Month, Year
                String dateString = new SimpleDateFormat("dd MMMM yyyy").format(date);
                componentMap.add(serialize.apply(minimessage.deserialize("<gray>To: ").append(GsonComponentSerializer.gson().deserialize(riftseekerCompound.getString("receiver")))));
                componentMap.add(serialize.apply(minimessage.deserialize("<gray>From: ").append(GsonComponentSerializer.gson().deserialize(riftseekerCompound.getString("sender")))));
                componentMap.add(serialize.apply(minimessage.deserialize("<color:"+ColorMap.DARK_GRAY_1+">This item was gifted on " + dateString)));
                componentMap.add(serialize.apply(Component.text(" ")));
            }

            // Attributes
            if (item.abilities.size() > 0) {
                componentMap.add(serialize.apply(minimessage.deserialize("<dark_gray>Attributes")));
                for (Map.Entry<String, ItemAttribute> entry : item.attributes.entrySet()) {
                    if (entry.getKey() == "damage" && item.baseItem.properties.getDamageType() == DamageType.TRUE) {
                        // True damage is an attribute of its own.
                        continue;
                    }

                    var attribute = entry.getValue();
                    var attributeSplit = entry.getKey().split("_");

                    for (var i = 0; i < attributeSplit.length; i++) {
                        attributeSplit[i] = attributeSplit[i].substring(0, 1).toUpperCase() + attributeSplit[i].substring(1);
                    }

                    var uppercase = String.join(" ", attributeSplit);
                    String valueString;

                    if (Objects.equals(entry.getKey(), "crit_chance")) {
                        uppercase = "Crit Chance";
                        valueString = String.format("%,.0f", attribute.value * 100) + "%";
                    } else if (Objects.equals(entry.getKey(), "crit_damage")) {
                        uppercase = "Crit Damage";
                        valueString = String.format("%,.0f", attribute.value * 100) + "%";
                    } else {
                        valueString = String.format("%,.0f", attribute.value);
                    }

                    if (attribute.value > 0) {
                        valueString = "+" + valueString;
                    }

                    if (Objects.equals(entry.getKey(), "damage")) {
                        valueString += " <color:" + ColorMap.DARK_GRAY_1 + ">(" + item.baseItem.properties.getDamageType().toString().toLowerCase() + ")";
                    }

                    componentMap.add(serialize.apply(minimessage.deserialize("<gray>" + uppercase + ": <blue>" + valueString)));
                }
            }

            var lore = display.getStringList("Lore");

            for (var component : componentMap) {
                lore.add(component);
            }
        }
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
