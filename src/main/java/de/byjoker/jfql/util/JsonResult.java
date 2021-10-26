package de.byjoker.jfql.util;

import de.byjoker.jfql.exception.ConnectorException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class JsonResult implements Result {

    private final JSONObject jsonResponse;

    public JsonResult(JSONObject jsonResult, boolean exception) {
        this.jsonResponse = jsonResult;

        if (jsonResult == null && exception)
            throw new ConnectorException("Empty response!");

        final ResponseType type = getType();

        if (exception) switch (type) {
            case ERROR:
                throw new ConnectorException(jsonResult.getString("exception"));
            case FORBIDDEN:
                throw new ConnectorException("You don't have the permissions to do that!");
            case SYNTAX_ERROR:
                throw new ConnectorException("Syntax error!");
        }
    }

    @Override
    public List<Column> getColumns() {
        if (getType() != ResponseType.RESULT)
            throw new ConnectorException("Response isn't of type 'RESULT'!");

        List<Column> columns = new ArrayList<>();
        JSONArray result = (jsonResponse.isNull("result") ? jsonResponse.getJSONArray("answer") : jsonResponse.getJSONArray("result"));

        IntStream.range(0, result.length()).forEach(j -> {
            Object object = result.get(j);
            if (object instanceof JSONObject) {
                columns.add(new JsonColumn(result.getJSONObject(j)));
            } else {
                columns.add(new JsonColumn(object));
            }
        });

        return columns;
    }

    @Override
    public List<String> getStructure() {
        if (getType() != ResponseType.RESULT)
            throw new ConnectorException("Response isn't of type 'RESULT'!");

        return jsonResponse.getJSONArray("structure").toList().stream().map(Object::toString).collect(Collectors.toList());
    }

    @Override
    public ResponseType getType() {
        return ResponseType.valueOf(jsonResponse.getString("type"));
    }

    @Override
    public JSONObject getResponse() {
        return jsonResponse;
    }

    @Override
    public String toString() {
        return jsonResponse.toString();
    }
}
