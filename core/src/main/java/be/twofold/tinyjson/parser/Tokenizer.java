package be.twofold.tinyjson.parser;

import be.twofold.tinyjson.*;

import java.util.*;

public final class Tokenizer {

    private final PeekingReader reader;
    private final StringBuilder builder = new StringBuilder();
    private Token current;

    public Tokenizer(PeekingReader reader) {
        this.reader = Objects.requireNonNull(reader, "reader");
    }

    public Token peek() {
        if (current == null) {
            current = nextToken();
        }
        return current;
    }

    public Token read() {
        if (current == null) {
            return nextToken();
        }
        Token result = current;
        current = null;
        return result;
    }

    private Token nextToken() {
        skipWhitespace();
        switch (reader.peek()) {
            case '{':
                reader.read();
                return new Token(TokenType.ObjectStart, null);
            case '}':
                reader.read();
                return new Token(TokenType.ObjectEnd, null);
            case '[':
                reader.read();
                return new Token(TokenType.ArrayStart, null);
            case ']':
                reader.read();
                return new Token(TokenType.ArrayEnd, null);
            case ':':
                reader.read();
                return new Token(TokenType.Colon, null);
            case ',':
                reader.read();
                return new Token(TokenType.Comma, null);
            case '"':
                return new Token(TokenType.String, parseString());
            case 't':
                return new Token(TokenType.True, expect("true"));
            case 'f':
                return new Token(TokenType.False, expect("false"));
            case 'n':
                return new Token(TokenType.Null, expect("null"));
            case -1:
                return new Token(TokenType.Eof, null);
            default:
                int ch = reader.peek();
                if (isDigit(ch) || ch == '-') {
                    return new Token(TokenType.Number, number());
                }
                throw new JsonException("Unexpected character '" + (char) ch + "'");
        }
    }

    private String parseString() {
        // skip leading '"'
        reader.read();
        builder.setLength(0);
        while (reader.peek() != '"' && !reader.isEof()) {
            if (reader.peek() < 0x20) {
                throw new JsonException("Raw control character");
            }

            int ch = reader.read();
            builder.append(ch == '\\' ? escape() : (char) ch);
        }
        if (reader.read() != '"') {
            throw new JsonException("Unclosed string literal");
        }
        return builder.toString();
    }

    private char escape() {
        switch (reader.read()) {
            case '"':
                return '"';
            case '\\':
                return '\\';
            case '/':
                return '/';
            case 'b':
                return '\b';
            case 'f':
                return '\f';
            case 'n':
                return '\n';
            case 'r':
                return '\r';
            case 't':
                return '\t';
            case 'u':
                return escapeUnicode();
            default:
                throw new JsonException("Illegal escape");
        }
    }

    private char escapeUnicode() {
        int result = 0;
        for (int i = 0; i < 4; i++) {
            int ch = reader.read();
            if (!isHex(ch)) {
                throw new JsonException("Expected hex digit");
            }
            result = (result << 4) | Character.digit(ch, 16);
        }
        return (char) result;
    }


    private String number() {
        builder.setLength(0);

        // sign
        if (reader.peek() == '-') {
            appendNext();
        }

        // integer part
        if (reader.peek() == '0') {
            appendNext();
        } else if (isDigit(reader.peek())) {
            digits();
        } else {
            throw new JsonException("Expected a digit");
        }

        // decimal part
        if (reader.peek() == '.') {
            appendNext();
            digits();
        }

        // exponent part
        if (reader.peek() == 'e' || reader.peek() == 'E') {
            appendNext();
            if (reader.peek() == '-' || reader.peek() == '+') {
                appendNext();
            }
            digits();
        }

        return builder.toString();
    }

    private void digits() {
        if (!isDigit(reader.peek())) {
            throw new JsonException("Expected a digit");
        }
        while (isDigit(reader.peek())) {
            appendNext();
        }
    }


    private String expect(String expected) {
        for (int i = 0; i < expected.length(); i++) {
            if (reader.read() != expected.charAt(i)) {
                throw new JsonException("Expected '" + expected + "' literal");
            }
        }
        return expected;
    }

    private void appendNext() {
        builder.append(reader.read());
    }

    private void skipWhitespace() {
        while (isWhitespace(reader.peek())) {
            reader.read();
        }
    }

    private boolean isWhitespace(int ch) {
        return ch == ' ' || ch == '\n' || ch == '\r' || ch == '\t';
    }

    private boolean isDigit(int ch) {
        return ch >= '0' && ch <= '9';
    }

    private boolean isHex(int ch) {
        return isDigit(ch)
            || ch >= 'a' && ch <= 'f'
            || ch >= 'A' && ch <= 'F';
    }

}
