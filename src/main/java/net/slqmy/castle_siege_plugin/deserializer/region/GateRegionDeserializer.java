package net.slqmy.castle_siege_plugin.deserializer.region;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.slqmy.castle_siege_plugin.game.data.region.GateRegion;
import net.slqmy.castle_siege_plugin.deserializer.region.base.AbstractRegionDeserializer;
import org.bukkit.Location;

import java.lang.reflect.Type;

public class GateRegionDeserializer extends AbstractRegionDeserializer<GateRegion> {
    @Override
    public GateRegion deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        return new GateRegion()
            .setCorner1(context.deserialize(jsonObject.get("corner_1"), Location.class))
            .setCorner2WhenOpen(context.deserialize(jsonObject.get("corner_2_when_open"), Location.class))
            .setCorner2WhenClosed(context.deserialize(jsonObject.get("corner_2_when_closed"), Location.class));
    }
}
