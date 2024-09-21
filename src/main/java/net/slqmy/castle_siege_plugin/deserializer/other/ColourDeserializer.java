package net.slqmy.castle_siege_plugin.deserializer.other;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import net.kyori.adventure.text.format.NamedTextColor;

import java.lang.reflect.Type;

public final class ColourDeserializer implements JsonDeserializer<NamedTextColor> {
    @Override
    public NamedTextColor deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return NamedTextColor.NAMES.value(json.getAsString());
    }
}
