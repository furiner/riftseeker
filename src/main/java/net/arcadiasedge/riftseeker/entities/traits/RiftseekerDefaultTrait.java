package net.arcadiasedge.riftseeker.entities.traits;

import net.arcadiasedge.riftseeker.RiftseekerPlugin;
import net.arcadiasedge.riftseeker.abilities.Ability;
import net.arcadiasedge.riftseeker.entities.NPCEntity;
import net.arcadiasedge.riftseeker.world.GameWorld;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.ClickType;

/**
 * This is a class made to implement the default traits for a Riftseeker NPC.
 *
 * Majorly, aggression handling, damage handling, and death handling.
 */
@TraitName("riftseeker_defualt")
public class RiftseekerDefaultTrait extends Trait {
    private final RiftseekerPlugin plugin;
    private NPCEntity<?> entity;

    public RiftseekerDefaultTrait() {
        super("riftseeker_default");

        plugin = RiftseekerPlugin.getInstance();
    }

    @EventHandler()
    public void onLeftClick(NPCLeftClickEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (event.getNPC() != this.getNPC()) {
            return;
        }

        var p = event.getClicker();
        var player = GameWorld.getInstance().getPlayer(p);

        System.out.println("Left click event fired.");

        // Check the player's held item.
        var heldItem = player.getInventory().getHeld();

        if (heldItem != null) {
            // Check the left click abilities.
            Ability finalAbility = null;
            for (Ability ability : heldItem.getAbilities()) {
                if (ability.getTrigger() == ClickType.LEFT) {
                    finalAbility = ability;
                    break;
                }
            }

            if (finalAbility != null) {
                // Check if the ability is on cooldown.
                if (!player.hasCooldown(finalAbility)) {
                    // Likely using the ability, so we should cancel the event.
                    // This is just a check as this event is usually fired before the player's
                    // actual interaction event.
                    return;
                }
            }
        }

        // TODO: probably a good idea to check if the entity we're hitting is something we *can* hit.
        if (heldItem != null && !heldItem.baseItem.getType().isMeleeWeapon()) {
            System.out.println("Not a melee weapon.");
            return;
        }

        var damage = player.calculateWeaponDamage(entity, heldItem);

        if (entity.getStatistics().getStatisticValue("health") <= 0) {
            return;
        }

        entity.damage(player, damage.getB(), damage.getA());
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onRightClick(NPCRightClickEvent event) {

    }

    @Override
    public void onSpawn() {
        // Find the entity from riftseeker's list of entities after waiting a bit.
        // This is because the entity is not immediately available after spawning.
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            entity = (NPCEntity<?>) GameWorld.getInstance().getEntities().stream().filter(e -> e.getEntity().getUniqueId().equals(npc.getEntity().getUniqueId())).findFirst().orElse(null);

            System.out.println("Found entity: " + entity);
        }, 1);
    }
}
