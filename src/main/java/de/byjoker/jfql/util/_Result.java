package de.byjoker.jfql.util;

import de.byjoker.jfql.exception.ConnectorException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class _Result {

    private final JSONObject jsonObject;

    public _Result(JSONObject jsonObject) {
        this(jsonObject, true);
    }

    public _Result(JSONObject jsonObject, boolean exception) {
        this.jsonObject = jsonObject;

        if (!exception && jsonObject == null)
            return;

        if (exception && jsonObject == null) {
            throw new ConnectorException("Empty response!");
        }

        if (exception && getType().equals("ERROR")) {
            throw new ConnectorException(jsonObject.getString("exception"));
        }

        if (exception && getType().equals("FORBIDDEN")) {
            throw new ConnectorException("You don't have the permissions to do that!");
        }

        if (exception && getType().equals("SYNTAX_ERROR")) {
            throw new ConnectorException("Syntax error!");
        }
    }

    public List<Column> getColumns() {
        if (!getType().equals("REST") && !getType().equals("RESULT")) {
            throw new ConnectorException("The response type isn't 'REST' or 'RESULT'!");
        }

        List<Column> columns = new ArrayList<>();
        JSONArray result = (jsonObject.isNull("result") ? jsonObject.getJSONArray("answer") : jsonObject.getJSONArray("result"));

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

    public List<String> getStructure() {
        if (!getType().equals("REST")) {
            throw new ConnectorException("The response type isn't 'REST'!");
        }

        return jsonObject.getJSONArray("structure").toList().stream().map(Object::toString).collect(Collectors.toList());
    }

    public String getType() {
        return jsonObject.getString("type");
    }

    @Override
    public String toString() {
        return jsonObject.toString();
    }

    public JSONObject getResponse() {
        return jsonObject;
    }
}
