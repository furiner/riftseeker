package net.arcadiasedge.riftseeker.entities.projectiles;

import net.arcadiasedge.riftseeker.entities.GameEntity;
import net.arcadiasedge.riftseeker.entities.players.GamePlayer;
import net.arcadiasedge.riftseeker.items.Item;
import org.bukkit.entity.Arrow;
import org.bukkit.projectiles.ProjectileSource;

public class GameArrow extends GameEntity<Arrow> {
    private GameEntity<?> owner;

    private Item weapon;

    public GameArrow(GameEntity<?> owner, Arrow entity, Item weapon) {
        super(entity);

        this.owner = owner;
        this.weapon = weapon;

        this.getEntity().setShooter((ProjectileSource) owner.getEntity());
    }

    public GameEntity<?> getOwner() {
        return this.owner;
    }

    public Item getWeapon() {
        return this.weapon;
    }

    @Override
    public void update() {

    }

    @Override
    public void setup() {

    }
}
