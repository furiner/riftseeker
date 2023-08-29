package net.arcadiasedge.riftseeker.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.google.gson.Gson;
import de.tr7zw.nbtapi.NBT;
import net.arcadiasedge.riftseeker.api.ApiModel;
import net.arcadiasedge.riftseeker.api.requests.profiles.UpdatePlayerProfileDataRequest;
import net.arcadiasedge.riftseeker.items.Item;
import net.arcadiasedge.riftseeker.entities.players.GamePlayer;
import net.arcadiasedge.riftseeker.utils.rarity.RarityMap;
import net.arcadiasedge.riftseeker.utils.GenericMessages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

public class PlayerInventoryListener implements Listener {
    @EventHandler
    public void onSwapItem(PlayerSwapHandItemsEvent event) {
        Player p = event.getPlayer();

        p.sendMessage(GenericMessages.NO_OFFHAND);
        event.setCancelled(true);
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
        GamePlayer player = GamePlayer.get(p);

        // Check if the itemstack is in the player's inventory
        if (player != null) {
            var item = event.getItemDrop().getItemStack();
            var itemInventory = player.getInventory();

            if (itemInventory.contains(item)) {
                var riftItem = player.getInventory().get(item);
                var itemEntity = event.getItemDrop();

                // Assign data to prevent other players from picking up the item
                // Also, retain the riftseeker data on the item.
                NBT.modify(itemEntity, nbt -> {
                    // Setting it on the item entity prevents it from being cleared by Minecraft.
                    nbt.getCompound("Item").getCompound("tag").setUUID("ItemOwner", p.getUniqueId());
                });
                // Color the item entity to match the item rarity
                var itemRarity = Item.colorForRarity(riftItem.baseItem.getRarity());
                var itemName = Component.text(riftItem.baseItem.name).color(itemRarity);
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
        GamePlayer player = GamePlayer.get(p);
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

                var riftItem = Item.from(stack);

                // Add the item to the player's inventory
                player.getInventory().add(riftItem);
                event.setCancelled(true);
                item.remove();
            } else {
                // Try to convert the item to a riftseeker item,
                // as it is likely a vanilla item.
                var material = itemStack.getType();
                Item riftItem = Item.create(material.toString());

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

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) throws IOException {
        System.out.println("Inventory closed");
        // Save the player's inventory to the database
        if (!(event.getInventory().getHolder() instanceof Player)) {
            return;
        }

        var player = GamePlayer.get((Player) event.getPlayer());
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
    }
}
