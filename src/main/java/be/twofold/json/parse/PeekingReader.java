package be.twofold.json.parse;

import be.twofold.json.*;

import java.io.*;
import java.util.*;

final class PeekingReader {

    private static final char InvalidChar = '\uFFFF';

    private final Reader reader;
    private int current;

    PeekingReader(Reader reader) {
        this.reader = Objects.requireNonNull(reader, "reader");
        read();
    }

    int peek() {
        return current;
    }

    int read() {
        int result = current;
        current = readCharAsInt();
        return result;
    }

    boolean isEof() {
        return current == -1;
    }

    private int readCodePoint() {
        char c1 = readChar();
        if (c1 == InvalidChar) return -1;
        if (!Character.isHighSurrogate(c1)) return c1;

        char c2 = readChar();
        if (c2 == InvalidChar) return -1;
        if (!Character.isLowSurrogate(c2)) return c1;

        return Character.toCodePoint(c1, c2);
    }

    private char readChar() {
        try {
            int read = reader.read();
            return read == -1 ? InvalidChar : (char) read;
        } catch (IOException e) {
            throw new JsonParseException("Unexpected IOException thrown", e);
        }
    }

    private int readCharAsInt() {
        try {
            return reader.read();
        } catch (IOException e) {
            throw new JsonParseException("Unexpected IOException thrown", e);
        }
    }

}
