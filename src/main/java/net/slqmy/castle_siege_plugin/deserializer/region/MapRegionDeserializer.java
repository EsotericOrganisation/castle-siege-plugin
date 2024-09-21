package net.slqmy.castle_siege_plugin.deserializer.region;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.slqmy.castle_siege_plugin.game.data.region.MapRegion;
import net.slqmy.castle_siege_plugin.deserializer.region.base.AbstractRegionDeserializer;
import net.slqmy.castle_siege_plugin.mobs.data.MobEnvironment;
import org.bukkit.Location;

import java.lang.reflect.Type;

public class MapRegionDeserializer extends AbstractRegionDeserializer<MapRegion> {
    @Override
    public MapRegion deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        return (MapRegion) new MapRegion()
            .setEnvironment(MobEnvironment.fromString(jsonObject.get("environment").getAsString()))
            .setMobCap(jsonObject.get("mob_cap").getAsInt())
            .setMobSpawnRate(jsonObject.get("mob_spawn_rate").getAsDouble())
            .setCorner1(context.deserialize(jsonObject.get("corner_1"), Location.class))
            .setCorner2(context.deserialize(jsonObject.get("corner_2"), Location.class))
            .setIgnoreY(jsonObject.get("ignore_y").getAsBoolean());
    }
}
