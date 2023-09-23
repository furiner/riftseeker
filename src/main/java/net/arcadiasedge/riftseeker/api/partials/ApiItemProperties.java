package net.arcadiasedge.riftseeker.api.partials;

import com.google.api.client.util.Key;
import com.google.gson.annotations.SerializedName;
import net.arcadiasedge.riftseeker.items.DamageType;

public class ApiItemProperties {
    @SerializedName("damage_type")
    public String damageType;

    @SerializedName("max_stack")
    public int maxStack;

    public boolean grantable;

    public boolean enchantable;

    public DamageType getDamageType() {
        System.out.println(damageType);
        return DamageType.fromString(damageType);
    }
}
