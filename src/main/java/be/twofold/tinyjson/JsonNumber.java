package be.twofold.tinyjson;

final class JsonNumber extends JsonValue {

    private final Number value;

    JsonNumber(Number value) {
        this.value = value;
    }

    JsonNumber(String value) {
        this.value = new LazyNumber(value);
    }


    @Override
    public JsonValue copy() {
        return this;
    }


    @Override
    public Number asNumber() {
        return value;
    }

    @Override
    public byte asByte() {
        return value.byteValue();
    }

    @Override
    public short asShort() {
        return value.shortValue();
    }

    @Override
    public int asInt() {
        return value.intValue();
    }

    @Override
    public long asLong() {
        return value.longValue();
    }

    @Override
    public float asFloat() {
        return value.floatValue();
    }

    @Override
    public double asDouble() {
        return value.doubleValue();
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
