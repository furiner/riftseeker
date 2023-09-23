package net.arcadiasedge.riftseeker.api;

import com.google.api.client.util.Key;
import com.google.gson.annotations.SerializedName;
import net.arcadiasedge.riftseeker.api.partials.ApiAbility;
import net.arcadiasedge.riftseeker.api.partials.ApiItemProperties;
import net.arcadiasedge.riftseeker.api.partials.ApiItemRestrictions;
import net.arcadiasedge.riftseeker.api.partials.ApiSetEffect;
import net.arcadiasedge.riftseeker.api.requests.items.FetchItemRequest;
import net.arcadiasedge.riftseeker.items.ItemType;
import net.arcadiasedge.riftseeker.items.Rarity;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ApiItem extends ApiModel {
    public String name;

    public String material;

    String rarity;

    public String lore;

    @SerializedName("type")
    public String type;

    public String kind;

    @SerializedName("set_effect")

    @Nullable
    public ApiSetEffect setEffect;

    public Map<String, Float> attributes;

    public List<ApiAbility> abilities;

    public ApiItemProperties properties;

    public ApiItemRestrictions restrictions;

    public static ApiItem fetch(String id) throws IOException {
        return ApiModel.send(new FetchItemRequest(id));
    }

    public Rarity getRarity() {
        return Rarity.fromString(rarity);
    }

    public ItemType getType() {
        return ItemType.fromString(type);
    }

    public int getMaxStack() {
        return properties.maxStack;
    }
}
