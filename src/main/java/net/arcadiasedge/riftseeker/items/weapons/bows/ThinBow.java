package net.arcadiasedge.riftseeker.items.weapons.bows;

import net.arcadiasedge.riftseeker.entities.projectiles.GameArrow;
import net.arcadiasedge.riftseeker.items.Item;

public class ThinBow extends Item {
    public ThinBow() {
        super("THIN_BOW");
    }

    @Override
    public void onArrowShoot(GameArrow arrow) {
        System.out.println("Bow arrow shoot");

        super.onArrowShoot(arrow);
    }
}
