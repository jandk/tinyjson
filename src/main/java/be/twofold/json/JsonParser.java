package be.twofold.json;

import java.io.*;
import java.util.*;

public class JsonParser {

    private static final char EOF = 0x1A;

    private final Reader reader;
    private char peek;

    public JsonParser(Reader reader) {
        this.reader = Objects.requireNonNull(reader, "reader");
        read();
    }

    public JsonValue parse() {
        skipWhitespace();
        JsonValue result = parseValue();
        skipWhitespace();
        if (!isEof()) {
            throw new JsonParseException("Not a single JSON document");
        }
        return result;
    }


    private JsonValue parseValue() {
        switch (peek()) {
            case '{':
                return parseObject();
            case '[':
                return parseArray();
            case '"':
                return parseString();
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
                return parseNumber();
            case 't':
                expect("true");
                return JsonBoolean.True;
            case 'f':
                expect("false");
                return JsonBoolean.False;
            case 'n':
                expect("null");
                return JsonNull.Null;
            default:
                throw new JsonParseException("Unexpected character '" + peek() + "'");
        }
    }

    private JsonObject parseObject() {
        expect('{');

        Map<String, JsonValue> values = new HashMap<>();
        while (peek() != '}') {
            if (!values.isEmpty()) {
                expect(',');
            }
            skipWhitespace();
            String key = readString();
            skipWhitespace();
            expect(':');
            skipWhitespace();
            JsonValue value = parseValue();
            values.put(key, value);
            skipWhitespace();
        }

        expect('}');
        return new JsonObject(values);
    }

    private JsonArray parseArray() {
        expect('[');

        List<JsonValue> values = new ArrayList<>();
        while (peek() != ']') {
            if (!values.isEmpty()) {
                expect(',');
            }
            skipWhitespace();
            values.add(parseValue());
            skipWhitespace();
        }

        expect(']');
        return new JsonArray(values);
    }

    private JsonString parseString() {
        return new JsonString(readString());
    }

    private String readString() {
        expect('"');
        StringBuilder res = new StringBuilder();
        while (!isEof() && peek() != '"') {
            if (peek() < 0x20) {
                throw new JsonParseException("Raw control character");
            } else if (peek() == '\\') {
                read();
                switch (read()) {
                    case '"':
                        res.append('"');
                        break;
                    case '\\':
                        res.append('\\');
                        break;
                    case '/':
                        res.append('/');
                        break;
                    case 'b':
                        res.append('\b');
                        break;
                    case 'f':
                        res.append('\f');
                        break;
                    case 'n':
                        res.append('\n');
                        break;
                    case 'r':
                        res.append('\r');
                        break;
                    case 't':
                        res.append('\t');
                        break;
                    case 'u':
                        res.append(readUnicode());
                        break;
                    default:
                        throw new JsonParseException("Illegal escape");
                }
            } else {
                res.append(read());
            }
        }
        expect('"');
        return res.toString();
    }

    private char readUnicode() {
        int result = 0;
        for (int i = 0; i < 4; i++) {
            char read = read();
            if (!isHexDigit(read)) {
                throw new JsonParseException("Expected hex digit");
            }
            result = (result << 4) | Character.digit(read, 16);
        }
        return (char) result;
    }

    private JsonNumber parseNumber() {
        StringBuilder sb = new StringBuilder();

        if (peek() == '-') {
            sb.append(read());
        }

        // integer part
        if (peek() == '0') {
            sb.append(read());
        } else {
            if (peek() < '1' || peek() > '9') {
                throw new JsonParseException();
            }
            digits(sb);
        }

        // decimal part
        if (peek() == '.') {
            sb.append(read());
            digits(sb);
        }

        // exponent part
        if (peek() == 'e' || peek() == 'E') {
            sb.append(read());
            if (peek() == '-' || peek() == '+') {
                sb.append(read());
            }
            digits(sb);
        }

        return new JsonNumber(sb.toString());
    }

    private void digits(StringBuilder sb) {
        if (!isDigit(peek())) {
            throw new JsonParseException("Expected a digit");
        }
        while (isDigit(peek())) {
            sb.append(read());
        }
    }

    private void expect(char c) {
        char read = read();
        if (read != c) {
            throw new JsonParseException("Invalid character '" + read + "'");
        }
    }

    private void expect(String value) {
        for (int i = 0; i < value.length(); i++) {
            expect(value.charAt(i));
        }
    }

    // region Helpers

    private void skipWhitespace() {
        while (isWhitespace(peek())) {
            read();
        }
    }

    private boolean isWhitespace(char c) {
        switch (c) {
            case '\t':
            case '\n':
            case '\r':
            case ' ':
                return true;
            default:
                return false;
        }
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isHexDigit(char c) {
        return c >= '0' && c <= '9'
            || c >= 'A' && c <= 'F'
            || c >= 'a' && c <= 'f';
    }

    private char peek() {
        return peek;
    }

    private char read() {
        try {
            int read = reader.read();
            char result = peek;
            peek = read == -1 ? EOF : (char) read;
            return result;
        } catch (IOException e) {
            throw new JsonParseException("Unexpected IOException thrown", e);
        }
    }

    private boolean isEof() {
        return peek() == EOF;
    }

    // endregion

}
