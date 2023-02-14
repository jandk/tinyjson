package be.twofold.tinyjson.parser;

import be.twofold.tinyjson.*;

import java.io.*;

final class PeekingReader {

    private static final int NotPeeked = -2;
    private final Reader reader;
    private int current = NotPeeked;

    PeekingReader(Reader reader) {
        this.reader = reader;
    }

    int peek() {
        if (current == NotPeeked) {
            current = readChar();
        }
        return current;
    }

    int next() {
        if (current == NotPeeked) {
            return readChar();
        }
        int result = current;
        current = NotPeeked;
        return result;
    }

    boolean isEof() {
        return peek() == -1;
    }

    private int readChar() {
        try {
            return reader.read();
        } catch (IOException e) {
            throw new JsonException("Unexpected I/O error", e);
        }
    }

}
