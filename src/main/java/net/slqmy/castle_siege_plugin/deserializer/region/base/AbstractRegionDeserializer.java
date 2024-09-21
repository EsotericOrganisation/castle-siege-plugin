package net.slqmy.castle_siege_plugin.deserializer.region.base;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public abstract class AbstractRegionDeserializer<T> implements JsonDeserializer<T> {
    public abstract T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException;
}
