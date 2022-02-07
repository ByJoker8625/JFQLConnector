package de.byjoker.jfql.util;

import de.byjoker.jfql.exception.ConnectorException;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.List;
import java.util.stream.Collectors;

public class Result extends SimpleResponse {

    public Result(@NotNull JSONObject response, boolean exception) {
        super(response, exception);

        if (getType() != ResponseType.RESULT) {
            throw new ConnectorException("This response isn't a result!");
        }
    }

    public List<TableEntry> getEntries() {
        return null;
    }

    public List<String> getStructure() {
        return getResponse().getJSONArray("structure").toList().stream().map(Object::toString).collect(Collectors.toList());
    }

    public ResultType getResultType() {
        return getResponse().has("resultType") ? getResponse().getEnum(ResultType.class, "resultType") : ResultType.DEPRECATED;
    }

}
