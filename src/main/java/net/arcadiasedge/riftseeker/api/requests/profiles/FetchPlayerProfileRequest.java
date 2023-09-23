package net.arcadiasedge.riftseeker.api.requests.profiles;

import net.arcadiasedge.riftseeker.api.ApiProfile;
import net.arcadiasedge.vespera.common.api.requests.ApiRequest;
import net.arcadiasedge.vespera.common.api.requests.HttpMethod;

import java.util.UUID;

public class FetchPlayerProfileRequest extends ApiRequest<ApiProfile> {
    private final UUID playerUuid;
    private final String profileId;

    public FetchPlayerProfileRequest(UUID playerUuid) {
        super(HttpMethod.Get, "/api/v1/riftseeker/profiles/" + playerUuid.toString() + "?active=true");

        this.playerUuid = playerUuid;
        this.profileId = null;
    }

    public FetchPlayerProfileRequest(UUID playerUuid, String profileId) {
        super(HttpMethod.Get, "/api/v1/riftseeker/profiles/" + playerUuid.toString() + "/" + profileId);

        this.playerUuid = playerUuid;
        this.profileId = profileId;
    }

    @Override
    public Class<ApiProfile> getResponseClass() {
        return ApiProfile.class;
    }

}
