package be.twofold.json;

public final class Json {

    private Json() {
        throw new UnsupportedOperationException();
    }

    public static JsonValue nul() {
        return JsonNull.Null;
    }

    public static JsonValue bool(boolean value) {
        return value ? JsonBoolean.True : JsonBoolean.False;
    }

    public static JsonValue number(String value) {
        return new JsonNumber(value);
    }

    public static JsonValue number(long value) {
        return new JsonNumber(value);
    }

    public static JsonValue number(double value) {
        return new JsonNumber(value);
    }

    public static JsonValue string(String value) {
        return new JsonString(value);
    }

    public static JsonArray array() {
        return new JsonArray();
    }

    public static JsonObject object() {
        return new JsonObject();
    }

}
