package be.twofold.tinyjson.read;

import be.twofold.tinyjson.*;

import java.io.*;
import java.util.*;

public final class JsonReader {

    // Reader state
    private static final int NotPeeked = -2;
    private final Reader reader;
    private int peeked = NotPeeked;

    // Tokenizer state
    private final StringBuilder builder = new StringBuilder();
    private JsonTokenType token;
    private String value;

    public JsonReader(Reader reader) {
        this.reader = Objects.requireNonNull(reader, "reader");
    }

    // region Parser

    public JsonValue parse() {
        JsonValue result;
        try {
            nextToken();
            result = parseValue();
        } catch (StackOverflowError e) {
            throw new JsonException("Stack overflow");
        }
        nextToken();
        if (token != JsonTokenType.Eof) {
            throw new JsonException("Not a single JSON document");
        }
        return result;
    }


    private JsonValue parseValue() {
        switch (token) {
            case ObjectStart:
                return parseObject();
            case ArrayStart:
                return parseArray();
            case String:
                return Json.string(value);
            case Number:
                return Json.number(new StringNumber(value));
            case True:
                return Json.bool(true);
            case False:
                return Json.bool(false);
            case Null:
                return Json.Null;
            default:
                throw new JsonException("Unexpected " + token);
        }
    }

    private JsonObject parseObject() {
        nextToken(); // Skip leading ObjectStart

        JsonObject object = Json.object();
        while (token != JsonTokenType.ObjectEnd) {
            if (object.size() > 0) {
                verify(JsonTokenType.Comma);
            }
            String key = verify(JsonTokenType.String);
            verify(JsonTokenType.Colon);
            object.add(key, parseValue());
            nextToken();
        }

        return object;
    }

    private JsonArray parseArray() {
        nextToken(); // Skip leading ArrayStart

        JsonArray array = Json.array();
        while (token != JsonTokenType.ArrayEnd) {
            if (array.size() > 0) {
                verify(JsonTokenType.Comma);
            }
            array.add(parseValue());
            nextToken();
        }
        return array;
    }

    private String verify(JsonTokenType expected) {
        if (token != expected) {
            throw new JsonException("Expected " + expected + ", got " + token);
        }
        String value = this.value;
        nextToken();
        return value;
    }

    // endregion

    // region Tokenizer

    public void nextToken() {
        skipWhitespace();
        switch (peek()) {
            case '{':
                read();
                token(JsonTokenType.ObjectStart, null);
                break;
            case '}':
                read();
                token(JsonTokenType.ObjectEnd, null);
                break;
            case '[':
                read();
                token(JsonTokenType.ArrayStart, null);
                break;
            case ']':
                read();
                token(JsonTokenType.ArrayEnd, null);
                break;
            case ':':
                read();
                token(JsonTokenType.Colon, null);
                break;
            case ',':
                read();
                token(JsonTokenType.Comma, null);
                break;
            case '"':
                token(JsonTokenType.String, parseString());
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
                token(JsonTokenType.Number, parseNumber());
                break;
            case 't':
                token(JsonTokenType.True, expect("true"));
                break;
            case 'f':
                token(JsonTokenType.False, expect("false"));
                break;
            case 'n':
                token(JsonTokenType.Null, expect("null"));
                break;
            case -1:
                token(JsonTokenType.Eof, null);
                break;
            default:
                throw new JsonException("Unexpected character '" + (char) peek() + "'");
        }
    }

    private void token(JsonTokenType type, String value) {
        this.token = type;
        this.value = value;
    }

    // region String

    private String parseString() {
        // skip leading '"'
        read();
        builder.setLength(0);
        while (!isEof() && peek() != '"') {
            if (peek() < 0x20) {
                throw new JsonException("Raw control character");
            }

            if (peek() == '\\') {
                read();
                switch (read()) {
                    case '"':
                        builder.append('"');
                        break;
                    case '\\':
                        builder.append('\\');
                        break;
                    case '/':
                        builder.append('/');
                        break;
                    case 'b':
                        builder.append('\b');
                        break;
                    case 'f':
                        builder.append('\f');
                        break;
                    case 'n':
                        builder.append('\n');
                        break;
                    case 'r':
                        builder.append('\r');
                        break;
                    case 't':
                        builder.append('\t');
                        break;
                    case 'u':
                        builder.append(parseUnicode());
                        break;
                    default:
                        throw new JsonException("Illegal escape");
                }
            } else {
                appendNext();
            }
        }
        if (read() != '"') {
            throw new JsonException("Unclosed string literal");
        }
        return builder.toString();
    }

    private char parseUnicode() {
        int result = 0;
        for (int i = 0; i < 4; i++) {
            int read = read();
            if (!isHexDigit(read)) {
                throw new JsonException("Expected hex digit");
            }
            result = (result << 4) | Character.digit(read, 16);
        }
        return (char) result;
    }

    // endregion

    // region Number

    private String parseNumber() {
        builder.setLength(0);

        if (peek() == '-') {
            appendNext();
        }

        // integer part
        if (peek() == '0') {
            appendNext();
        } else {
            if (peek() < '1' || peek() > '9') {
                throw new JsonException();
            }
            digits();
        }

        // decimal part
        if (peek() == '.') {
            appendNext();
            digits();
        }

        // exponent part
        if (peek() == 'e' || peek() == 'E') {
            appendNext();
            if (peek() == '-' || peek() == '+') {
                appendNext();
            }
            digits();
        }

        return builder.toString();
    }

    private void digits() {
        if (!isDigit(peek())) {
            throw new JsonException("Expected a digit");
        }
        while (isDigit(peek())) {
            appendNext();
        }
    }

    // endregion

    private String expect(String expected) {
        for (int i = 0; i < expected.length(); i++) {
            if (read() != expected.charAt(i)) {
                throw new JsonException("Expected '" + expected + "' literal");
            }
        }
        return expected;
    }

    private void appendNext() {
        builder.append(read());
    }

    private void skipWhitespace() {
        while (isWhitespace(peek())) {
            read();
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

    // endregion

    // region Low-level reading

    private int peek() {
        if (peeked == NotPeeked) {
            peeked = readChar();
        }
        return peeked;
    }

    private int read() {
        if (peeked == NotPeeked) {
            return readChar();
        }
        int result = peeked;
        peeked = NotPeeked;
        return result;
    }

    private boolean isEof() {
        return peek() == -1;
    }

    private int readChar() {
        try {
            return reader.read();
        } catch (IOException e) {
            throw new JsonException("Unexpected I/O error", e);
        }
    }

    // endregion

}
