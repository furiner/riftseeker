package net.arcadiasedge.riftseeker.abilities;

import de.tr7zw.nbtapi.NBT;
import net.arcadiasedge.riftseeker.RiftseekerPlugin;
import net.arcadiasedge.riftseeker.api.partials.ApiAbility;
import net.arcadiasedge.riftseeker.entities.GameEntity;
import net.arcadiasedge.riftseeker.entities.NPCEntity;
import net.arcadiasedge.riftseeker.entities.effects.ShakeredEffect;
import net.arcadiasedge.riftseeker.entities.players.GamePlayer;
import net.arcadiasedge.riftseeker.items.Item;
import net.arcadiasedge.riftseeker.utils.RNG;
import net.arcadiasedge.riftseeker.world.GameWorld;
import net.citizensnpcs.util.Quaternion;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TheShakerAbility extends Ability {
    public TheShakerAbility(Item item, ApiAbility ability) {
        this.setItem(item);
        this.setApiAbility(ability);
        this.setCooldown(5 * 20); // 5 seconds
    }

    @Override
    public float calculateDamage(GamePlayer player, GameEntity<?> target) {
        return this.calculateBaseDamage(player);
    }

    @Override
    public List<GameEntity<?>> execute(GamePlayer player) {
        List<Location> blocks = new ArrayList<>();
        Player playerEntity = player.getEntity();
        Location location = playerEntity.getLocation();
        World world = location.getWorld();

        // Get a random radius between 2 numbers
        int randomRadius = RNG.getRandomInt(4, 6);
        int y = location.getBlockY();

        while (world.getBlockAt(location.getBlockX(), y, location.getBlockZ()).getType() == Material.AIR) {
            y--;
        }

        // Get all the locations within the radius
        // This is primarily for entity detection.
        for (int x = location.getBlockX() - randomRadius; x <= location.getBlockX() + randomRadius; x++) {
            for (int z = location.getBlockZ() - randomRadius; z <= location.getBlockZ() + randomRadius; z++) {
                Location block = new Location(world, x, y, z);

                if (block.distance(location) <= randomRadius) {
                    blocks.add(block);
                }
            }
        }

        // Create a rippling effect
        float finalY = (float)y;

         new BukkitRunnable()
         {
            double radius = 1.0;
            final double interval = 0.75;

            @Override
            public void run()
            {
                for (double theta = 0; theta <= 2 * Math.PI; theta += interval) {
                    double dx = radius * Math.cos(theta);
                    double dz = radius * Math.sin(theta);

                    Location block = new Location(world, location.getX() + dx, finalY, location.getZ() + dz);

                    FallingBlock fallingBlock = world.spawnFallingBlock(block, block.getBlock().getBlockData());
                    PersistentDataContainer container = fallingBlock.getPersistentDataContainer();

                    container.set(RiftseekerPlugin.getInstance().getKey("riftseeker-ability-block"), PersistentDataType.BYTE, (byte) 1);

                    fallingBlock.setDropItem(false);
                    fallingBlock.setVelocity(new Vector(0, 0.35, 0));
                }

                radius += interval;

                if (radius >= randomRadius) {
                    this.cancel();
                }
            }
        }.runTaskTimer(RiftseekerPlugin.getInstance(), 0, 1);

        // Get a list of entities within the radius
        List<GameEntity<?>> targets = new ArrayList<>();

        for (Location block : blocks) {
            for (var entity : block.getNearbyEntities(1.5, 1.5, 1.5)) {
                if (entity instanceof Player) {
                    continue;
                }

                var gameEntity = GameWorld.getInstance().getEntity(entity);

                if (gameEntity != null) {
                    if (targets.contains(gameEntity)) {
                        continue;
                    }

                    targets.add(gameEntity);
                }
            }
        }

        return targets;
    }

    @Override
    public float onEntityHit(GamePlayer player, GameEntity<?> target, int index) {
        System.out.println("TheShakerAbility.onEntityHit(): " + target.getName() + " hit by " + player.getName() + " with " + this.getName());
        target.getEntity().setVelocity(new Vector(0, 0.35, 0));
        target.giveEffect(new ShakeredEffect(10));

        return this.calculateDamage(player, target);
    }
}
