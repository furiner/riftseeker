package net.arcadiasedge.riftseeker.api.requests.profiles;

import com.google.api.client.http.HttpResponse;
import com.google.api.client.json.gson.GsonFactory;
import net.arcadiasedge.riftseeker.api.ApiProfile;
import net.arcadiasedge.vespera.common.api.requests.ApiRequest;
import net.arcadiasedge.vespera.common.api.requests.HttpMethod;
import net.arcadiasedge.vespera.common.api.requests.IntermediaryResponse;
import net.arcadiasedge.vespera.common.api.responses.ApiResponse;

import java.io.IOException;
import java.util.UUID;

public class FetchPlayerProfileRequest extends ApiRequest<ApiProfile> {
    private final UUID playerUuid;
    private final String profileId;

    public FetchPlayerProfileRequest(UUID playerUuid) {
        this.playerUuid = playerUuid;
        this.profileId = null;
    }

    public FetchPlayerProfileRequest(UUID playerUuid, String profileId) {
        this.playerUuid = null;
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

    @Override
    public ApiResponse<ApiProfile> parseResponse(HttpResponse response) throws IOException {
        var intermediary = response.parseAs(IntermediaryResponse.class);
        var gson = new GsonFactory();
        var responseClass = getResponseClass();

        var apiResponse = new ApiResponse<ApiProfile>();
        apiResponse.status = intermediary.status;
        apiResponse.message = intermediary.message;

        if (responseClass == null) {
            apiResponse.data = null;
        } else {
            System.out.println(gson.toString(intermediary.data));
            apiResponse.data = gson.fromString(gson.toString(intermediary.data), responseClass);
        }

        return apiResponse;
    };
}
