package be.twofold.json.parse;

import be.twofold.json.*;

import java.io.*;
import java.util.*;

public class JsonParser {

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
                return Json.Null;
            default:
                throw new JsonParseException("Unexpected token: " + tokenizer.getToken());
        }
    }

    private JsonObject parseObject() {
        Map<String, JsonValue> values = new HashMap<>();
        while (true) {
            tokenizer.next();
            if (tokenizer.getToken() == JsonToken.ObjectEnd) {
                break;
            }
            if (!values.isEmpty()) {
                verify(JsonToken.Comma);
                tokenizer.next();
            }
            verify(JsonToken.String);
            String key = tokenizer.getValue();
            tokenizer.next();
            verify(JsonToken.Colon);
            tokenizer.next();
            JsonValue value = parseValue();
            values.put(key, value);
        }

        return new JsonObject(values);
    }

    private JsonArray parseArray() {
        List<JsonValue> values = new ArrayList<>();
        while (true) {
            tokenizer.next();
            if (tokenizer.getToken() == JsonToken.ArrayEnd) {
                break;
            }
            if (!values.isEmpty()) {
                verify(JsonToken.Comma);
                tokenizer.next();
            }
            JsonValue value = parseValue();
            values.add(value);
        }

        return new JsonArray(values);
    }

    private void verify(JsonToken expected) {
        if (tokenizer.getToken() != expected) {
            throw new JsonParseException();
        }
    }

}
