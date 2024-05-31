package be.twofold.tinyjson.read;

public enum JsonTokenType {
    ObjectStart,
    ObjectEnd,
    ArrayStart,
    ArrayEnd,
    Colon,
    Comma,
    String,
    Number,
    True,
    False,
    Null,
    Eof,
}
