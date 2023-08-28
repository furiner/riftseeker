package net.arcadiasedge.riftseeker.api;

import com.google.api.client.util.Key;
import net.arcadiasedge.vespera.common.VesperaClient;
import net.arcadiasedge.vespera.common.api.requests.ApiRequest;

import java.io.IOException;

public abstract class ApiModel {
    @Key
    public String id;

    public static <T> T send(ApiRequest<T> request) throws IOException {
        var client = VesperaClient.getApiClientStatic();

        if (client == null) {
            throw new IOException("API client is not initialized");
        }

        var response = client.send(request);

        if (response.isSuccess()) {
            return response.data;
        } else {
            throw new IOException("Failed to fetch player profile.");
        }
    }
}
