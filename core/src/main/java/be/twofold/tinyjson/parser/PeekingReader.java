package be.twofold.tinyjson.parser;

import be.twofold.tinyjson.*;

import java.io.*;

public final class PeekingReader {

    private static final int NotPeeked = -2;
    private final Reader reader;
    private int current = NotPeeked;

    public PeekingReader(Reader reader) {
        this.reader = reader;
    }

    public int peek() {
        if (current == NotPeeked) {
            current = readChar();
        }
        return current;
    }

    public int next() {
        if (current == NotPeeked) {
            return readChar();
        }
        int result = current;
        current = NotPeeked;
        return result;
    }

    public boolean isEof() {
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
