package net.arcadiasedge.riftseeker.utils;

import com.google.api.client.util.ArrayMap;
import org.javatuples.Triplet;

import java.util.Map;

public class ItemConstants {
    /**
     * These are all the possible enhancement rates for an item, based on its current
     * enhancement level.
     * <p>
     * The order of the values in the triplet are as follows:
     * 1. the rate of success
     * 2. the rate for no change
     * 3. the rate for a downgrade
     * <p>
     * Each rate is a percentage, and the sum of all three rates must equal 100.
     */
    public static Map<Integer, Triplet<Float, Float, Float>> ENHANCEMENT_RATES = new ArrayMap<>() {
        {
            this.add(1, new Triplet<>(100.0f, 0.0f, 0.0f));
            this.add(2, new Triplet<>(90.0f, 5.0f, 5.0f));
            this.add(3, new Triplet<>(80.0f, 10.0f, 10.0f));
            this.add(4, new Triplet<>(70.0f, 15.0f, 15.0f));
            this.add(5, new Triplet<>(60.0f, 20.0f, 20.0f));
            this.add(6, new Triplet<>(50.0f, 25.0f, 25.0f));
            this.add(7, new Triplet<>(40.0f, 30.0f, 30.0f));
            this.add(8, new Triplet<>(30.0f, 35.0f, 35.0f));
            this.add(9, new Triplet<>(20.0f, 40.0f, 40.0f));
            this.add(10, new Triplet<>(10.0f, 45.0f, 45.0f));
            this.add(11, new Triplet<>(5.0f, 50.0f, 45.0f));
            this.add(12, new Triplet<>(5.0f, 50.0f, 45.0f));
            this.add(13, new Triplet<>(5.0f, 50.0f, 45.0f));
            this.add(14, new Triplet<>(5.0f, 50.0f, 45.0f));
            this.add(15, new Triplet<>(5.0f, 50.0f, 45.0f));
        }
    };
}
