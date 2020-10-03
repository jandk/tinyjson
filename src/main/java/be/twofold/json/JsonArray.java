package be.twofold.json;

import java.util.*;

public final class JsonArray extends JsonValue implements Iterable<JsonValue> {

    private final List<JsonValue> values;

    JsonArray() {
        this(new ArrayList<>());
    }

    public JsonArray(List<JsonValue> values) {
        this.values = Objects.requireNonNull(values, "values");
    }


    @Override
    public Iterator<JsonValue> iterator() {
        return values.iterator();
    }


    @Override
    public JsonValue copy() {
        List<JsonValue> values = new ArrayList<>();
        for (JsonValue value : this.values) {
            values.add(value.copy());
        }
        return new JsonArray(values);
    }


    @Override
    public JsonArray asArray() {
        return this;
    }


    // region Accessors

    public int size() {
        return values.size();
    }

    public JsonValue get(int index) {
        return values.get(index);
    }

    public boolean getBoolean(int index) {
        return values.get(index).asBoolean();
    }

    public byte getByte(int index) {
        return values.get(index).asByte();
    }

    public short getShort(int index) {
        return values.get(index).asShort();
    }

    public int getInt(int index) {
        return values.get(index).asInt();
    }

    public long getLong(int index) {
        return values.get(index).asLong();
    }

    public float getFloat(int index) {
        return values.get(index).asFloat();
    }

    public double getDouble(int index) {
        return values.get(index).asDouble();
    }

    public Number getNumber(int index) {
        return values.get(index).asNumber();
    }

    public String getString(int index) {
        return values.get(index).asString();
    }

    public JsonArray getArray(int index) {
        return values.get(index).asArray();
    }

    public JsonObject getObject(int index) {
        return values.get(index).asObject();
    }

    // endregion

    // region Mutators

    public JsonArray add(Boolean value) {
        values.add(value == null ? Json.Null : Json.bool(value));
        return this;
    }

    public JsonArray add(Number value) {
        values.add(value == null ? Json.Null : Json.number(value));
        return this;
    }

    public JsonArray add(String value) {
        values.add(value == null ? Json.Null : Json.string(value));
        return this;
    }

    public JsonArray add(JsonValue value) {
        values.add(value == null ? Json.Null : value);
        return this;
    }

    public JsonValue set(int index, JsonValue value) {
        return values.set(index, value);
    }

    public JsonValue remove(int index) {
        return values.remove(index);
    }

    public boolean remove(JsonValue value) {
        return values.remove(value);
    }

    // endregion


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
        return "JsonArray(" + values + ")";
    }

}
