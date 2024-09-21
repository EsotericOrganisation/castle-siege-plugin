package net.slqmy.castle_siege_plugin.deserializer.other;

import com.google.gson.*;
import org.bukkit.Location;

import java.lang.reflect.Type;

public final class LocationDeserializer implements JsonDeserializer<Location> {

    @Override
    public Location deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonArray array = json.getAsJsonArray();

        return new Location(
            null,
            array.get(0).getAsDouble(),
            array.get(1).getAsDouble(),
            array.get(2).getAsDouble(),
            (float) getOrDefault(array, 3),
            (float) getOrDefault(array, 4));
    }

    private double getOrDefault(JsonArray array, int index) {
        return array.size() > index ? array.get(index).getAsDouble() : 0.0F;
    }
}
