package net.arcadiasedge.riftseeker.data;

import org.bukkit.persistence.PersistentDataType;
import java.util.UUID;

public class RiftseekerDataTypes {
    public static final PersistentDataType<byte[], UUID> UUID = new UUIDDataType();
}
