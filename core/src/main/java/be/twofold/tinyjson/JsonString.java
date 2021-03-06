package be.twofold.tinyjson;

import java.util.*;

final class JsonString extends JsonValue {

    private final String value;

    JsonString(String value) {
        this.value = Objects.requireNonNull(value, "value");
    }


    @Override
    public JsonValue copy() {
        return this;
    }


    @Override
    public String asString() {
        return value;
    }


    @Override
    public boolean equals(Object obj) {
        return this == obj || obj instanceof JsonString
            && value.equals(((JsonString) obj).value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return "JsonString('" + value + "')";
    }

}
