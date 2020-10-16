package be.twofold.tinyjson;

final class JsonBoolean extends JsonValue {

    static final JsonBoolean True = new JsonBoolean(true);
    static final JsonBoolean False = new JsonBoolean(false);

    private final boolean value;

    private JsonBoolean(boolean value) {
        this.value = value;
    }


    @Override
    public JsonValue copy() {
        return this;
    }


    @Override
    public boolean asBoolean() {
        return value;
    }


    @Override
    public boolean equals(Object obj) {
        return this == obj || obj instanceof JsonBoolean
            && value == ((JsonBoolean) obj).value;
    }

    @Override
    public int hashCode() {
        return Boolean.hashCode(value);
    }

    @Override
    public String toString() {
        return "JsonBoolean(" + value + ")";
    }

}
