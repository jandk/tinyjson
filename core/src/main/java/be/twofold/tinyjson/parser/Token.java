package be.twofold.tinyjson.parser;

import java.util.*;

public final class Token {
    private final TokenType type;
    private final String value;

    public Token(TokenType type, String value) {
        this.type = Objects.requireNonNull(type, "type");
        this.value = value;
    }

    public TokenType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Token)) return false;

        Token other = (Token) obj;
        return type == other.type
            && Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + type.hashCode();
        result = 31 * result + Objects.hashCode(value);
        return result;
    }

    @Override
    public String toString() {
        return type + "(" + value + ")";
    }
}
