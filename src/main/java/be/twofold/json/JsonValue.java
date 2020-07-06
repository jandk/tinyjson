package be.twofold.json;

public abstract class JsonValue {

    JsonValue() {
    }


    public static JsonValue jsonNull() {
        return JsonNull.Null;
    }

    public static JsonValue jsonBoolean(boolean value) {
        return value ? JsonBoolean.True : JsonBoolean.False;
    }

    public static JsonValue jsonNumber(String value) {
        return new JsonNumber(value);
    }

    public static JsonValue jsonNumber(long value) {
        return new JsonNumber(value);
    }

    public static JsonValue jsonNumber(double value) {
        return new JsonNumber(value);
    }

    public static JsonValue jsonString(String value) {
        return new JsonString(value);
    }

    public static JsonArray jsonArray() {
        return new JsonArray();
    }

    public static JsonObject jsonObject() {
        return new JsonObject();
    }


    public boolean isNull() {
        return false;
    }

    public boolean isBoolean() {
        return false;
    }

    public boolean isNumber() {
        return false;
    }

    public boolean isString() {
        return false;
    }

    public boolean isArray() {
        return false;
    }

    public boolean isObject() {
        return false;
    }


    public boolean asBoolean() {
        throw ex("Boolean");
    }

    public Number asNumber() {
        throw ex("Number");
    }

    public String asString() {
        throw ex("String");
    }

    public JsonArray asArray() {
        throw ex("Array");
    }

    public JsonObject asObject() {
        throw ex("Object");
    }


    private RuntimeException ex(String type) {
        return new IllegalStateException("Value is not of type " + type);
    }

}
