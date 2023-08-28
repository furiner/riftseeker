package net.arcadiasedge.riftseeker.api.requests.items;

import net.arcadiasedge.riftseeker.api.ApiItem;
import net.arcadiasedge.vespera.common.api.requests.ApiRequest;
import net.arcadiasedge.vespera.common.api.requests.HttpMethod;

public class FetchItemRequest extends ApiRequest<ApiItem> {
    private final String id;

    public FetchItemRequest(String id) {
        this.id = id;
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.Get;
    }

    @Override
    public String getUrl() {
        return "/api/v1/riftseeker/items/" + this.id;
    }

    @Override
    public Class<ApiItem> getResponseClass() {
        return ApiItem.class;
    }
}
