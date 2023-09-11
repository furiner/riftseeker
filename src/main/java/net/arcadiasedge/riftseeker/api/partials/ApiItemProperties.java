package net.arcadiasedge.riftseeker.api.partials;

import com.google.api.client.util.Key;
import net.arcadiasedge.riftseeker.items.DamageType;

public class ApiItemProperties {
    @Key("damage_type")
    public String damageType;

    @Key("max_stack")
    public int maxStack;

    public DamageType getDamageType() {
        return DamageType.fromString(damageType);
    }
}
