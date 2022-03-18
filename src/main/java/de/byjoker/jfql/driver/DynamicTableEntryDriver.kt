package de.byjoker.jfql.driver

import de.byjoker.jfql.connection.Connection
import de.byjoker.jfql.statement.InsertBuilder
import de.byjoker.jfql.statement.RemoveBuilder
import de.byjoker.jfql.statement.SelectBuilder
import de.byjoker.jfql.util.Response
import de.byjoker.jfql.util.Result
import de.byjoker.jfql.util.TableEntry
import org.json.JSONArray
import org.json.JSONObject

class DynamicTableEntryDriver(
    private val connection: Connection,
    override val table: TableDriver,
    private val identifier: String
) :
    TableEntryDriver {

    override fun insert(field: String, value: String?): Response {
        return connection.query(
            InsertBuilder(table.name).keys(field).values(value.toString()).primary(identifier).build()
        )
    }

    override fun insertContent(content: JSONObject): Response {
        return connection.query(InsertBuilder(table.name).content(content).primary(identifier).build())
    }

    override fun delete(): Response {
        return connection.query(RemoveBuilder(identifier).from(table.name).build())
    }

    override fun fetch(): TableEntry {
        return (connection.query(
            SelectBuilder("*").from(table.name).primary(identifier).build()
        ) as Result).entries[0]
    }

    override fun getObject(key: String): Any {
        return fetch().getObject(key)
    }

    override fun getRawString(key: String): String {
        return fetch().getRawString(key)
    }

    override fun getString(key: String): String {
        return fetch().getString(key)
    }

    override fun getInteger(key: String): Int {
        return fetch().getInteger(key)
    }

    override fun getLong(key: String): Long {
        return fetch().getLong(key)
    }

    override fun getFloat(key: String): Float {
        return fetch().getFloat(key)
    }

    override fun getDouble(key: String): Double {
        return fetch().getDouble(key)
    }

    override fun getShort(key: String): Short {
        return fetch().getShort(key)
    }

    override fun getBoolean(key: String): Boolean {
        return fetch().getBoolean(key)
    }

    override fun getJsonObject(key: String): JSONObject {
        return fetch().getJsonObject(key)
    }

    override fun getJsonArray(key: String): JSONArray {
        return fetch().getJsonArray(key)
    }

    override fun <T : Any> parse(key: String, clazz: Class<T>): T {
        return fetch().parse(key, clazz)
    }

    override fun isPresent(key: String): Boolean {
        return fetch().isPresent(key)
    }

    override fun isNull(key: String): Boolean {
        return fetch().isNull(key)
    }

    override fun getCreation(): Long {
        return fetch().creation
    }

}
