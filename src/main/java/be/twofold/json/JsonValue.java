package be.twofold.json;

public abstract class JsonValue {

    JsonValue() {
    }


    public final boolean isNull() {
        return this instanceof JsonNull;
    }

    public final boolean isBoolean() {
        return this instanceof JsonBoolean;
    }

    public final boolean isNumber() {
        return this instanceof JsonNumber;
    }

    public final boolean isString() {
        return this instanceof JsonString;
    }

    public final boolean isArray() {
        return this instanceof JsonArray;
    }

    public final boolean isObject() {
        return this instanceof JsonObject;
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
