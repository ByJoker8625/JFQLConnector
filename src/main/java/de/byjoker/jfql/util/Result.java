package de.byjoker.jfql.util;

import de.byjoker.jfql.exception.ConnectorException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Result {

    private final JSONObject jsonObject;

    public Result(JSONObject jsonObject) {
        this(jsonObject, true);
    }

    public Result(JSONObject jsonObject, boolean exception) {
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
        if (!getType().equals("REST")) {
            throw new ConnectorException("The response type isn't 'REST'!");
        }

        List<Column> columns = new ArrayList<>();
        JSONArray answer = jsonObject.getJSONArray("answer");

        for (int j = 0; j < answer.length(); j++) {
            Object object = answer.get(j);

            if (object instanceof JSONObject) {
                columns.add(new Column(answer.getJSONObject(j)));
            } else {
                columns.add(new Column(object));
            }
        }

        return columns;
    }

    public List<String> getStructureList() {
        if (!getType().equals("REST")) {
            throw new ConnectorException("The response type isn't 'REST'!");
        }

        return jsonObject.getJSONArray("structure").toList().stream().map(Object::toString).collect(Collectors.toList());
    }

    public String[] getStructureArray() {
        if (!getType().equals("REST")) {
            throw new ConnectorException("The response type isn't 'REST'!");
        }

        final String[] structure = new String[getStructureList().size()];
        int index = 0;

        for (String s : getStructureList()) {
            structure[index] = s;
            index++;
        }

        return structure;
    }

    public int getCode() {
        return jsonObject.getInt("rCode");
    }

    public String getType() {
        return jsonObject.getString("type");
    }

    @Override
    public String toString() {
        return jsonObject.toString();
    }

    @Deprecated
    public JSONObject getResponse() {
        return jsonObject;
    }
}
