package de.byjoker.jfql.driver

import de.byjoker.jfql.connection.Connection
import de.byjoker.jfql.util.Response
import de.byjoker.jfql.util.Result

class DynamicDatabaseDriver(val connection: Connection, override val name: String) : DatabaseDriver {

    override fun createTable(name: String, structure: List<String>, primary: String): TableDriver? {
        return null
    }

    override fun getTable(name: String): TableDriver? {
        val response = connection.query("structure of $name")

        if (!response.isOk()) {
            return null
        }

        val result = response as Result

        if (result.entries.size == 0) {
            return null
        }

        return DynamicTableDriver(
            connection,
            this,
            name,
            result.entries.first { tableEntry -> tableEntry.getString("type") == "PRIMARY_KEY" }.getString("name")
        )
    }

    override fun deleteTable(name: String): Response {
        return connection.query("delete table $name")
    }

    override val tables: List<String>
        get() = (connection.query("list tables") as Result).entries.map { tableEntry -> tableEntry.getString("name") }

    override fun delete(): Response {
        return connection.query("delete database $name")
    }

}
