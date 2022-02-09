package de.byjoker.jfql.util;

import de.byjoker.jfql.exception.ConnectorException;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Result extends SimpleResponse {

    public Result(@NotNull JSONObject response, boolean exception) {
        super(response, exception);

        if (getType() != ResponseType.RESULT) {
            throw new ConnectorException("This response isn't a result!");
        }
    }

    public List<TableEntry> getEntries() {
        final JSONObject response = getResponse();

        switch (getResultType()) {
            case RELATIONAL: {
                return IntStream.range(0, response.getJSONArray("result").length()).mapToObj(i -> response.getJSONArray("result").getJSONObject(i)).
                        map(jsonObject -> new RelationalTableEntry(jsonObject.getJSONObject("content"), jsonObject.getLong("creation")))
                        .collect(Collectors.toList());
            }
            case DOCUMENT: {
                return IntStream.range(0, response.getJSONArray("result").length()).mapToObj(i -> response.getJSONArray("result").getJSONObject(i)).
                        map(jsonObject -> new DocumentCollectionEntry(jsonObject.getJSONObject("content"), jsonObject.getLong("creation")))
                        .collect(Collectors.toList());
            }
            default: {
                final JSONArray result = (response.has("result") ? response.getJSONArray("result") : response.getJSONArray("answer"));
                final List<TableEntry> entries = new ArrayList<>();

                IntStream.range(0, result.length()).forEach(j -> {
                    final Object object = result.get(j);
                    if (object instanceof JSONObject) {
                        entries.add(new DeprecatedTableEntry(result.getJSONObject(j).getJSONObject("content"), result.getJSONObject(j).getLong("creation")));
                    } else {
                        entries.add(new DeprecatedTableEntry(object, -1));
                    }
                });

                return entries;
            }
        }
    }

    public List<String> getStructure() {
        return getResponse().getJSONArray("structure").toList().stream().map(Object::toString).collect(Collectors.toList());
    }

    public ResultType getResultType() {
        return getResponse().has("resultType") ? getResponse().getEnum(ResultType.class, "resultType") : ResultType.DEPRECATED;
    }

}
