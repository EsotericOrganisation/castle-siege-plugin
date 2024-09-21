package net.slqmy.castle_siege_plugin.items;

import lombok.Getter;

@Getter
public enum ItemIdentifier {
    LONGBOW("longbow"),
    RECURVE_BOW("recurve_bow");

    private final String asString;
    ItemIdentifier(String identifier) {
        this.asString = identifier;
    }
}
