package be.twofold.tinyjson.read;

import be.twofold.tinyjson.*;

import java.io.*;

public final class JsonReader {
    private final JsonTokenizer tokenizer;

    public JsonReader(Reader reader) {
        this.tokenizer = new JsonTokenizer(reader);
    }

    public JsonValue parse() {
        JsonValue result;
        try {
            tokenizer.nextToken();
            result = parseValue();
        } catch (StackOverflowError e) {
            throw new JsonException("Stack overflow");
        }
        tokenizer.nextToken();
        if (tokenizer.token() != JsonTokenType.Eof) {
            throw new JsonException("Not a single JSON document");
        }
        return result;
    }


    private JsonValue parseValue() {
        switch (tokenizer.token()) {
            case ObjectStart:
                return parseObject();
            case ArrayStart:
                return parseArray();
            case String:
                return Json.string(tokenizer.value());
            case Number:
                return Json.number(new StringNumber(tokenizer.value()));
            case True:
                return Json.bool(true);
            case False:
                return Json.bool(false);
            case Null:
                return Json.Null;
            default:
                throw new JsonException("Expected a value, got " + tokenizer.token());
        }
    }

    private JsonObject parseObject() {
        tokenizer.nextToken(); // Skip leading ObjectStart

        JsonObject object = Json.object();
        while (tokenizer.token() != JsonTokenType.ObjectEnd) {
            if (object.size() > 0) {
                verify(JsonTokenType.Comma);
            }
            String key = verify(JsonTokenType.String);
            verify(JsonTokenType.Colon);
            object.add(key, parseValue());
            tokenizer.nextToken();
        }

        return object;
    }

    private JsonArray parseArray() {
        tokenizer.nextToken(); // Skip leading ArrayStart

        JsonArray array = Json.array();
        while (tokenizer.token() != JsonTokenType.ArrayEnd) {
            if (array.size() > 0) {
                verify(JsonTokenType.Comma);
            }
            array.add(parseValue());
            tokenizer.nextToken();
        }
        return array;
    }

    private String verify(JsonTokenType expected) {
        if (tokenizer.token() != expected) {
            throw new JsonException("Expected " + expected + ", got " + tokenizer.token());
        }
        String value = tokenizer.value();
        tokenizer.nextToken();
        return value;
    }
}
