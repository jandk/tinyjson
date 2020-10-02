package be.twofold.json;

import java.util.*;

public final class JsonObject extends JsonValue {

    private final Map<String, JsonValue> values;

    JsonObject() {
        this(new HashMap<>());
    }

    public JsonObject(Map<String, JsonValue> values) {
        this.values = Objects.requireNonNull(values, "values");
    }


    @Override
    public JsonObject asObject() {
        return this;
    }


    @Override
    public boolean equals(Object obj) {
        return this == obj || obj instanceof JsonObject
            && values.equals(((JsonObject) obj).values);
    }

    @Override
    public int hashCode() {
        return values.hashCode();
    }

    @Override
    public String toString() {
        return "JsonObject(" + values + ")";
    }

}
