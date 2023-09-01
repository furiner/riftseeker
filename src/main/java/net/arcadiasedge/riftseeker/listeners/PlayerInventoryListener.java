package net.arcadiasedge.riftseeker.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import de.tr7zw.nbtapi.NBT;
import net.arcadiasedge.riftseeker.RiftseekerPlugin;
import net.arcadiasedge.riftseeker.abilities.Ability;
import net.arcadiasedge.riftseeker.abilities.ApplyType;
import net.arcadiasedge.riftseeker.entities.GameEntity;
import net.arcadiasedge.riftseeker.entities.players.GamePlayer;
import net.arcadiasedge.riftseeker.events.PlayerEquipArmorEvent;
import net.arcadiasedge.riftseeker.items.GameEquipmentSlot;
import net.arcadiasedge.riftseeker.items.Item;
import net.arcadiasedge.riftseeker.items.ItemType;
import net.arcadiasedge.riftseeker.utils.GenericMessages;
import net.arcadiasedge.riftseeker.utils.rarity.RarityMap;
import net.arcadiasedge.riftseeker.world.GameWorld;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class PlayerInventoryListener implements Listener {
    public static final ConcurrentMap<UUID, ItemStack[]> savedContents = new ConcurrentHashMap<>();
    public static final String[] armorEndings = new String[] {"_HELMET", "_CHESTPLATE", "_LEGGINGS", "_BOOTS"};

    public PlayerInventoryListener() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            savedContents.put(p.getUniqueId(), p.getInventory().getContents());
        }
    }

    @EventHandler
    public void onSwapItem(PlayerSwapHandItemsEvent event) {
        Player p = event.getPlayer();

        p.sendMessage(GenericMessages.NO_OFFHAND);
        event.setCancelled(true);
    }

    @EventHandler
    public void onSwapItem(PlayerItemHeldEvent event) {
        var p = event.getPlayer();
        var player = GameWorld.getInstance().getPlayer(p);
        var oldSlot = p.getInventory().getItem(event.getPreviousSlot());
        var newSlot = p.getInventory().getItem(event.getNewSlot());

        var inventory = player.getInventory();
        var oldItem = inventory.get(oldSlot);
        var newItem = inventory.get(newSlot);

        player.onSwapItem(oldItem, newItem);
    }

    public void onEquipArmor(PlayerEquipArmorEvent event) {
        System.out.println("Equipping armor");
        System.out.println(event.getEquipmentSlot());
    }

    @EventHandler
    public void onPlayerItemCLick(InventoryClickEvent event) {
        if (event.getSlotType() == InventoryType.SlotType.QUICKBAR && event.getSlot() == 9) {
            Player p = (Player) event.getWhoClicked();

            p.sendMessage(GenericMessages.NO_OFFHAND);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player p = event.getPlayer();
        GamePlayer player = GameWorld.getInstance().getPlayer(p);
        var minimessage = MiniMessage.miniMessage();

        System.out.println("Dropping item");

        // Check if the itemstack is in the player's inventory
        if (player != null) {
            var item = event.getItemDrop().getItemStack();
            var itemInventory = player.getInventory();

            if (itemInventory.get(item) != null) {
                var riftItem = player.getInventory().get(item);
                var itemEntity = event.getItemDrop();

                // Assign data to prevent other players from picking up the item
                // Also, retain the riftseeker data on the item.
                NBT.modify(itemEntity, nbt -> {
                    // Setting it on the item entity prevents it from being cleared by Minecraft.
                    nbt.getCompound("Item").getCompound("tag").setUUID("ItemOwner", p.getUniqueId());
                });

                // Color the item entity to match the item rarity
                Component itemName = minimessage.deserialize(RarityMap.fromRarity(riftItem.baseItem.getRarity()).getColorTag(riftItem.baseItem.name));

                var scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
                var container = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
                var team = scoreboard.registerNewTeam("riftseeker-" + itemEntity.getEntityId());

                //team.setColor(itemRarity.);
                team.addEntry(itemEntity.getUniqueId().toString());

                container.getIntegers().write(0, itemEntity.getEntityId());

                WrappedDataWatcher watcher = new WrappedDataWatcher();
                watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(0, WrappedDataWatcher.Registry.get(Byte.class)), (byte) 0x40);
                watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(2, WrappedDataWatcher.Registry.getChatComponentSerializer(true)),
                        Optional.of(
                                WrappedChatComponent.fromJson(
                                        GsonComponentSerializer.gson().serialize(itemName)
                                ).getHandle()
                        ));
                watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(3, WrappedDataWatcher.Registry.get(Boolean.class)), true);

                container.getWatchableCollectionModifier().write(0, watcher.getWatchableObjects());

                // Send the packet to the player
                ProtocolLibrary.getProtocolManager().sendServerPacket(p, container);

                // Remove the item from the player's inventory
                itemInventory.remove(item);
            } else {
                // Cancel the event
                p.sendMessage(MiniMessage.miniMessage().deserialize("<red>Sorry, you can't drop this item."));
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerPickupItem(EntityPickupItemEvent event) throws IOException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player p = (Player) event.getEntity();
        GamePlayer player = GameWorld.getInstance().getPlayer(p);
        var itemStack = event.getItem().getItemStack();

        // Check if the itemstack is a riftseeker item
        if (player != null) {
            var item = event.getItem();
            var entityItemTag = NBT.get(item, nbt -> nbt.getCompound("Item").getCompound("tag"));

            if (entityItemTag != null && entityItemTag.hasTag("riftseeker")) {
                // Check if the item is owned by the player
                var nbtEntityOwner = entityItemTag.getUUID("ItemOwner");
                System.out.println(nbtEntityOwner);

                if (nbtEntityOwner != null && !nbtEntityOwner.equals(p.getUniqueId())) {
                    // Cancel the event
                    event.setCancelled(true);
                    return;
                }

                // Create a new item from the itemstack, then reapply the data
                var stack = new ItemStack(itemStack.getType());

                // Reapply the data
                NBT.modify(stack, nbt -> {
                    nbt.mergeCompound(entityItemTag);
                });

                var riftItem = Item.from(stack, player);

                // Add the item to the player's inventory
                player.getInventory().add(riftItem);
                event.setCancelled(true);
                item.remove();
            } else {
                // Try to convert the item to a riftseeker item,
                // as it is likely a vanilla item.
                var material = itemStack.getType();
                Item riftItem = Item.create(material.toString(), player);

                if (riftItem != null) {
                    // Add the item to the player's inventory
                    player.getInventory().add(riftItem);
                    item.remove();
                }
            }
        }

        // Cancel the event
        event.setCancelled(true);
    }

    /*
    fix later
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) throws IOException {
        System.out.println("Inventory closed");
        // Save the player's inventory to the database
        if (!(event.getInventory().getHolder() instanceof Player)) {
            return;
        }

        var player = GameWorld.getInstance().getPlayer((Player) event.getPlayer());
        var inventory = event.getPlayer().getInventory();
        System.out.println(inventory.getContents());

        if (player != null) {
            System.out.println("Saving inventory");
            // Serialize the inventory
            var profile = player.getProfile();
            var itemData = new ArrayList<String>();
            var itemInventory = profile.inventory.get("items");

            Arrays.stream(inventory.getStorageContents()).forEach(item -> {
                if (item == null || item.getType() == Material.AIR) {
                    return;
                }
                var nbtItem = NBT.itemStackToNBT(item);
                var slot = inventory.first(item);


                // Set the slot number if riftseeker data is present
                if (nbtItem.hasTag("riftseeker")) {
                    nbtItem.setInteger("slot", slot);
                }

                if (item != null) {
                    itemData.add(nbtItem.toString());
                }
            });

            // Serialize items
            for (int i = 0; i < inventory.getStorageContents().length; i++) {
                var item = event.getInventory().getItem(i);

            }

            // Assign data
            itemInventory.data = new Gson().toJson(itemData);

            ApiModel.send(new UpdatePlayerProfileDataRequest(player.getProfile()));
        }
    }*/

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }


        var player = GameWorld.getInstance().getPlayer((Player) event.getWhoClicked());
        var clickedInventory = event.getClickedInventory();

        if (player != null && clickedInventory != null) {
            if ((clickedInventory.getType() == InventoryType.CRAFTING || clickedInventory.getType() == InventoryType.PLAYER)) {
                if (event.getSlotType() == InventoryType.SlotType.ARMOR || event.isShiftClick()) {
                    this.checkArmor((Player) event.getWhoClicked());
                }

                if (event.isShiftClick()) {
                    // Handle shift click logic similarly to vanilla minecraft, with item merging
                    var item = event.getCurrentItem();

                    if (item == null) {
                        return;
                    }

                    if (item.getType() == Material.AIR) {
                        return;
                    }

                    if (item.getAmount() <= 0) {
                        return;
                    }

                    var itemSlot = clickedInventory.first(item);
                    var gameItem = player.getInventory().get(item);

                    if (gameItem == null) {
                        return;
                    }

                    // Iterate through the player's inventory and readjust items
                    for (ListIterator<ItemStack> it = clickedInventory.iterator(); it.hasNext(); ) {
                        var inventoryItem = it.next();

                        if (inventoryItem == null) {
                            continue;
                        }

                        if (inventoryItem.getType() == Material.AIR) {
                            continue;
                        }

                        if (inventoryItem.getAmount() <= 0) {
                            continue;
                        }

                        var inventoryGameItem = player.getInventory().get(inventoryItem);
                        var inventoryItemSlot = clickedInventory.first(inventoryItem);

                        if (inventoryGameItem == null) {
                            continue;
                        }

                        if (inventoryItemSlot == itemSlot) {
                            continue;
                        }

                        if (inventoryGameItem.getItemStack().isSimilar(gameItem.getItemStack())) {
                            var amount = inventoryItem.getAmount() + item.getAmount();
                            var maxStackSize = inventoryGameItem.getItemStack().getMaxStackSize();

                            // Check if the item is stackable
                            if (inventoryItem.getAmount() < maxStackSize) {
                                // Check if the item amount exceeds the max stack size
                                if (amount > maxStackSize) {
                                    var remainder = amount - maxStackSize;
                                    inventoryItem.setAmount(maxStackSize);
                                    item.setAmount(remainder);
                                } else {
                                    // Set the item amount and remove the item from the player's inventory
                                    inventoryItem.setAmount(amount);
                                    item.setAmount(0);
                                    player.getInventory().remove(gameItem);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler()
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            var player = GameWorld.getInstance().getPlayer(event.getPlayer());
            var item = event.getItem();

            if (item == null) {
                return;
            }

            var isArmor = false;

            for (var armorEnding : PlayerInventoryListener.armorEndings) {
                if (item.getType().toString().contains(armorEnding)) {
                    isArmor = true;
                    this.checkArmor(event.getPlayer());
                    break;
                }
            }

            if (isArmor == true) {
                if (player.getInventory().getHeld().equals(item)) {
                    // Set held item as null
                    player.getInventory().setHeld(null);
                }
                return;
            }

            var heldItem = player.getInventory().getHeld();

            // Check if the player
            if (heldItem != null) {
                // Get the item's right click ability
                Ability ability;
                for (var ab : heldItem.getAbilities()) {
                    if (ab.getTrigger() == ClickType.RIGHT) {
                        if ((ab.getType() == ApplyType.ARROW_HII || ab.getType() ==  ApplyType.ARROW_SHOOT) && heldItem.getItemStack().getType() == Material.BOW) {
                            // This will be handled individually by other events.
                            // TODO: Potentially add a bow that immediately fires, but doing so might be too similar to Hypixel Skyblock.
                            break;
                        }
                        ability = ab;
                        break;
                    }
                }
            }
        } else if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            var player = GameWorld.getInstance().getPlayer(event.getPlayer());
            var heldItem = player.getInventory().getHeld();

            // Check if the player
            if (heldItem != null) {
                if (heldItem.baseItem.getType().isRangedWeapon()) {
                    // Treat the item as a fist.
                    return;
                }

                // Get the item's left click ability
                Ability ability = null;
                for (var ab : heldItem.getAbilities()) {
                    if (ab.getTrigger() == ClickType.LEFT) {
                        ability = ab;
                        break;
                    }
                }

                if (ability != null) {
                    if (player.hasCooldown(ability)) {
                        return;
                    }

                    event.setCancelled(true);

                    // Run the ability on a separate thread
                    Ability finalAbility = ability;
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            List<GameEntity<?>> entities = new ArrayList<>();
                            entities = finalAbility.execute(player);
                            int idx = 0;

                            for (var target : entities) {
                                idx++;
                                var damage = finalAbility.onEntityHit(player, target, idx);
                                var critical = player.rollForCrit();

                                if (critical) {
                                    damage *= player.getStatistics().getStatistic("crit_damage").getFinalTotal();
                                }

                                target.damage(damage, critical);
                            }
                        }
                    }.runTaskAsynchronously(RiftseekerPlugin.getInstance());

                }
            }
        }
    }

    public void checkArmor(Player player) {
        // Have to run this 1 tick later, as the player's inventory is updated after the event is called
        new BukkitRunnable() {
            @Override
            public void run() {
                // Check if the player equipped, or unequipped any armor
                var current = player.getInventory().getArmorContents();
                var previous = savedContents.get(player.getUniqueId());

                var gamePlayer = GameWorld.getInstance().getPlayer(player);
                var playerInventory = gamePlayer.getInventory();

                System.out.println("Getting inventories");

                if (previous == null) {
                    savedContents.put(player.getUniqueId(), current);
                    return;
                }

                var slot = EquipmentSlot.values();
                var slotIndex = 5; // Reverse order

                // Check if the player equipped/unequipped any armor
                // Function: gamePlayer.onEquipArmor or gamePlayer.onUnequipArmor
                for (int i = 0; i < current.length; i++) {
                    var currentArmor = current[i];
                    var previousArmor = previous[i];

                    if (currentArmor == null && previousArmor == null) {
                        continue;
                    }

                    var equipmentSlot = GameEquipmentSlot.fromEquipmentSlot(slot[slotIndex]);
                    var previousItem = playerInventory.get(previousArmor);
                    var currentItem = playerInventory.get(currentArmor);

                    if (currentArmor == null && previousArmor != null) {
                        System.out.println("Unequipped armor");
                        gamePlayer.onUnequipArmor(previousItem, equipmentSlot);
                    } else if (currentArmor != null && previousArmor == null) {
                        // Equipped armor
                        System.out.println("Equipped armor");
                        System.out.println(currentItem);
                        gamePlayer.onEquipArmor(currentItem, equipmentSlot);
                    } else if (!currentArmor.equals(previousArmor)) {
                        // Unequipped armor
                        System.out.println("Unequipped & Reequipped new armor");
                        gamePlayer.onUnequipArmor(currentItem, equipmentSlot);

                        // Equipped armor
                        gamePlayer.onEquipArmor(currentItem, equipmentSlot);
                    }

                    slotIndex--;
                }

                // Update the saved contents
                savedContents.put(player.getUniqueId(), current);
            }
        }.runTaskLater(RiftseekerPlugin.getInstance(), 1);
    }
}
