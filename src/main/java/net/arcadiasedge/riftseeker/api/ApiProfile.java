package net.arcadiasedge.riftseeker.api;

import com.google.api.client.util.Key;
import net.arcadiasedge.riftseeker.api.partials.ApiInventoryData;
import net.arcadiasedge.riftseeker.api.partials.ApiSkill;
import net.arcadiasedge.riftseeker.api.requests.profiles.CreatePlayerProfileRequest;
import net.arcadiasedge.riftseeker.api.requests.profiles.FetchPlayerProfileRequest;
import net.arcadiasedge.vespera.common.VesperaClient;
import net.arcadiasedge.vespera.common.api.models.ApiPlayer;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public class ApiProfile extends ApiModel {
    public Number level;

    public Number xp;

    public ApiPlayer player;

    public Map<String, ApiSkill> skills;

    public Map<String, ApiInventoryData> inventory;

    public static ApiProfile fetch(UUID uuid) throws IOException {
        var client = VesperaClient.getApiClientStatic();

        if (client == null) {
            throw new IOException("API client is not initialized");
        }

        var request = new FetchPlayerProfileRequest(uuid);
        var response = client.send(request);

        if (response.isSuccess()) {

            return response.data;
        } else {
            throw new IOException("Failed to fetch player profile.");
        }
    }

    public static ApiProfile create(UUID uuid) throws IOException {
        var client = VesperaClient.getApiClientStatic();

        if (client == null) {
            throw new IOException("API client is not initialized");
        }

        var request = new CreatePlayerProfileRequest(uuid);
        var response = client.send(request);

        if (response.isSuccess()) {
            return response.data;
        } else {
            throw new IOException("Failed to create player profile.");
        }
    }

    public static ApiProfile fetchOrCreate(UUID uuid) {
        try {
            return fetch(uuid);
        } catch (IOException e) {
            try {
                return create(uuid);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
