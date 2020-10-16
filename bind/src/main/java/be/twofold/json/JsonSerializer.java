package be.twofold.json;

import be.twofold.tinyjson.*;

@FunctionalInterface
public interface JsonSerializer<T> {

    JsonValue serialize(T object, JsonSerializationContext context);

}
