package net.arcadiasedge.riftseeker.entities;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

/**
 * A game entity backed by a Citizens NPC.
 * @param <E>
 */
public abstract class GameNPCEntity<E extends Entity> extends GameEntity<E> {
    public NPC npc;

    public GameNPCEntity(EntityType type, String name) {
        super(null);

        this.npc = CitizensAPI.getNPCRegistry().createNPC(type, name);
        this.entity = (E) this.npc.getEntity();

    }

    public void spawn(Location location) {
        this.npc.spawn(location);
    }
}
