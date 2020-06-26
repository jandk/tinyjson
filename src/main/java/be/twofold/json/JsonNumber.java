package be.twofold.json;

final class JsonNumber extends JsonValue {

    private final Number value;

    JsonNumber(Number value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj instanceof JsonNumber
            && value.equals(((JsonNumber) obj).value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return "JsonNumber(" + value + ")";
    }

}
