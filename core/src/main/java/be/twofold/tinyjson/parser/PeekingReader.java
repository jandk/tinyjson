package be.twofold.tinyjson.parser;

import be.twofold.tinyjson.*;

import java.io.*;

public final class PeekingReader {

    private static final int NotPeeked = -2;
    private final Reader reader;
    private int peeked = NotPeeked;

    public PeekingReader(Reader reader) {
        this.reader = reader;
    }

    public int peek() {
        if (peeked == NotPeeked) {
            peeked = readChar();
        }
        return peeked;
    }

    public int read() {
        if (peeked == NotPeeked) {
            return readChar();
        }
        int result = peeked;
        peeked = NotPeeked;
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
