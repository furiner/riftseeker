package net.arcadiasedge.riftseeker.commands;

import dev.jorel.commandapi.annotations.Command;
import dev.jorel.commandapi.annotations.Default;
import dev.jorel.commandapi.annotations.arguments.AStringArgument;
import net.arcadiasedge.riftseeker.RiftseekerPlugin;
import net.arcadiasedge.riftseeker.entities.GameEntity;
import net.arcadiasedge.riftseeker.entities.NPCEntity;
import net.arcadiasedge.riftseeker.entities.dummies.TrainingDummyEntity;
import net.arcadiasedge.riftseeker.managers.GameEntityManager;
import net.arcadiasedge.riftseeker.utils.ColorMap;
import net.arcadiasedge.riftseeker.utils.rarity.RarityMap;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

@Command("spawn")
public class SpawnCommand extends RiftseekerCommand {
    @Default
    public static void spawnEntity(Player player, @AStringArgument String entityName) {
        var minimessage = MiniMessage.miniMessage();

        var location = player.getLocation();
        location.add(location.getDirection().multiply(2));

        // Make sure the entity spawns above the ground
        location.setY(location.getWorld().getHighestBlockYAt(location) + 1);

        GameEntityManager manager = RiftseekerPlugin.getInstance().getManager("entities");
        GameEntity<?> entity;
        try {
            entity = manager.create(entityName.toUpperCase());
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        if (entity == null) {
            player.sendMessage(minimessage.deserialize("<red>This entity does not exist."));
            return;
        }

        if (!(entity instanceof NPCEntity<?> npc)) {
            player.sendMessage(minimessage.deserialize("<red>Provided entity cannot be spawned."));
            return;
        }

        npc.spawn(location);
        player.sendMessage("Spawned entity.");
    }
}
