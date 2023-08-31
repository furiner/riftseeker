package net.arcadiasedge.riftseeker.api.partials;

import com.google.api.client.util.Key;
import net.arcadiasedge.riftseeker.items.DamageType;

public class ApiItemProperties {
    @Key("damage_type")
    String damageType;

    @Key("max_stack")
    int maxStack;

    public DamageType getDamageType() {
        return DamageType.fromString(damageType);
    }
}
