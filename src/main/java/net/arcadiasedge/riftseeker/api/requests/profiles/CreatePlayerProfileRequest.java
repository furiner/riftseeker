package net.arcadiasedge.riftseeker.api.requests.profiles;

import net.arcadiasedge.riftseeker.api.ApiProfile;
import net.arcadiasedge.vespera.common.api.requests.ApiRequest;
import net.arcadiasedge.vespera.common.api.requests.HttpMethod;

import java.util.UUID;

public class CreatePlayerProfileRequest extends ApiRequest<ApiProfile> {
    private final UUID playerUuid;

    public CreatePlayerProfileRequest(UUID playerUuid) {
        this.playerUuid = playerUuid;
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.Post;
    }

    @Override
    public String getUrl() {
        return "/api/v1/riftseeker/profiles/" + playerUuid.toString();
    }

    @Override
    public Class<ApiProfile> getResponseClass() {
        return ApiProfile.class;
    }
}
