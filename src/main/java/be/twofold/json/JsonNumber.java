package be.twofold.json;

import java.math.*;
import java.util.*;

final class JsonNumber extends JsonValue {

    private final Number value;

    JsonNumber(Number value) {
        this.value = value;
    }

    JsonNumber(String value) {
        this.value = new LazilyParsedNumber(value);
    }


    @Override
    public boolean isNumber() {
        return true;
    }

    @Override
    public Number asNumber() {
        return value;
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

    static final class LazilyParsedNumber extends Number {
        private final String value;

        LazilyParsedNumber(String value) {
            this.value = Objects.requireNonNull(value);
        }

        @Override
        public int intValue() {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                return (int) longValue();
            }
        }

        @Override
        public long longValue() {
            try {
                return Long.parseLong(value);
            } catch (NumberFormatException e) {
                return new BigDecimal(value).longValue();
            }
        }

        @Override
        public float floatValue() {
            return Float.parseFloat(value);
        }

        @Override
        public double doubleValue() {
            return Double.parseDouble(value);
        }

        @Override
        public boolean equals(Object obj) {
            return this == obj || obj instanceof LazilyParsedNumber
                && value.equals(((LazilyParsedNumber) obj).value);
        }

        @Override
        public int hashCode() {
            return value.hashCode();
        }

        @Override
        public String toString() {
            return value;
        }
    }
}
