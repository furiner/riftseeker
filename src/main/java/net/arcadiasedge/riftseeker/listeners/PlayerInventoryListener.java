package net.arcadiasedge.riftseeker.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import de.tr7zw.nbtapi.NBT;
import net.arcadiasedge.riftseeker.RiftseekerPlugin;
import net.arcadiasedge.riftseeker.abilities.Ability;
import net.arcadiasedge.riftseeker.abilities.ApplyType;
import net.arcadiasedge.riftseeker.entities.GameEntity;
import net.arcadiasedge.riftseeker.entities.players.GamePlayer;
import net.arcadiasedge.riftseeker.items.GameEquipmentSlot;
import net.arcadiasedge.riftseeker.items.Item;
import net.arcadiasedge.riftseeker.items.ItemType;
import net.arcadiasedge.riftseeker.manufacturers.ItemManufacturer;
import net.arcadiasedge.riftseeker.utils.GenericMessages;
import net.arcadiasedge.riftseeker.world.GameWorld;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

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
    public void onItemHeldEvent(PlayerItemHeldEvent event) {
        var p = event.getPlayer();
        var player = GameWorld.getInstance().getPlayer(p);
        var newSlot = p.getInventory().getItem(event.getNewSlot());

        var inventory = player.getInventory();
        var newItem = inventory.get(newSlot);

        inventory.setHeld(newItem);
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

                var scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
                var container = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
                var team = scoreboard.registerNewTeam("riftseeker-" + itemEntity.getEntityId());
                team.addEntry(itemEntity.getUniqueId().toString());

                // Assign data to prevent other players from picking up the item
                // Also, retain the riftseeker data on the item.
                NBT.modify(itemEntity, nbt -> {
                    // Setting it on the item entity prevents it from being cleared by Minecraft.
                    nbt.getCompound("Item").getCompound("tag").setUUID("ItemOwner", p.getUniqueId());

                    nbt.setString("CustomName", GsonComponentSerializer.gson().serialize(riftItem.getDisplayName()));
                    nbt.setBoolean("CustomNameVisible", true);
                    nbt.setBoolean("Glowing", true);

                });;

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

                var riftItem = ItemManufacturer.from(stack, player);

                // Add the item to the player's inventory
                player.getInventory().add(riftItem);
                event.setCancelled(true);
                item.remove();
            } else {
                // Try to convert the item to a riftseeker item,
                // as it is likely a vanilla item.
                var material = itemStack.getType();
                Item riftItem = ItemManufacturer.create(material.toString(), player);

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
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }

        var player = GameWorld.getInstance().getPlayer((Player) event.getPlayer());
        var playerInventory = player.getInventory();
        var inventory = event.getInventory();

        // Set the player's held item as null for the time being
        playerInventory.setHeld(null);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryClose(InventoryCloseEvent event) throws IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }

        // Reset the held item of the player.
        var player = GameWorld.getInstance().getPlayer((Player) event.getPlayer());
        var playerInventory = player.getInventory();
        var inventory = event.getPlayer().getInventory();

        var heldItem = inventory.getItemInMainHand();

        if (heldItem != null && heldItem.getType() != Material.AIR) {
            if (playerInventory.get(heldItem) == null) {
                var riftItem = ItemManufacturer.from(heldItem, player);

                if (riftItem != null) {
                    playerInventory.setHeld(riftItem);
                }
            } else {
                playerInventory.setHeld(playerInventory.get(heldItem));
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        var player = GameWorld.getInstance().getPlayer((Player) event.getWhoClicked());
        var clickedInventory = event.getClickedInventory();

        if (player != null && clickedInventory != null) {
            if ((clickedInventory.getType() == InventoryType.CRAFTING || clickedInventory.getType() == InventoryType.PLAYER)) {
                if (event.getSlotType() != InventoryType.SlotType.ARMOR) {
                    if (event.isShiftClick()) {
                        var gameItem = player.getInventory().get(event.getCurrentItem());

                        checkItem(event, player, gameItem);
                    }
                } else {
                    var cursorItem = event.getCursor();

                    if (cursorItem == null || cursorItem.getType() == Material.AIR) {
                        // Nothing.
                    } else {
                        var gameItem = player.getInventory().get(cursorItem);

                        checkItem(event, player, gameItem);
                    }
                }

                if (event.getSlotType() == InventoryType.SlotType.ARMOR || event.isShiftClick()) {
                    this.checkArmor((Player) event.getWhoClicked());
                }
            }
        }
    }

    private void checkItem(InventoryClickEvent event, GamePlayer player, Item gameItem) {
        if (gameItem != null) {
            System.out.println("GameItem: " + gameItem.baseItem.getType());
            System.out.println("EventSlot: " + event.getSlot());

            if (gameItem.baseItem.getType() == ItemType.HELMET && event.getSlot() == 39) {
                this.checkEquipSlot(player, gameItem, EquipmentSlot.HEAD);
            } else if (gameItem.baseItem.getType() == ItemType.CHESTPLATE && event.getSlot() == 38) {
                this.checkEquipSlot(player, gameItem, EquipmentSlot.CHEST);
            } else if (gameItem.baseItem.getType() == ItemType.LEGGINGS && event.getSlot() == 37) {
                this.checkEquipSlot(player, gameItem, EquipmentSlot.LEGS);
            } else if (gameItem.baseItem.getType() == ItemType.BOOTS && event.getSlot() == 36) {
                this.checkEquipSlot(player, gameItem, EquipmentSlot.FEET);
            }
        }
    }


    private void checkEquipSlot(GamePlayer player, Item item, EquipmentSlot slot) {
        var playerInventory = player.getInventory();
        var actualInventory = player.getEntity().getInventory();
        var gameSlot = playerInventory.getEquipmentPiece(slot);


        if (gameSlot == null) {
            actualInventory.remove(item.getItemStack());
            actualInventory.setItem(slot, item.getItemStack());
            player.getEntity().updateInventory();
        } else {
            return;
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            var player = GameWorld.getInstance().getPlayer(event.getPlayer());
            var item = event.getItem();

            if (item == null) {
                return;
            }

            var gameItem = player.getInventory().get(item);


            if (gameItem == null) {
                return;
            }

            if (gameItem.baseItem.getType().isArmor()) {

                if (gameItem.baseItem.getType() == ItemType.HELMET) {
                    this.checkEquipSlot(player, gameItem, EquipmentSlot.HEAD);
                } else if (gameItem.baseItem.getType() == ItemType.CHESTPLATE) {
                    this.checkEquipSlot(player, gameItem, EquipmentSlot.CHEST);
                } else if (gameItem.baseItem.getType() == ItemType.LEGGINGS) {
                    this.checkEquipSlot(player, gameItem, EquipmentSlot.LEGS);
                } else if (gameItem.baseItem.getType() == ItemType.BOOTS) {
                    this.checkEquipSlot(player, gameItem, EquipmentSlot.FEET);
                }

                return;
            }

            var heldItem = player.getInventory().getHeld();

            // Check if the player
            if (heldItem != null) {
                System.out.println("HeldItem: " + heldItem.baseItem.getType());
                // Get the item's right click ability
                Ability ability = null;
                for (var ab : heldItem.getAbilities()) {
                    if (ab.getTrigger() == ClickType.RIGHT) {
                        if ((ab.getType() == ApplyType.ARROW_HIT || ab.getType() ==  ApplyType.ARROW_SHOOT) && heldItem.getItemStack().getType() == Material.BOW) {
                            // This will be handled individually by other events.
                            // TODO: Potentially add a bow that immediately fires, but doing so might be too similar to Hypixel Skyblock.
                            break;
                        }
                        ability = ab;
                        break;
                    }
                }

                System.out.println("nya");
                if (ability != null) {
                    System.out.println("Ability: " + ability.getName());
                    if (player.hasCooldown(ability)) {
                        return;
                    }


                    event.setCancelled(true);
                    runAbility(ability, player);
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
                    runAbility(ability, player);
                }
            }
        }
    }

    private void runAbility(Ability finalAbility, GamePlayer player) {
        if (finalAbility.getCooldown() > 0) {
            player.addCooldown(finalAbility, finalAbility.getCooldown());
        }

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

            if (damage == 0) {
                return;
            }

            target.damage(player, damage, critical);
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
