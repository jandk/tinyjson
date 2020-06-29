package be.twofold.json;

import java.io.*;
import java.util.*;

public class JsonParser {

    private final Reader reader;

    public JsonParser(Reader reader) {
        this.reader = Objects.requireNonNull(reader, "reader");
    }

    public JsonValue parse() {
        throw new UnsupportedOperationException();
    }

}
