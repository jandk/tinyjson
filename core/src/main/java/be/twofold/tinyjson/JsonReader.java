package be.twofold.tinyjson;

import be.twofold.tinyjson.parser.*;

import java.util.*;

public final class JsonReader {

    private final PeekingReader reader;

    // Tokenizer state
    private final StringBuilder builder = new StringBuilder();
    private TokenType token;
    private String value;

    JsonReader(PeekingReader reader) {
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
        if (token != TokenType.Eof) {
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
        while (token != TokenType.ObjectEnd) {
            if (object.size() > 0) {
                verify(TokenType.Comma);
            }
            String key = verify(TokenType.String);
            verify(TokenType.Colon);
            object.add(key, parseValue());
            nextToken();
        }

        return object;
    }

    private JsonArray parseArray() {
        nextToken(); // Skip leading ArrayStart

        JsonArray array = Json.array();
        while (token != TokenType.ArrayEnd) {
            if (array.size() > 0) {
                verify(TokenType.Comma);
            }
            array.add(parseValue());
            nextToken();
        }
        return array;
    }

    private String verify(TokenType expected) {
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
