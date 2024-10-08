package be.twofold.tinyjson;

import be.twofold.tinyjson.read.*;

import java.io.*;

public final class Json {

    public static final JsonValue Null = JsonNull.Null;

    private Json() {
        throw new UnsupportedOperationException();
    }

    // region Factory Methods

    public static JsonValue bool(boolean value) {
        return value ? JsonBoolean.True : JsonBoolean.False;
    }

    public static JsonValue number(long value) {
        return new JsonNumber(value);
    }

    public static JsonValue number(double value) {
        return new JsonNumber(value);
    }

    public static JsonValue number(Number value) {
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

    // endregion

    public static JsonValue parse(Reader reader) {
        return new JsonParser(reader).parse();
    }

    public static JsonValue parse(String json) {
        return parse(new StringReader(json));
    }

}
