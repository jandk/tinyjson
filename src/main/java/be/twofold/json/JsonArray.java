package be.twofold.json;

import java.util.*;

public final class JsonArray extends JsonValue {

    private final List<JsonValue> values;

    JsonArray(List<JsonValue> values) {
        this.values = Objects.requireNonNull(values, "values");
    }


    @Override
    public boolean isArray() {
        return true;
    }


    @Override
    public boolean equals(Object obj) {
        return this == obj || obj instanceof JsonArray
            && values.equals(((JsonArray) obj).values);
    }

    @Override
    public int hashCode() {
        return values.hashCode();
    }

    @Override
    public String toString() {
        return "JsonArray(" + values.size() + "items)";
    }

}
