package test.project1.application;

import io.vertx.core.json.JsonObject;

public class Result {
    private final String value;
    private final String lexical;

    public Result(String value, String lexical) {
        this.value = value;
        this.lexical = lexical;
    }

    public String getValue() {
        return value;
    }

    public String getLexical() {
        return lexical;
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.put("value", value);
        json.put("lexical", lexical);
        return json;
    }
}
