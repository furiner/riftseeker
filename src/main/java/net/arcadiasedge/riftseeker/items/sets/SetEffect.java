package net.arcadiasedge.riftseeker.items.sets;

import de.tr7zw.nbtapi.NBT;
import net.arcadiasedge.riftseeker.entities.players.GamePlayer;
import net.arcadiasedge.riftseeker.items.GameEquipmentSlot;
import net.arcadiasedge.riftseeker.manufacturers.ItemManufacturer;
import net.arcadiasedge.riftseeker.statistics.StatisticBoost;

import java.util.*;

/**
 * This is a representation for a set effect.
 *
 * It is a partial representation of the set effect as it is stored in the database;
 * and is retroactively applied to the item when it is equipped.
 */
public abstract class SetEffect {
    /**
     * The ID of the set effect.
     */
    public String id;

    /**
     * The name of the set effect.
     */
    public String name;

    /**
     * The lore of the set effect.
     */
    public String lore;

    /**
     * The required pieces for the set effect in order to be applied.
     */
    public Map<GameEquipmentSlot, String> requiredPieces;

    /**
     * Whether the player must have all the required pieces equipped in order to apply the set effect.
     */
    public boolean requiresAllPieces;

    private StatisticBoost[] statisticBoostsPerPiece;

    public SetEffect() {
        this("UNKNOWN");
    }

    public SetEffect(String id) {
        this(id, "Set Effect", "This is a set effect.");
    }

    public SetEffect(String id, String name, String lore) {
        this.id = id;
        this.name = name;
        this.lore = lore;

        this.requiredPieces = new HashMap<>();
        this.requiresAllPieces = true;
    }

    /**
     * Adds a required piece to the set effect.
     * @param slot The slot the piece must be equipped in.
     * @param itemId The ID of the item that must be equipped in the slot.
     */
    public void addRequiredPiece(GameEquipmentSlot slot, String itemId) {
        this.requiredPieces.put(slot, itemId);
    }

    /**
     * Sets the required pieces for the set effect.
     * @param requiredPieces A map of the required pieces.
     */
    public void setRequiredPieces(Map<GameEquipmentSlot, String> requiredPieces) {
        this.requiredPieces = requiredPieces;
        this.statisticBoostsPerPiece = new StatisticBoost[this.requiredPieces.size()];
    }

    /**
     * Gets the required pieces for the set effect.
     * @return A map of the required pieces. The key is the slot the piece must be equipped in, and the value is the ID of the item that must be equipped in the slot.
     */
    public Map<GameEquipmentSlot, String> getRequiredPieces() {
        return requiredPieces;
    }

    public int getAppliedPieces(GamePlayer player) {
        var inventory = player.getInventory();
        var count = 0;

        for (var piece : this.requiredPieces.entrySet()) {
            if (inventory.getEquipmentPiece(piece.getKey()) != null && inventory.getEquipmentPiece(piece.getKey()).getId() == piece.getValue()) {
                count++;
            }
        }

        applyLore(player, count);
        return count;
    }

    public void onApply(GamePlayer player, int count) {
        return;
    }

    public void onRemove(GamePlayer player) {
        return;
    }

    /**
     * Applies the set effect to the player.
     * This method will retroactively apply the set effect to the player based off how many pieces of the set they have equipped.
     * If the player does not have any pieces of the set equipped,
     * or if the player does not have all the required pieces equipped and the set effect requires all pieces to be equipped,
     * then the set effect will not be applied.
     * @param player The player to apply the set effect to.
     * @return The number of pieces of the set the player has equipped.
     */
    public List<StatisticBoost> getBoostsToApply(GamePlayer player) {
        var count = this.getAppliedPieces(player);

        if (this.requiresAllPieces && count != this.requiredPieces.size()) {
            return null;
        }

        return this.getStatisticBoosts(player, count);
    }

    /**
     * Gets a list of statistic boosts for the set effect.
     * This method should be overridden by its subclasses, and should return a list of statistic boosts to apply to the player,
     * given the number of pieces of the set the player has equipped.
     * @param player The player that the set effect is being applied to.
     * @param count The number of pieces of the set the player has equipped.
     * @return A list of statistic boosts to apply to the player.
     */
    public abstract List<StatisticBoost> getStatisticBoosts(GamePlayer player, int count);

    private void applyLore(GamePlayer player, int count) {
        System.out.println("Applying lore to player " + player.getName() + " for set effect " + this.id + " with count " + count);
        for (var piece : player.getInventory().getEquipment()) {
            if (piece != null) {
                NBT.modify(piece.itemStack, (nbt) -> {
                    System.out.println("NBT: " + nbt);
                    nbt.setInteger("SetCount", count);
                    ItemManufacturer.constructLore(piece, nbt, player);
                });
            }
        }
    }
}
