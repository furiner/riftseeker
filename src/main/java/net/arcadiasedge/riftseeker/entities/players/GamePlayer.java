package net.arcadiasedge.riftseeker.entities.players;

import net.arcadiasedge.riftseeker.RiftseekerPlugin;
import net.arcadiasedge.riftseeker.abilities.Ability;
import net.arcadiasedge.riftseeker.api.ApiProfile;
import net.arcadiasedge.riftseeker.api.partials.ApiSetEffect;
import net.arcadiasedge.riftseeker.entities.GameEntity;
import net.arcadiasedge.riftseeker.entities.statistics.StatisticsSnapshot;
import net.arcadiasedge.riftseeker.entities.statuses.TexturePackStatus;
import net.arcadiasedge.riftseeker.items.DamageType;
import net.arcadiasedge.riftseeker.items.GameEquipmentSlot;
import net.arcadiasedge.riftseeker.items.Item;
import net.arcadiasedge.riftseeker.items.ItemType;
import net.arcadiasedge.riftseeker.items.enchantments.Enchantment;
import net.arcadiasedge.riftseeker.items.sets.SetEffect;
import net.arcadiasedge.riftseeker.managers.SetEffectManager;
import net.arcadiasedge.riftseeker.world.GameWorld;
import net.arcadiasedge.riftseeker.world.locations.GameLocation;
import net.minecraft.util.Tuple;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * A representation of a player in the game, catered towards having MMO-like elements in the game.
 * This class is used to store player data, such as their inventory, online profile, among other things.
 */
public class GamePlayer extends GameEntity<Player> {
    /**
     * The cached database profile of the player.
     * This should not be used to get player's statistics, as it is not updated in real-time.
     * Instead, use {@link GamePlayer#statistics} to get the player's statistics.
     */
    public ApiProfile profile;

    /**
     * The player's current inventory, which is used to store custom Riftseeker items and armor.
     */
    public GamePlayerInventory inventory;

    /**
     * The texture pack status of the player.
     * When they are loaded, custom textures, models, and shaders will be applied to enhance the player's experience.
     */
    public TexturePackStatus texturePackStatus;

    /**
     * The current location of the player within the game world.
     * This can also be used to determine the player's current world and or general zone.
     */
    public GameLocation location;

    private final Map<String, Integer> cooldowns;

    public GamePlayer(ApiProfile profile, Player player) {
        super(player);

        this.profile = profile;
        this.inventory = new GamePlayerInventory(this);
        this.texturePackStatus = TexturePackStatus.Loading;
        this.location = GameWorld.UnknownLocation.UNKNOWN_LOCATION;
        this.cooldowns = new HashMap<>();
    }

    public GamePlayerInventory getInventory() {
        return inventory;
    }

    public ApiProfile getProfile() {
        return profile;
    }

    public void onSwapItem(Item oldItem, Item newItem) {
        var statistics = getStatistics();

        if (oldItem != null && oldItem.baseItem.getType().isItem()) {
            var oldEnchantments = oldItem.enchantments;

            // Try to remove the old item's stats from the player's statistics
            for (var statistic : statistics.getValues()) {
                if (statistic.hasContributor(oldItem)) {
                    statistic.removeContributor(oldItem);
                }

                for (var enchantment : oldEnchantments) {
                    if (statistic.hasBoost(enchantment)) {
                        statistic.removeBoosts(enchantment);
                    }
                }
            }
        }

        if (newItem != null && newItem.baseItem.getType().isItem()) {
            var newEnchantments = newItem.enchantments;

            // Try to add the new item's stats to the player's statistics
            for (var statistic : statistics.getValues()) {
                if (newItem.attributes.containsKey(statistic.getName())) {
                    var attribute = newItem.attributes.get(statistic.getName());
                    statistic.setContributorValue(newItem, attribute.getFinalValue());
                }
            }

            for (var enchantment : newEnchantments) {
                var toApply = enchantment.onApply(newItem);

                for (var boost : toApply) {
                    var statistic = statistics.getStatistic(boost.statistic);
                    statistic.addBoost(enchantment, boost);
                }
            }
        }

        this.getInventory().setHeld(newItem);
    }

