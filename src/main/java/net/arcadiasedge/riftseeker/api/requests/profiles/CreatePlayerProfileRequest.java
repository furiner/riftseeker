package net.arcadiasedge.riftseeker.api.requests.profiles;

import net.arcadiasedge.riftseeker.api.ApiProfile;
import net.arcadiasedge.vespera.common.api.requests.ApiRequest;
import net.arcadiasedge.vespera.common.api.requests.HttpMethod;

import java.util.UUID;

public class CreatePlayerProfileRequest extends ApiRequest<ApiProfile> {
    private final UUID playerUuid;

    public CreatePlayerProfileRequest(UUID playerUuid) {
        super(HttpMethod.Post, "/api/v1/riftseeker/profiles/" + playerUuid.toString());

        this.playerUuid = playerUuid;
    }

    @Override
    public Class<ApiProfile> getResponseClass() {
        return ApiProfile.class;
    }
}
