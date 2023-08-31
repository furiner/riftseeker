package net.arcadiasedge.riftseeker.statistics;

import net.arcadiasedge.riftseeker.entities.GameEntity;
import net.arcadiasedge.riftseeker.entities.players.GamePlayer;
import net.arcadiasedge.riftseeker.entities.statistics.StatisticsMap;

/**
 * Anything that boosts the statistics of an item, or target entity.
 */
public abstract class StatisticBoost {
    public String statistic;

    public StatisticBoost(String statistic) {
        this.statistic = statistic;
    }

    public abstract float getValue(GameEntity<?> player, StatisticsMap statistic);
}
