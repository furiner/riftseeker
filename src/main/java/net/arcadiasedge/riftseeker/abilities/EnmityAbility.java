package net.arcadiasedge.riftseeker.abilities;

import com.google.common.util.concurrent.Futures;
import net.arcadiasedge.riftseeker.api.partials.ApiAbility;
import net.arcadiasedge.riftseeker.entities.GameEntity;
import net.arcadiasedge.riftseeker.entities.GameNPCEntity;
import net.arcadiasedge.riftseeker.entities.players.GamePlayer;
import net.arcadiasedge.riftseeker.items.Item;
import net.arcadiasedge.riftseeker.world.GameWorld;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import xyz.xenondevs.particle.ParticleBuilder;
import xyz.xenondevs.particle.ParticleEffect;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

public class EnmityAbility extends Ability {
    public EnmityAbility(Item item, ApiAbility ability) {
        this.setItem(item);
        this.setApiAbility(ability);
    }
    @Override
    public float calculateDamage(GamePlayer player, GameEntity<?> target) {
        return 0.0f;
    }

    @Override
    public List<GameEntity<?>> execute(GamePlayer player) {
        var playerEntity = player.getEntity();
        var world = playerEntity.getWorld();

        System.out.println("EnmityAbility.execute()");

        // Send a line of particles from the player to the target.
        // Get the current direction that the player is facing.
        var location = playerEntity.getEyeLocation();
        var direction = location.getDirection();
        var particleLocation = location.clone();

        List<Location> locations = new ArrayList<>();
        List<GameEntity<?>> targets = new ArrayList<>();

        // A custom offset to set.
        var offsetFloat = 0.5f;
        var offset = direction.clone().multiply(offsetFloat);

        // Prematurely add the ability to the cooldown list.
        player.addCooldown(this, 20);

        for (var i = 0; i < 20; i++) {
            particleLocation.add(offset);

            new ParticleBuilder(ParticleEffect.FIREWORKS_SPARK, particleLocation)
                    .setAmount(1)
                    .setSpeed(0)
                    .display();
        }

        // Check each game entity in the world to see if it is within the line of particles.
        for (var loc : locations) {
            for (var entity : world.getNearbyEntities(loc, 1, 1, 1)) {
                if (entity instanceof Player) {
                    continue;
                }

                var gameEntity = GameWorld.getInstance().getEntity(entity);

                if (gameEntity != null) {
                    if (targets.contains(gameEntity)) {
                        continue;
                    }

                    var particleMinVector = new Vector(
                            loc.getX() - offsetFloat,
                            loc.getY() - offsetFloat,
                            loc.getZ() - offsetFloat);
                    var particleMaxVector = new Vector(
                            loc.getX() + offsetFloat,
                            loc.getY() + offsetFloat,
                            loc.getZ() + offsetFloat);

                    if (entity.getBoundingBox().overlaps(particleMinVector, particleMaxVector)) {
                        targets.add(gameEntity);
                    }
                }
            }
        }

        return targets;
    }

    @Override
    public float onEntityHit(GamePlayer player, GameEntity<?> target, int index) {
        System.out.println("EnmityAbility.onEntityHit(): " + target.getName() + " hit by " + player.getName() + " with " + this.getName());

        return this.calculateDamage(player, target);
    }
}
