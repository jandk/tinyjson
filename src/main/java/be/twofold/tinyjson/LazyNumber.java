package be.twofold.tinyjson;

import java.math.*;
import java.util.*;

final class LazyNumber extends Number {

    private final String value;

    LazyNumber(String value) {
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
        return this == obj || obj instanceof LazyNumber
            && value.equals(((LazyNumber) obj).value);
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
