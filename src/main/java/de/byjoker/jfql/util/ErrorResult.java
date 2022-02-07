package de.byjoker.jfql.util;

import de.byjoker.jfql.exception.ConnectorException;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class ErrorResult extends SimpleResponse {

    public ErrorResult(@NotNull JSONObject response, boolean exception) {
        super(response, exception);

        if (getType() != ResponseType.ERROR) {
            throw new ConnectorException("This response isn't a error!");
        }
    }

    public String getError() {
        return getResponse().getString("exception");
    }

}
