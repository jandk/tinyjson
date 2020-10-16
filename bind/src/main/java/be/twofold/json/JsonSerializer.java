package be.twofold.json;

import be.twofold.tinyjson.*;

public interface JsonSerializer<T> {

    JsonValue serialize(T object, JsonSerializationContext context);

}
