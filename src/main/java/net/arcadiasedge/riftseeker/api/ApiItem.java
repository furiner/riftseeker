package net.arcadiasedge.riftseeker.api;

import com.google.api.client.util.Key;
import net.arcadiasedge.riftseeker.api.partials.ApiAbility;
import net.arcadiasedge.riftseeker.api.requests.items.FetchItemRequest;
import net.arcadiasedge.riftseeker.items.Rarity;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ApiItem extends ApiModel {
    @Key
    public String name;

    @Key
    public String material;

    @Key
    String rarity;

    @Key
    public String lore;

    @Key("type")
    public String type;

    @Key
    public Map<String, Integer> attributes;

    @Key
    public List<ApiAbility> abilities;

    public static ApiItem fetch(String id) throws IOException {
        return ApiModel.send(new FetchItemRequest(id));
    }

    public Rarity getRarity() {
        return Rarity.fromString(rarity);
    }
}
