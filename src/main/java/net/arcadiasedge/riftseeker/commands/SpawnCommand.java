package net.arcadiasedge.riftseeker.commands;

import dev.jorel.commandapi.annotations.Command;
import dev.jorel.commandapi.annotations.Default;
import net.arcadiasedge.riftseeker.entities.dummies.TrainingDummyEntity;
import net.arcadiasedge.riftseeker.utils.ColorMap;
import net.arcadiasedge.riftseeker.utils.rarity.RarityMap;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

@Command("spawn")
public class SpawnCommand extends RiftseekerCommand {
    @Default
    public static void rarityMap(Player player) {
        // Spawn a training dummy for now.
        var trainingDummy = new TrainingDummyEntity();

        trainingDummy.spawn(player.getLocation());

        player.sendMessage("Spawned a training dummy.");
    }
}
