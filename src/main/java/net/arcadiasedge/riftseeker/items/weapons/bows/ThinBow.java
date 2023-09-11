package net.arcadiasedge.riftseeker.items.weapons.bows;

import net.arcadiasedge.riftseeker.entities.projectiles.ArrowEntity;
import net.arcadiasedge.riftseeker.items.Item;

public class ThinBow extends Item {
    public ThinBow() {
        super("THIN_BOW");
    }

    @Override
    public void onArrowShoot(ArrowEntity arrow) {
        System.out.println("Bow arrow shoot");

        super.onArrowShoot(arrow);
    }
}
