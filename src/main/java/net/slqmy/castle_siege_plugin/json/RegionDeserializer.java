package net.slqmy.castle_siege_plugin.json;

import com.google.gson.*;
import net.slqmy.castle_siege_plugin.game.data.arena.CastleSiegeTeamBaseData.Region;

import java.lang.reflect.Type;

public class RegionDeserializer implements JsonDeserializer<Region> {
    @Override
    public Region deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        Region region = new Region();
        region.setCorner1(context.deserialize(jsonObject.get("corner_1"), Double[].class));
        region.setCorner2(context.deserialize(jsonObject.get("corner_2"), Double[].class));
        region.setIgnoreY(jsonObject.get("ignore_y").getAsBoolean());

        return region;
    }
}