    public void onEquipArmor(Item item, GameEquipmentSlot slot) {
        if (item == null) {
            return;
        }

        if (item.baseItem.getType() != ItemType.EQUIPMENT) {
            return;
        }

        // Check if there's already an item in the slot
        var inventory = this.getInventory();
        var currentEquipment = inventory.getEquipmentPiece(slot);

        if (currentEquipment != null) {
            // Remove the current equipment's stats from the player's statistics
            this.removeStatisticFor(currentEquipment);
        }

        // Add the item's stats to the player's statistics
        this.addStatisticFor(item);
        this.inventory.setEquipmentPiece(slot, item);

        System.out.println("Equipped " + item.baseItem.id + " to " + slot.name());

        try {
            this.applySetEffects();
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void onUnequipArmor(Item item, GameEquipmentSlot slot) {
        var inventory = this.getInventory();
        if (item == null) {
            // Set the respective slot to null, and remove the stats from the player's statistics
            if (inventory.getEquipmentPiece(slot) != null) {
                var equipmentItem = inventory.getEquipmentPiece(slot);

                this.removeStatisticFor(equipmentItem);
                inventory.setEquipmentPiece(slot, null);

                System.out.println("Unequipped " + equipmentItem.baseItem.id + " from " + slot.name());

                try {
                    this.applySetEffects();
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException(e);
                } catch (InstantiationException e) {
                    throw new RuntimeException(e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }

            return;
        }

        this.removeStatisticFor(item);
        this.getInventory().setEquipmentPiece(slot, null);

        System.out.println("Unequipped " + item.baseItem.id + " from " + slot.name());

        try {
            this.applySetEffects();
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Applies a cooldown for the specified ability in ticks.
     * This will prevent the player from using the ability until the cooldown is over.
     * @param ability The ability to apply the cooldown for.
     * @param ticks The amount of ticks that this ability should be on cooldown for.
     */
    public void addCooldown(Ability ability, int ticks) {
        cooldowns.put(ability.getId(), ticks);
        System.out.println("Removed cooldown for " + ability.getId());

        // Remove the cooldown after the specified amount of ticks
        new BukkitRunnable() {
            @Override
            public void run() {
                if (cooldowns.containsKey(ability.getId())) {
                    System.out.println("Removed cooldown for " + ability.getId());
                    cooldowns.remove(ability.getId());
                    this.cancel();
                }
            }
        }.runTaskLaterAsynchronously(RiftseekerPlugin.getInstance(), ticks);
    }

    /**
     * Whether the player has a cooldown for the specified ability.
     * @param ability The ability to check for.
     * @return True if the player has a cooldown for the specified ability, otherwise false.
     */
    public boolean hasCooldown(Ability ability) {
        return cooldowns.containsKey(ability.getId());
    }

    /**
     * Removes an enchantment from the player's statistics.
     * <br>
     * Note that this will not remove the enchantment from the item itself, as that
     * would have to be done manually through the item's enchantments list.
     * @param enchantment The enchantment to remove.
     */
    public void removeEnchantment(Enchantment enchantment) {
        for (var statistic : statistics.getValues()) {
            if (statistic.hasBoost(enchantment)) {
                statistic.removeBoosts(enchantment);
            }
        }
    }

    /**
     * Adds an enchantment to the player's statistics.
     * <br>
     * Note that this will not add the enchantment to the item itself, as that
     * would have to be done manually through the item's enchantments list.
     * @param enchantment The enchantment to add.
     */
    public void addEnchantment(Enchantment enchantment) {
        var toApply = enchantment.onApply(inventory.getHeld());

        for (var boost : toApply) {
            var statistic = statistics.getStatistic(boost.statistic);
            statistic.addBoost(enchantment, boost);
        }
    }

    public Tuple<Boolean, Float> calculateWeaponDamage(GameEntity<?> target, Item heldItem) {
        return calculateWeaponDamage(target, heldItem, null);
    }

    /**
     * Calculates the damage that the player should deal to the specified target.
     * <br><br>
     * In order to calculate the damage, the following formula is used:
     * <ul>
     *     <li>Calculate the total damage that will be dealt by the player ("damage" statistic).</li>
     *     <li>Fetch the defense value for the specified target.</li>
     *     <li>If we're holding a weapon...</li>
     *     <ul>
     *         <li>Check the damage type that given weapon will apply.</li>
     *         <li>If the type is magical, apply intelligence as the <code>b</code> parameter.</li>
     *         <li>If the type is physical, apply strength as the <code>b</code> parameter.</li>
     *         <li>If the type is ranged, apply dexterity as the <code>b</code> parameter.</li>
     *         <li>If the type is true, apply strength as the <code>b</code> parameter, however do not apply the target's defense statistic in the calculation.</li>
     *     </ul>
     *     <li>If we're not holding a weapon, apply the final strength statistic as the <code>b</code> parameter.</li>
     *     <li>
     *         Formula: <code>finalDamage = damage * (1 - (defense / (defense + 50))) * (1 + (b / 100))</code>
     *     </li>
     *     <li>Roll if the hit is a crit using the <code>crit_chance</code> statistic, if so, apply the <code>crit_damage</code> multiplier towards the final damage</li>
     *     <li>Finally, apply every enchantment the item has using the {@link Enchantment#onHit(GameEntity, float)} method.</li>
     *     <li>Return the final damage value, along with whether the damage was a crit.</li>
     * </ul>
     * @param target The target to calculate the damage for.
     * @param heldItem The item that the player is holding. (or was holding, during the time of the calculation)
     * @return A tuple containing the final damage value, along with whether the damage was a crit.
     */
    public Tuple<Boolean, Float> calculateWeaponDamage(GameEntity<?> target, Item heldItem, Object source) {
        var statistics = getStatistics();
        var statisticsTarget = target.getStatistics();
        var statisticsSnapshot = statistics.consumeSnapshot(source != null ? source : heldItem);

        // The user's own values
        var playerDamage = statisticsSnapshot.get("damage");
        var finalDamage = 0.0f;

        // Damage reduction algorithm
        // Lower deense should result in more damage taken
        var targetDefense = statisticsTarget.getStatisticValue("defense");

        if (heldItem != null) {
            // https://www.desmos.com/calculator/g13nmptjqf
            if (heldItem.baseItem.properties.getDamageType() == DamageType.MAGICAL) {
                /// Magical Damage Calculations
                // Scale damage with Intelligence
                var playerIntelligence = statisticsSnapshot.get("intelligence");
                finalDamage = playerDamage * (1.0f - (targetDefense / (targetDefense + 50.0f))) * (1.0f + (playerIntelligence / 100.0f));
            } else if (heldItem.baseItem.properties.getDamageType() == DamageType.PHYSICAL) {
                /// Physical Damage Calculations
                // Scale damage with Strength
                var playerStrength = statisticsSnapshot.get("strength");
                finalDamage = playerDamage * (1.0f - (targetDefense / (targetDefense + 50.0f))) * (1.0f + (playerStrength / 100.0f));
            } else if (heldItem.baseItem.properties.getDamageType() == DamageType.RANGED) {
                /// Ranged Damage Calculations
                var playerDexterity = statisticsSnapshot.get("dexterity");
                finalDamage = playerDamage * (1.0f - (targetDefense / (targetDefense + 50.0f))) * (1.0f + (playerDexterity / 100.0f));

                // TODO: maybe add a check for the player's distance from the target?
            } else if (heldItem.baseItem.properties.getDamageType() == DamageType.TRUE) {
                // True Damage Calculations
                // Scale damage with Strength
                var playerStrength = statisticsSnapshot.get("strength");
                finalDamage = playerDamage * (1.0f + (playerStrength / 100.0f));
            }
        } else {
            // We're attacking with a fist, which is technically physical
            // So we should handle it similarly to physical damage, but with a heavier nerf.
            var playerStrength = statisticsSnapshot.get("strength");
            finalDamage = playerDamage * (1.0f - (targetDefense / (targetDefense + 50.0f))) * (1.0f + (playerStrength / 200.0f));
        }

        if (finalDamage < 0.0f) {
            finalDamage = 0.0f;
        }

        // Handle a random chance for a crit
        var isCrit = rollForCrit(statisticsSnapshot);
        finalDamage = finalDamage * (isCrit ? statisticsSnapshot.get("crit_damage") : 1.0f);

        if (getInventory().getHeld() != null) {
            var item = getInventory().getHeld();
            for (var enchantment : item.enchantments) {
                System.out.println("Enchantment: " + enchantment.getClass().getSimpleName());
                enchantment.onHit(target, finalDamage);
            }
        }

        return new Tuple<>(isCrit, finalDamage);
    }

    @Override
    public void update() {
        // Ultimately, this is a no-op, but it's here for consistency
        // and incase we ever need to do something with the player
        // entity.
        var statistics = getStatistics();
        var inventory = getInventory();
        var item = getInventory().getHeld();

        // Handle every worn & held item
        if (item != null) {
            for (var attributeKeyPair : getInventory().getHeld().attributes.entrySet()) {
                if (statistics.getStatistic(attributeKeyPair.getKey()).hasContributor(item)) {
                    // Check if the value has changed
                    var attribute = attributeKeyPair.getValue();

                    if (statistics.getStatistic(attributeKeyPair.getKey()).getContributorValue(item) != attribute.getValue()) {
                        statistics.getStatistic(attributeKeyPair.getKey()).setContributorValue(item, attribute.getFinalValue());
                    }
                }
            }
        }
    }

    @Override
    public void setup() {
        // Add base statistics
        var statistics = getStatistics();

        statistics.getStatistic("health").setContributorValue(this, 100.0f);
        statistics.getStatistic("defense").setContributorValue(this, 1.0f);
        statistics.getStatistic("damage").setContributorValue(this, 10.0f);
        statistics.getStatistic("strength").setContributorValue(this, 5.0f);
        statistics.getStatistic("intelligence").setContributorValue(this, 5.0f);
        statistics.getStatistic("crit_chance").setContributorValue(this, 0.15f); // 15%
        statistics.getStatistic("crit_damage").setContributorValue(this, 1.5f); // 150%
    }

    public boolean rollForCrit() {
        return rollForCrit(null);
    }
    /**
     * Rolls a random number and compares it against the <code>crit_chance</code> statistic value.
     * @return True if the rolled number is below, otherwise false.
     */
    public boolean rollForCrit(StatisticsSnapshot snapshot) {
        var statistics = getStatistics();
        var random = new Random();
        return snapshot != null ? snapshot.get("crit_chance") > random.nextFloat(): statistics.getStatistic("crit_chance").getFinalTotal() > random.nextFloat();
    }

    /**
     * Removes a contributor from every statistic.
     * @param contributor The contributor to remove.
     */
    private void removeStatisticFor(Object contributor) {
        for (var statistic : statistics.getValues()) {
            if (statistic.hasContributor(contributor)) {
                statistic.removeContributor(contributor);
            }
        }
    }

    /**
     * Adds a contributor to every statistics that it applies to.
     * @param contributor The contributor to apply.
     */
    private void addStatisticFor(Item contributor) {
        for (var statistic : statistics.getValues()) {
            if (!statistic.hasContributor(contributor) && contributor.attributes.containsKey(statistic.getName())) {
                statistic.setContributorValue(contributor, contributor.attributes.get(statistic.getName()).getFinalValue());
            }
        }
    }

    /**
     * Applies set effects onto
     */
    private void applySetEffects() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        List<ApiSetEffect> applied = new ArrayList<>();

        // Iterate through the slots to check for set effects
        for (var piece : this.getInventory().getEquipment()) {
            if (piece != null && piece.baseItem.setEffect != null) {
                var setEffect = piece.baseItem.setEffect;

                if (!applied.contains(setEffect)) {
                    // Check if the player has the set effect already
                    if (this.getInventory().getSetEffect(setEffect.id) != null) {
                        // Reapply the set effect
                        var effect = this.getInventory().getSetEffect(setEffect.id);

                        // Apply the set effect
                        var count = effect.apply(this);

                        if (count == 0) {
                            // Remove the set effect
                            this.getInventory().removeSetEffect(setEffect.id);
                        } else {
                            applied.add(setEffect);
                        }
                    } else {
                        SetEffectManager sem = RiftseekerPlugin.getInstance().getManager("sets");

                        // Add the set effect to the player's inventory
                        SetEffect effect = sem.create(setEffect.id);
                        this.getInventory().setSetEffect(effect);

                        // Apply the set effect
                        var count = effect.apply(this);

                        if (count == 0) {
                            // Remove the set effect (albeit we really shouldn't be here)
                            this.getInventory().removeSetEffect(setEffect.id);
                        } else {
                            applied.add(setEffect);
                        }
                    }
                }
            }
        }
    }
}
