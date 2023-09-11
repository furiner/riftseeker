package net.arcadiasedge.riftseeker.utils;

import java.util.Map;

public class RNG {
    public static String fromWeightedChance(Map<String, Float> weights) {
        float totalWeight = 0;
        for (float weight : weights.values()) {
            totalWeight += weight;
        }
        float random = (float) Math.random() * totalWeight;
        for (Map.Entry<String, Float> entry : weights.entrySet()) {
            random -= entry.getValue();
            if (random <= 0) {
                return entry.getKey();
            }
        }
        return null;
    }

    public static int getRandomInt(int min, int max) {
        return (int) (Math.random() * (max - min + 1) + min);
    }
}
