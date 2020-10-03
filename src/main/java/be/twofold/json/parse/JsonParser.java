package be.twofold.json.parse;

import be.twofold.json.*;

import java.io.*;
import java.util.*;

public final class JsonParser {

    private final JsonTokenizer tokenizer;

    public JsonParser(Reader reader) {
        this.tokenizer = new JsonTokenizer(reader);
    }

    public JsonValue parse() {
        JsonValue result;
        try {
            tokenizer.next();
            result = parseValue();
        } catch (StackOverflowError e) {
            throw new JsonParseException("Stack overflow");
        }
        tokenizer.next();
        if (tokenizer.getToken() != JsonToken.Eof) {
            throw new JsonParseException("Not a single JSON document");
        }
        return result;
    }


    private JsonValue parseValue() {
        switch (tokenizer.getToken()) {
            case ObjectStart:
                return parseObject();
            case ArrayStart:
                return parseArray();
            case String:
                return Json.string(tokenizer.getValue());
            case Number:
                return Json.number(tokenizer.getValue());
            case True:
                return Json.bool(true);
            case False:
                return Json.bool(false);
            case Null:
                return Json.nul();
            default:
                throw new JsonParseException("Unexpected " + tokenizer.getToken());
        }
    }

    private JsonObject parseObject() {
        tokenizer.next(); // Skip leading ObjectStart

        Map<String, JsonValue> values = new LinkedHashMap<>();
        while (tokenizer.getToken() != JsonToken.ObjectEnd) {
            if (!values.isEmpty()) {
                verify(JsonToken.Comma);
            }
            String key = verify(JsonToken.String);
            verify(JsonToken.Colon);
            values.put(key, parseValue());
            tokenizer.next();
        }

        return new JsonObject(values);
    }

    private JsonArray parseArray() {
        tokenizer.next(); // Skip leading ArrayStart

        List<JsonValue> values = new ArrayList<>();
        while (tokenizer.getToken() != JsonToken.ArrayEnd) {
            if (!values.isEmpty()) {
                verify(JsonToken.Comma);
            }
            values.add(parseValue());
            tokenizer.next();
        }
        return new JsonArray(values);
    }

    private String verify(JsonToken expected) {
        if (tokenizer.getToken() != expected) {
            throw new JsonParseException("Expected " + expected + ", got " + tokenizer.getToken());
        }
        String value = tokenizer.getValue();
        tokenizer.next();
        return value;
    }

}
