package de.byjoker.jfql.util;

import org.json.JSONObject;

import java.util.List;

public interface Result {

    List<Column> getColumns();

    List<String> getStructure();

    ResponseType getType();

    JSONObject getResponse();

}
