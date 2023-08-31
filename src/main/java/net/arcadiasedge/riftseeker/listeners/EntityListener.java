package net.arcadiasedge.riftseeker.listeners;

import net.arcadiasedge.riftseeker.entities.players.GamePlayer;
import net.arcadiasedge.riftseeker.entities.projectiles.GameArrow;
import net.arcadiasedge.riftseeker.world.GameWorld;
import org.bukkit.entity.Arrow;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

public class EntityListener implements Listener {
    @EventHandler
    public void onShootBow(EntityShootBowEvent event) {
        // We're going to handle this ourselves.
        event.setCancelled(true);
        event.setConsumeItem(false);

        var entity = GameWorld.getInstance().getEntity(event.getEntity());

        if (entity == null) {
            return;
        }

        if (entity instanceof GamePlayer player) {
            var arrow = player.getInventory().get(event.getConsumable());
            var bow = player.getInventory().get(event.getBow());

            if (arrow == null || bow == null) {
                return;
            }

            var arrowItemStack = arrow.getItemStack();

            // Consume the arrow.
            if (arrowItemStack.getAmount() == 1) {
                player.getInventory().remove(arrow);
            } else {
                arrowItemStack.setAmount(arrowItemStack.getAmount() - 1);
            }

            var arrowProjectile = event.getProjectile();
            var arrowEntity = GameWorld.getInstance().getWorld().spawnArrow(
                    arrowProjectile.getLocation(),
                    arrowProjectile.getVelocity(),
                    1.0f,
                    0
            );

            var arrowGameEntity = new GameArrow(player, arrowEntity, bow);

            if (arrow.attributes.containsKey("damage")) {
                // Get the arrow's damage statistic and set its contributor value to the arrow's damage.
                // Then, immediately take a snapshot of the statistic based off the arrow.
                System.out.println("Arrow damage: " + arrow.attributes.get("damage").getFinalValue());
                var damage = player.getStatistics().getStatistic("damage");
                damage.setContributorValue(arrow, arrow.attributes.get("damage").getFinalValue());
                player.getStatistics().takeSnapshot(arrowGameEntity);
                damage.removeContributor(arrow);
            }

            GameWorld.getInstance().getEntities().add(arrowGameEntity);
            bow.onArrowShoot(arrowGameEntity);
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        // We're going to handle this event ourselves.
        event.setCancelled(true);
        var gameEntity = GameWorld.getInstance().getEntity(event.getEntity());

        if (gameEntity == null) {
            event.getEntity().remove();
            return;
        }

        var arrowEntity = (GameArrow) gameEntity;

        if (event.getHitBlock() != null) {
            GameWorld.getInstance().getEntities().remove(arrowEntity);
            event.getEntity().remove();
        }

        var shooter = arrowEntity.getOwner();

        var hitEntity = event.getHitEntity();

        var hitGameEntity = GameWorld.getInstance().getEntity(hitEntity);
        if (hitGameEntity == null) {
            GameWorld.getInstance().getEntities().remove(arrowEntity);
            event.getEntity().remove();
            return;
        }

        if (shooter instanceof GamePlayer player) {
            // TODO: This is not guaranteed to be the actual bow at the time of hit. (could have switched)
            // TODO: I believe this is fixed now.
            var damage = player.calculateWeaponDamage(hitGameEntity, arrowEntity.getWeapon(), arrowEntity);

            System.out.println("Damage: " + damage.getA() + " " + damage.getB());

            hitGameEntity.damage(damage.getB(), damage.getA());
        }
    }
}
