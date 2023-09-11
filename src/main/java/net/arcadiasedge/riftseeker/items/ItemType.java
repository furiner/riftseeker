package net.arcadiasedge.riftseeker.items;

public enum ItemType {
    ITEM("ITEM"),
    WEAPON("WEAPON"),
    EQUIPMENT("EQUIPMENT"),

    STAFF("STAFF"),
    SWORD("SWORD"),
    BOW("BOW"),
    HAMMER("HAMMER"),

    HELMET("HELMET"),
    CHESTPLATE("CHESTPLATE"),
    LEGGINGS("LEGGINGS"),
    BOOTS("BOOTS");

    private final String value;

    ItemType(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public boolean isItem() {
        return this == ItemType.ITEM || this.isWeapon();
    }

    public boolean isWeapon() {
        return this == ItemType.WEAPON || this == ItemType.BOW || this == ItemType.SWORD;
    }

    public boolean isMeleeWeapon() {
        return this == ItemType.SWORD || this == ItemType.STAFF || this == ItemType.HAMMER;
    }

    public boolean isRangedWeapon() {
        return this == ItemType.BOW;
    }

    public boolean isEquipment() {
        return this == ItemType.EQUIPMENT || this.isArmor();
    }

    public boolean isArmor() {
        return this == ItemType.HELMET || this == ItemType.CHESTPLATE || this == ItemType.LEGGINGS || this == ItemType.BOOTS;
    }

    public static ItemType fromString(String rarity) {
        for (ItemType itemType : ItemType.values()) {
            if (itemType.getValue().equals(rarity)) {
                return itemType;
            }
        }

        return null;
    }
}
