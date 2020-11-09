package de.jokergames.connector.util;

import de.jokergames.connector.exception.ConnectorException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Janick
 */

public class Result {

    private final JSONObject jsonObject;

    public Result(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public List<Column> getColumns() {
        if (!getType().equals("REST")) {
            throw new ConnectorException("The response type isn't 'REST'!");
        }

        List<Column> columns = new ArrayList<>();
        JSONArray answer = jsonObject.getJSONArray("answer");

        for (int j = 0; j < answer.length(); j++) {
            columns.add(new Column(answer.getJSONObject(j)));
        }

        return columns;
    }

    public List<String> getStructure() {
        if (!getType().equals("REST")) {
            throw new ConnectorException("The response type isn't 'REST'!");
        }

        return jsonObject.getJSONArray("structure").toList().stream().map(Object::toString).collect(Collectors.toList());
    }

    public int getCode() {
        return jsonObject.getInt("rCode");
    }

    public String getType() {
        return jsonObject.getString("type");
    }

    @Deprecated
    public JSONObject getResponse() {
        return jsonObject;
    }
}
