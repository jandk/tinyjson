package be.twofold.tinyjson;

final class JsonNull extends JsonValue {

    static final JsonNull Null = new JsonNull();

    private JsonNull() {
    }


    @Override
    public JsonValue copy() {
        return this;
    }


    @Override
    public boolean equals(Object obj) {
        return obj instanceof JsonNull;
    }

    @Override
    public int hashCode() {
        return 1;
    }

    @Override
    public String toString() {
        return "JsonNull()";
    }

}
