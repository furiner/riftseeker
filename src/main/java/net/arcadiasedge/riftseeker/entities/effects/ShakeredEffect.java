package net.arcadiasedge.riftseeker.entities.effects;

import net.arcadiasedge.riftseeker.entities.GameEntity;
import net.arcadiasedge.riftseeker.entities.statistics.StatisticsMap;
import net.arcadiasedge.riftseeker.statistics.StatisticBoost;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class ShakeredEffect extends StatusEffect {
    public ShakeredEffect(int length) {
        super(length, 1);
    }

    @Override
    public List<StatisticBoost> apply() {
        this.getLivingEntity().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 9999, 1));

        return new ArrayList<>() {{
            add(new StatisticBoost("damage") {
                @Override
                public float getValue(Object owner, StatisticsMap<?> statistic) {
                    return -10000;
                }
            });
        }};
    }

    public void unapply() {
        if (this.getLivingEntity().hasPotionEffect(PotionEffectType.SLOW)) {
            this.getLivingEntity().removePotionEffect(PotionEffectType.SLOW);
        }
    }
}
