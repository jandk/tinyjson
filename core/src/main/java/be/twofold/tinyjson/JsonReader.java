package be.twofold.tinyjson;

import be.twofold.tinyjson.parser.*;

import java.util.*;

public final class JsonReader {

    private final Tokenizer tokenizer;

    JsonReader(Tokenizer tokenizer) {
        this.tokenizer = Objects.requireNonNull(tokenizer, "tokenizer");
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
        if (tokenizer.getToken().getType() != TokenType.Eof) {
            throw new JsonException("Not a single JSON document");
        }
        return result;
    }


    private JsonValue parseValue() {
        switch (tokenizer.getToken().getType()) {
            case ObjectStart:
                return parseObject();
            case ArrayStart:
                return parseArray();
            case String:
                return Json.string(tokenizer.getToken().getValue());
            case Number:
                return Json.number(new StringNumber(tokenizer.getToken().getValue()));
            case True:
                return Json.bool(true);
            case False:
                return Json.bool(false);
            case Null:
                return Json.Null;
            default:
                throw new JsonException("Unexpected " + tokenizer.getToken());
        }
    }

    private JsonObject parseObject() {
        // Skip leading ObjectStart
        tokenizer.nextToken();

        JsonObject object = Json.object();
        while (tokenizer.getToken().getType() != TokenType.ObjectEnd) {
            if (object.size() > 0) {
                verify(TokenType.Comma);
            }
            String key = verify(TokenType.String);
            verify(TokenType.Colon);
            object.add(key, parseValue());
            tokenizer.nextToken();
        }

        return object;
    }

    private JsonArray parseArray() {
        // Skip leading ArrayStart
        tokenizer.nextToken();

        JsonArray array = Json.array();
        while (tokenizer.getToken().getType() != TokenType.ArrayEnd) {
            if (array.size() > 0) {
                verify(TokenType.Comma);
            }
            array.add(parseValue());
            tokenizer.nextToken();
        }
        return array;
    }

    private String verify(TokenType expected) {
        if (tokenizer.getToken().getType() != expected) {
            throw new JsonException("Expected " + expected + ", got " + tokenizer.getToken());
        }
        String value = tokenizer.getToken().getValue();
        tokenizer.nextToken();
        return value;
    }

}
