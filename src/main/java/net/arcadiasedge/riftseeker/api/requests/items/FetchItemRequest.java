package net.arcadiasedge.riftseeker.api.requests.items;

import net.arcadiasedge.riftseeker.api.ApiItem;
import net.arcadiasedge.vespera.common.api.requests.ApiRequest;
import net.arcadiasedge.vespera.common.api.requests.HttpMethod;

public class FetchItemRequest extends ApiRequest<ApiItem> {
    private final String id;

    public FetchItemRequest(String id) {
        super(HttpMethod.Get, "/api/v1/riftseeker/items/" + id);

        this.id = id;
    }

    @Override
    public Class<ApiItem> getResponseClass() {
        return ApiItem.class;
    }
}
