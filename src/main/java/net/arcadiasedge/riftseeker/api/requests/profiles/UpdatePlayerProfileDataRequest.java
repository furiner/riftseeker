package net.arcadiasedge.riftseeker.api.requests.profiles;

import com.google.api.client.http.HttpContent;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.json.gson.GsonFactory;
import net.arcadiasedge.riftseeker.api.ApiProfile;
import net.arcadiasedge.vespera.common.api.requests.ApiRequest;
import net.arcadiasedge.vespera.common.api.requests.HttpMethod;

public class UpdatePlayerProfileDataRequest extends ApiRequest<Void> {
    private final ApiProfile profile;
    public UpdatePlayerProfileDataRequest(ApiProfile profile) {
        this.profile = profile;
    }

    @Override
    public HttpContent getContent() {
        return new JsonHttpContent(new GsonFactory(), profile);
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.Put;
    }

    @Override
    public String getUrl() {
        return "/api/v1/riftseeker/profiles/" + profile.player.id + "/" + profile.id + "/data";
    }
}
