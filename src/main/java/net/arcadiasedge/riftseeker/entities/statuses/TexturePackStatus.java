package net.arcadiasedge.riftseeker.entities.statuses;

import org.bukkit.event.player.PlayerResourcePackStatusEvent;

public enum TexturePackStatus {
    Loading,
    Loaded,
    Failed,
    Disabled;

    public static final TexturePackStatus fromStatus(PlayerResourcePackStatusEvent.Status status) {
        return switch (status) {
            default -> Failed;
            case ACCEPTED, SUCCESSFULLY_LOADED -> Loaded;
            case DECLINED -> Disabled;
        };
    }
}
