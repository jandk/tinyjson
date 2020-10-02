package be.twofold.json;

public abstract class JsonValue {

    JsonValue() {
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
