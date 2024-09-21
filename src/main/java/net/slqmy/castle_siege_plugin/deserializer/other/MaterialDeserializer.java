package net.slqmy.castle_siege_plugin.deserializer.other;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import org.bukkit.Material;

import java.lang.reflect.Type;

public final class MaterialDeserializer implements JsonDeserializer<Material> {
    @Override
    public Material deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return Material.getMaterial(json.getAsJsonObject().get("material").getAsString());
    }
}
