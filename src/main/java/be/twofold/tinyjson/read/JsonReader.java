package be.twofold.tinyjson.read;

import be.twofold.tinyjson.*;

import java.io.*;

public final class JsonReader {

    // Tokenizer state
    private final StringBuilder builder = new StringBuilder();
    private final PeekingReader reader;
    private JsonTokenType token;
    private String value;

    public JsonReader(Reader reader) {
        this.reader = new PeekingReader(reader);
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
        switch (reader.peek()) {
            case '{':
                reader.read();
                token(JsonTokenType.ObjectStart, null);
                break;
            case '}':
                reader.read();
                token(JsonTokenType.ObjectEnd, null);
                break;
            case '[':
                reader.read();
                token(JsonTokenType.ArrayStart, null);
                break;
            case ']':
                reader.read();
                token(JsonTokenType.ArrayEnd, null);
                break;
            case ':':
                reader.read();
                token(JsonTokenType.Colon, null);
                break;
            case ',':
                reader.read();
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
                throw new JsonException("Unexpected character '" + (char) reader.peek() + "'");
        }
    }

    private void token(JsonTokenType type, String value) {
        this.token = type;
        this.value = value;
    }

    // region String

    private String parseString() {
        // skip leading '"'
        reader.read();
        builder.setLength(0);
        while (!reader.isEof() && reader.peek() != '"') {
            if (reader.peek() < 0x20) {
                throw new JsonException("Raw control character");
            }

            if (reader.peek() == '\\') {
                reader.read();
                switch (reader.read()) {
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
        if (reader.read() != '"') {
            throw new JsonException("Unclosed string literal");
        }
        return builder.toString();
    }

    private char parseUnicode() {
        int result = 0;
        for (int i = 0; i < 4; i++) {
            int read = reader.read();
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

    // endregion

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

    // endregion

}
