package net.slqmy.castle_siege_plugin.game.data.npc;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.Location;

@Getter @Setter @Accessors(chain = true)
public class NPCData {
    private NPCType type;
    private Location location;
    private String name;
    private String skinValue;
    private String skinSignature;
}
