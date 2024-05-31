package be.twofold.tinyjson.read;

import be.twofold.tinyjson.*;

import java.io.*;
import java.util.*;

final class PeekingReader {
    private static final int NotPeeked = -2;
    private final Reader reader;
    private int peeked = NotPeeked;
    private int line;
    private int column;

    PeekingReader(Reader reader) {
        this.reader = Objects.requireNonNull(reader, "reader cannot be null");
    }

    int line() {
        return line;
    }

    int column() {
        return column;
    }

    int peek() {
        if (peeked == NotPeeked) {
            peeked = readChar();
        }
        return peeked;
    }

    int read() {
        if (peeked == NotPeeked) {
            return readChar();
        }
        int result = peeked;
        peeked = NotPeeked;
        if (result == '\n') {
            line++;
            column = 0;
        } else {
            column++;
        }
        return result;
    }

    boolean isEof() {
        return peek() == -1;
    }

    int readChar() {
        try {
            int high = reader.read();
            if (high < 0 || !Character.isSurrogate((char) high)) {
                return high;
            }
            if (Character.isLowSurrogate((char) high)) {
                throw new IOException("Unpaired low surrogate");
            }

            int low = reader.read();
            if (low < 0 || Character.isHighSurrogate((char) low)) {
                throw new IOException("Unpaired high surrogate");
            }

            return Character.toCodePoint((char) high, (char) low);
        } catch (IOException e) {
            throw new JsonException("Unexpected I/O error", e);
        }
    }
}
