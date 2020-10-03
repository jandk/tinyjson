package be.twofold.json.parse;

import be.twofold.json.*;

import java.io.*;

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
                return Json.Null;
            default:
                throw new JsonParseException("Unexpected " + tokenizer.getToken());
        }
    }

    private JsonObject parseObject() {
        tokenizer.next(); // Skip leading ObjectStart

        JsonObject object = Json.object();
        while (tokenizer.getToken() != JsonToken.ObjectEnd) {
            if (object.size() > 0) {
                verify(JsonToken.Comma);
            }
            String key = verify(JsonToken.String);
            verify(JsonToken.Colon);
            object.add(key, parseValue());
            tokenizer.next();
        }

        return object;
    }

    private JsonArray parseArray() {
        tokenizer.next(); // Skip leading ArrayStart

        JsonArray array = Json.array();
        while (tokenizer.getToken() != JsonToken.ArrayEnd) {
            if (array.size() > 0) {
                verify(JsonToken.Comma);
            }
            array.add(parseValue());
            tokenizer.next();
        }
        return array;
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
