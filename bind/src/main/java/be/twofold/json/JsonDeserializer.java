package be.twofold.json;

import be.twofold.tinyjson.*;

@FunctionalInterface
public interface JsonDeserializer<T> {

    T deserialize(JsonValue value, JsonDeserializationContext context);

}
