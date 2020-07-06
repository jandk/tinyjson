package be.twofold.json.parse;

import java.io.*;

final class JsonTokenizer {

    private final StringBuilder builder = new StringBuilder();
    private final PeekingReader reader;

    private JsonToken token;
    private String value;

    JsonTokenizer(Reader reader) {
        this.reader = new PeekingReader(reader);
    }

    public JsonToken getToken() {
        return token;
    }

    public String getValue() {
        return value;
    }

    public JsonToken next() {
        skipWhitespace();
        switch (reader.peek()) {
            case '{':
                return token(JsonToken.ObjectStart);
            case '}':
                return token(JsonToken.ObjectEnd);
            case '[':
                return token(JsonToken.ArrayStart);
            case ']':
                return token(JsonToken.ArrayEnd);
            case ':':
                return token(JsonToken.Colon);
            case ',':
                return token(JsonToken.Comma);
            case '"':
                return token(JsonToken.String, parseString());
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
                return token(JsonToken.Number, parseNumber());
            case 't':
                return token(JsonToken.True, expect("true"));
            case 'f':
                return token(JsonToken.False, expect("false"));
            case 'n':
                return token(JsonToken.Null, expect("null"));
            case -1:
                return token(JsonToken.Eof);
            default:
                throw new JsonParseException("Unexpected character '" + reader.peek() + "'");
        }
    }

    private JsonToken token(JsonToken type) {
        reader.read();
        this.token = type;
        this.value = null;
        return type;
    }

    private JsonToken token(JsonToken type, String value) {
        this.token = type;
        this.value = value;
        return type;
    }

    // region String

    private String parseString() {
        // skip leading '"'
        reader.read();
        builder.setLength(0);
        while (!reader.isEof() && reader.peek() != '"') {
            if (reader.peek() < 0x20) {
                throw new JsonParseException("Raw control character");
            } else if (reader.peek() == '\\') {
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
                        builder.append(readUnicode());
                        break;
                    default:
                        throw new JsonParseException("Illegal escape");
                }
            } else {
                appendNext();
            }
        }
        if (reader.peek() != '"') {
            throw new JsonParseException("Unclosed string literal");
        }
        reader.read();
        return builder.toString();
    }

    private char readUnicode() {
        int result = 0;
        for (int i = 0; i < 4; i++) {
            int read = reader.read();
            if (!isHexDigit(read)) {
                throw new JsonParseException("Expected hex digit");
            }
            result = (result << 4) | Character.digit(read, 16);
        }
        return (char) result;
    }

    private boolean isHexDigit(int c) {
        return c >= '0' && c <= '9'
            || c >= 'A' && c <= 'F'
            || c >= 'a' && c <= 'f';
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
                throw new JsonParseException();
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

    private void appendNext() {
        builder.appendCodePoint(reader.read());
    }

    private void digits() {
        if (!isDigit(reader.peek())) {
            throw new JsonParseException("Expected a digit");
        }
        while (isDigit(reader.peek())) {
            appendNext();
        }
    }

    private boolean isDigit(int ch) {
        return ch >= '0' && ch <= '9';
    }

    // endregion

    private String expect(String expected) {
        for (int i = 0; i < expected.length(); i++) {
            if (reader.read() != expected.charAt(i)) {
                throw new JsonParseException("Expected '" + expected + "' literal");
            }
        }
        return expected;
    }

    private void skipWhitespace() {
        while (isWhitespace(reader.peek())) {
            reader.read();
        }
    }

    private boolean isWhitespace(int c) {
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

}
