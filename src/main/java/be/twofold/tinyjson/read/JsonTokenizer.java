package be.twofold.tinyjson.read;

import be.twofold.tinyjson.*;

import java.io.*;

final class JsonTokenizer {
    private final StringBuilder builder = new StringBuilder();
    private final PeekingReader reader;
    private JsonTokenType token;
    private String value;

    JsonTokenizer(Reader reader) {
        this.reader = new PeekingReader(reader);
    }

    JsonTokenType token() {
        return token;
    }

    String value() {
        return value;
    }

    void nextToken() {
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
                throw ex("Unexpected character '" + (char) reader.peek() + "'");
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
                throw ex("Raw control character");
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
                        throw ex("Illegal escape");
                }
            } else {
                appendNext();
            }
        }
        if (reader.read() != '"') {
            throw ex("Unclosed string literal");
        }
        return builder.toString();
    }

    private char parseUnicode() {
        int result = 0;
        for (int i = 0; i < 4; i++) {
            int read = reader.read();
            if (!isHexDigit(read)) {
                throw ex("Expected hex digit");
            }
            result = (result << 4) | Character.digit(read, 16);
        }
        return (char) result;
    }

    // endregion

    //
    // Number
    //

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
                throw ex("Expected a digit");
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
            throw ex("Expected a digit");
        }
        while (isDigit(reader.peek())) {
            appendNext();
        }
    }

    //
    // Helpers
    //

    private JsonException ex(String message) {
        return new JsonException(message + " at line " + reader.line() + " column " + reader.column());
    }

    private String expect(String expected) {
        for (int i = 0; i < expected.length(); i++) {
            if (reader.read() != expected.charAt(i)) {
                throw ex("Expected '" + expected + "' literal");
            }
        }
        return expected;
    }

    private void appendNext() {
        builder.appendCodePoint(reader.read());
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
