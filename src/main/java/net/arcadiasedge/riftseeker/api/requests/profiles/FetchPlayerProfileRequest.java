package net.arcadiasedge.riftseeker.api.requests.profiles;

import net.arcadiasedge.riftseeker.api.ApiProfile;
import net.arcadiasedge.vespera.common.api.requests.ApiRequest;
import net.arcadiasedge.vespera.common.api.requests.HttpMethod;

import java.util.UUID;

public class FetchPlayerProfileRequest extends ApiRequest<ApiProfile> {
    private final UUID playerUuid;
    private final String profileId;

    public FetchPlayerProfileRequest(UUID playerUuid) {
        this.playerUuid = playerUuid;
        this.profileId = null;
    }

    public FetchPlayerProfileRequest(UUID playerUuid, String profileId) {
        this.playerUuid = playerUuid;
        this.profileId = profileId;
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.Get;
    }

    @Override
    public String getUrl() {
        if (profileId != null) {
            return "/api/v1/riftseeker/profiles/" + playerUuid.toString() + "/" + profileId;
        } else {
            return "/api/v1/riftseeker/profiles/" + playerUuid.toString() + "?active=true";
        }
    }

    @Override
    public Class<ApiProfile> getResponseClass() {
        return ApiProfile.class;
    }

}
