package be.twofold.tinyjson.parser;

import be.twofold.tinyjson.*;

import java.util.*;

public final class Tokenizer {

    private final PeekingReader reader;
    private final StringBuilder builder = new StringBuilder();
    private Token token = new Token();

    public Tokenizer(PeekingReader reader) {
        this.reader = Objects.requireNonNull(reader, "reader");
    }

    public Token getToken() {
        return token;
    }

    public void nextToken() {
        skipWhitespace();
        switch (reader.peek()) {
            case '{':
                reader.read();
                token(TokenType.ObjectStart, null);
                break;
            case '}':
                reader.read();
                token(TokenType.ObjectEnd, null);
                break;
            case '[':
                reader.read();
                token(TokenType.ArrayStart, null);
                break;
            case ']':
                reader.read();
                token(TokenType.ArrayEnd, null);
                break;
            case ':':
                reader.read();
                token(TokenType.Colon, null);
                break;
            case ',':
                reader.read();
                token(TokenType.Comma, null);
                break;
            case '"':
                token(TokenType.String, parseString());
                break;
            case '-':
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                token(TokenType.Number, parseNumber());
                break;
            case 't':
                token(TokenType.True, expect("true"));
                break;
            case 'f':
                token(TokenType.False, expect("false"));
                break;
            case 'n':
                token(TokenType.Null, expect("null"));
                break;
            case -1:
                token(TokenType.Eof, null);
                break;
            default:
                throw new JsonException("Unexpected character '" + (char) reader.peek() + "'");
        }
    }

    private void token(TokenType type, String value) {
        this.token.setType(type);
        this.token.setValue(value);
    }

    private String parseString() {
        // skip leading '"'
        reader.read();
        builder.setLength(0);
        while (!reader.isEof() && reader.peek() != '"') {
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
            if (!isHexDigit(ch)) {
                throw new JsonException("Expected hex digit");
            }
            result = (result << 4) | Character.digit(ch, 16);
        }
        return (char) result;
    }


    private String parseNumber() {
        builder.setLength(0);

        if (reader.peek() == '-') {
            appendNext();
        }

        // integer part
        if (reader.peek() == '0') {
            appendNext();
        } else {
            if (reader.peek() < '1' || reader.peek() > '9') {
                throw new JsonException();
            }
            digits();
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

    private boolean isWhitespace(int c) {
        switch (c) {
            case ' ':
            case '\n':
            case '\r':
            case '\t':
                return true;
            default:
                return false;
        }
    }

    private boolean isDigit(int c) {
        return c >= '0' && c <= '9';
    }

    private boolean isHexDigit(int c) {
        return isDigit(c)
            || c >= 'a' && c <= 'f'
            || c >= 'A' && c <= 'F';
    }

}
