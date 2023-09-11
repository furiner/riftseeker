package net.arcadiasedge.riftseeker.players;

import net.arcadiasedge.riftseeker.api.ApiProfile;
import net.arcadiasedge.riftseeker.api.requests.profiles.FetchPlayerProfileRequest;
import net.arcadiasedge.vespera.common.VesperaClient;

import java.io.IOException;
import java.util.UUID;

public class Profile {
    private ApiProfile apiProfile;

    public Profile(ApiProfile self) {
        this.apiProfile = self;

        // Reassign values to this profile.
        this.reassign();
    }

    public ApiProfile getApiProfile() {
        return this.apiProfile;
    }

    public ApiProfile refresh() {
        var req = new FetchPlayerProfileRequest(UUID.fromString(apiProfile.player.uuid), apiProfile.id);

        try {
            var res = VesperaClient.getApiClientStatic().send(req);
            apiProfile = res.data;
        } catch (IOException e) {
            // do nothing
        }

        return apiProfile;
    }

    private void reassign() {

    }
}
