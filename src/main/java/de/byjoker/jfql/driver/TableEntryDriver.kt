package de.byjoker.jfql.driver

import de.byjoker.jfql.util.Response
import de.byjoker.jfql.util.TableEntry
import org.json.JSONObject

interface TableEntryDriver : TableEntry {
    val table: TableDriver
    fun insert(field: String, value: String?): Response
    fun insertContent(content: Any): Response = insertContent(JSONObject(content))
    fun insertContent(content: JSONObject): Response
    fun remove(field: String): Response = insert(field, "null")
    fun delete(): Response
    fun fetch(): TableEntry
}
