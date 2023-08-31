package net.arcadiasedge.riftseeker.items;

public enum DamageType {
    PHYSICAL("PHYSICAL"),
    RANGED("RANGED"),
    MAGICAL("MAGICAL"),
    TRUE("TRUE");

    public String type;

    DamageType(String type) {
        this.type = type;
    }

    public String getValue() {
        return this.type;
    }

    public static DamageType fromString(String rarity) {
        for (DamageType damageType : DamageType.values()) {
            if (damageType.getValue().equals(rarity)) {
                return damageType;
            }
        }

        return null;
    }
}
