package net.arcadiasedge.riftseeker.api.partials;

import com.google.api.client.util.Key;

public class ApiAbility {
    @Key
    public String id;

    @Key
    public String name;

    @Key
    public String lore;

    @Key
    public float damage;

    @Key
    public float cost;

    @Key
    public String button;
}
