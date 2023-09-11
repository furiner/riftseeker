package net.arcadiasedge.riftseeker.entities.dummies;

import net.arcadiasedge.riftseeker.entities.NPCEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;

public class TrainingDummyEntity extends NPCEntity<Zombie> {
    public TrainingDummyEntity() {
        super(EntityType.ZOMBIE, "Training Dummy");

        this.getStatistics().setBaseStatistic("health", 150_000);
    }

    @Override
    public void update() {
//        var healthStatistic = this.getStatistics().getStatistic("health");
//        if (healthStatistic.getCurrent() <= healthStatistic.getFinalTotal()) {
//            // Add the difference between the current and the final total back to the current.
//            healthStatistic.add(healthStatistic.getFinalTotal() - healthStatistic.getCurrent());
//        }
    }

    @Override
    public void setup() {

    }

    @Override
    public void onSpawn() {
        return;
    }
}
