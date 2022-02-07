package de.byjoker.jfql.util

import org.json.JSONObject

interface Response {
    val type: ResponseType
    val response: JSONObject
}
