package de.byjoker.jfql.util

import de.byjoker.jfql.exception.ConnectorException
import org.json.JSONObject


open class SimpleResponse(final override val response: JSONObject, val exception: Boolean) : Response {

    final override var type: ResponseType = ResponseType.valueOf(response.getString("type"))

    init {
        if (exception) when (type) {
            ResponseType.ERROR ->
                throw ConnectorException(response.getString("exception"));
            ResponseType.FORBIDDEN ->
                throw ConnectorException("You don't have the permissions to do that!");
            ResponseType.SYNTAX_ERROR ->
                throw ConnectorException("Syntax error!");
            else -> {}
        }
    }

}
