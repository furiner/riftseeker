package net.arcadiasedge.riftseeker.players.statistics;

import net.arcadiasedge.riftseeker.players.GamePlayer;

/**
 * A representation of a player's statistics.
 */
public class PlayerStatistics {
    public GamePlayer player;

    public int health;



    public PlayerStatistics(GamePlayer player) {
        this.player = player;
    }

    /**
     * This is a method that actively applies current statistics to the player, such as health, mana, etc.
     * This is meant to be called whenever any statistic is changed; and should be called automatically
     * by any method that changes a statistic.
     */
    public void apply() {

    }
}
