package de.byjoker.jfql.driver

import de.byjoker.jfql.connection.Connection
import de.byjoker.jfql.statement.ConditionSet
import de.byjoker.jfql.statement.InsertBuilder
import de.byjoker.jfql.statement.RemoveBuilder
import de.byjoker.jfql.statement.SelectBuilder
import de.byjoker.jfql.util.Response
import de.byjoker.jfql.util.Result
import de.byjoker.jfql.util.ResultType

class DynamicTableDriver(
    val connection: Connection,
    override val database: DatabaseDriver,
    override val name: String,
    override val primary: String
) : TableDriver {

    override val type: ResultType
        get() = (connection.query(SelectBuilder("*").from(name).build()) as Result).resultType

    override val entries: List<TableEntryDriver>
        get() = (connection.query(
            SelectBuilder("*").from(name).build()
        ) as Result).entries.map { tableEntry ->
            DynamicTableEntryDriver(
                connection,
                this,
                tableEntry.getString(primary)
            )
        }

    override val structure: List<String>
        get() = (connection.query(SelectBuilder("*").from(name).build()) as Result).structure

    override fun delete(): Response {
        return connection.query("delete table $name")
    }

    override fun removeEntry(identifier: String): Response {
        return connection.query(RemoveBuilder(identifier).from(name).build())
    }

    override fun removeEntriesWhere(conditionSet: ConditionSet): Response {
        return connection.query(RemoveBuilder("*").from(name).where(conditionSet).build())
    }

    override fun getEntry(identifier: String): TableEntryDriver? {
        val response = connection.query(SelectBuilder("*").from(name).primary(identifier).build())

        if (!response.isOk()) {
            return null
        }

        val result = response as Result

        if (result.entries.size != 1) {
            return null
        }

        return DynamicTableEntryDriver(connection, this, result.entries[0].getString(primary))
    }

    override fun getEntriesWhere(conditionSet: ConditionSet): List<TableEntryDriver> {
        return (connection.query(
            SelectBuilder("*").from(name).where(conditionSet).build()
        ) as Result).entries.map { tableEntry ->
            DynamicTableEntryDriver(
                connection,
                this,
                tableEntry.getString(primary)
            )
        }
    }

    override fun createEntry(content: Map<String, Any>): TableEntryDriver? {
        val response = when (type) {
            ResultType.DOCUMENT -> connection.query(InsertBuilder(name).content(content).build())
            else -> {
                val keys = StringBuilder("key")
                val values = StringBuilder("value")

                for (field in content.entries) {
                    keys.append(" ").append(field.key)
                    values.append(" ").append(field.value)
                }

                connection.query(InsertBuilder(name).keys(keys.toString()).values(values.toString()).build())
            }
        }

        if (!response.isOk()) {
            return null
        }

        return getEntry(content[primary].toString())
    }

}
