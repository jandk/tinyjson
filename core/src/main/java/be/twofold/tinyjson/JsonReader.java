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
            result = parseValue();
        } catch (StackOverflowError e) {
            throw new JsonException("Stack overflow");
        }
        if (notMatch(TokenType.Eof)) {
            throw new JsonException("Not a single JSON document");
        }
        return result;
    }


    private JsonValue parseValue() {
        Token token = tokenizer.next();
        switch (token.getType()) {
            case ObjectStart:
                return parseObject();
            case ArrayStart:
                return parseArray();
            case String:
                return Json.string(token.getValue());
            case Number:
                return Json.number(new StringNumber(token.getValue()));
            case True:
                return Json.bool(true);
            case False:
                return Json.bool(false);
            case Null:
                return Json.Null;
            default:
                throw new JsonException("Unexpected " + token);
        }
    }

    private JsonObject parseObject() {
        JsonObject object = Json.object();

        while (notMatch(TokenType.ObjectEnd)) {
            if (object.size() > 0) {
                verify(TokenType.Comma);
            }
            String key = verify(TokenType.String);
            verify(TokenType.Colon);
            object.add(key, parseValue());
        }
        tokenizer.next(); // read '}'

        return object;
    }

    private JsonArray parseArray() {
        JsonArray array = Json.array();

        while (notMatch(TokenType.ArrayEnd)) {
            if (array.size() > 0) {
                verify(TokenType.Comma);
            }
            array.add(parseValue());
        }
        tokenizer.next(); // read ']'

        return array;
    }

    private boolean notMatch(TokenType type) {
        return tokenizer.peek().getType() != type;
    }

    private String verify(TokenType expected) {
        if (notMatch(expected)) {
            throw new JsonException("Expected " + expected + ", got " + tokenizer.peek());
        }
        return tokenizer.next().getValue();
    }

}
