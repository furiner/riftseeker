package net.arcadiasedge.riftseeker.items.enchantments;

import net.arcadiasedge.riftseeker.entities.GameEntity;
import net.arcadiasedge.riftseeker.entities.GameNPCEntity;
import net.arcadiasedge.riftseeker.entities.statistics.StatisticsMap;
import net.arcadiasedge.riftseeker.items.Item;
import net.arcadiasedge.riftseeker.items.ItemType;
import net.arcadiasedge.riftseeker.statistics.StatisticBoost;

import java.util.List;

public class FuckYouEnchantment extends Enchantment {
    public FuckYouEnchantment() {
        super("FUCK_YOU", "Fuck You");

        this.setItemType(ItemType.WEAPON);
    }

    @Override
    public List<StatisticBoost> onApply(Item item) {
        return List.of(new StatisticBoost("strength") {
            @Override
            public float getValue(GameEntity<?> player, StatisticsMap statistic) {
                return 9 * FuckYouEnchantment.this.getLevel();
            }
        });
    }

    @Override
    public void onHit(GameEntity<?> entity, float damage) {
        if (entity instanceof GameNPCEntity<?> npcEntity) {
            // Begone bitch
            System.out.println("Begone, thot.");
            npcEntity.despawn();
        }
    }

    @Override
    public boolean canEnchant(Item item) {
        return true;
    }
}