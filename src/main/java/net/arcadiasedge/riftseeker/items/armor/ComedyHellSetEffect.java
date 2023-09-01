package net.arcadiasedge.riftseeker.items.armor;

import net.arcadiasedge.riftseeker.entities.GameEntity;
import net.arcadiasedge.riftseeker.entities.players.GamePlayer;
import net.arcadiasedge.riftseeker.entities.statistics.StatisticsMap;
import net.arcadiasedge.riftseeker.items.GameEquipmentSlot;
import net.arcadiasedge.riftseeker.items.sets.SetEffect;
import net.arcadiasedge.riftseeker.statistics.BoostType;
import net.arcadiasedge.riftseeker.statistics.StatisticBoost;
import java.util.List;

public class ComedyHellSetEffect extends SetEffect {
    public ComedyHellSetEffect() {
        super("COMEDY_HELL");

        this.addRequiredPiece(GameEquipmentSlot.HELMET, "HELMET_OF_COMEDY");
    }

    @Override
    public List<StatisticBoost> getStatisticBoosts(GamePlayer player, int count) {
        return List.of(new StatisticBoost("strength") {
            @Override
            public float getValue(Object owner, StatisticsMap<?> statistic) {
                return statistic.getBaseTotal() * 1.5f;
            }
        });
    }
}
