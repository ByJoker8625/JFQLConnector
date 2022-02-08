package de.byjoker.jfql.repository

import de.byjoker.jfql.connection.Connection
import de.byjoker.jfql.statement.*
import de.byjoker.jfql.util.DocumentCollectionEntry
import de.byjoker.jfql.util.Response
import de.byjoker.jfql.util.Result
import de.byjoker.jfql.util.TableEntry
import org.json.JSONObject

class DocumentRepository(private val connection: Connection, private val name: String) : Repository<JSONObject> {

    private val primary = "_id"

    override fun formatEntity(entry: TableEntry): JSONObject {
        return (entry as DocumentCollectionEntry).parseJsonObject()
    }

    override fun formatEntities(entries: MutableList<TableEntry>): MutableList<JSONObject> {
        return entries.map { tableEntry -> formatEntity(tableEntry) }.toMutableList()
    }

    override fun query(statement: Statement): Response {
        return connection.query(statement)
    }

    override fun save(entity: JSONObject) {
        connection.query(InsertBuilder(name).content(entity).build())
    }

    override fun saveAll(entities: MutableList<JSONObject>) {
        entities.forEach { entity -> save(entity) }
    }

    override fun update(entity: JSONObject) {
        connection.query(InsertBuilder(name).content(entity).primary(entity.getString(primary)).build())
    }

    override fun updateBy(entity: JSONObject, field: String, value: Any) {
        updateWhere(entity, ConditionSetBuilder(ConditionBuilder(field).`is`().equals(value).build()).build())
    }

    override fun updateWhere(entity: JSONObject, conditions: String) {
        updateWhere(entity, LegacyConditionSetBuilder(conditions).build())
    }

    override fun updateWhere(entity: JSONObject, conditionSet: ConditionSet) {
        connection.query(InsertBuilder(name).content(entity).where(conditionSet).build())
    }

    override fun delete(entity: JSONObject) {
        connection.query(RemoveBuilder(entity.getString("_id")).from(name).build())
    }

    override fun deleteAll() {
        connection.query(RemoveBuilder("*").from(name).build())
    }

    override fun deleteAllBy(field: String, value: Any) {
        deleteAllWhere(ConditionSetBuilder(ConditionBuilder(field).`is`().equals(value).build()).build())
    }

    override fun deleteAllWhere(conditions: String) {
        deleteAllWhere(LegacyConditionSetBuilder(conditions).build())
    }

    override fun deleteAllWhere(conditionSet: ConditionSet) {
        connection.query(RemoveBuilder("*").from(name).where(conditionSet).build())
    }

    override fun findAll(): MutableList<JSONObject> {
        return (connection.query(SelectBuilder("*").from(name).build()) as Result).entries.map { entry ->
            formatEntity(
                entry
            )
        }.toMutableList()
    }

    override fun findAll(statement: SelectBuilder): MutableList<JSONObject> {
        return (connection.query(statement.build()) as Result).entries.map { entry ->
            formatEntity(
                entry
            )
        }.toMutableList()
    }

    override fun findAllBy(field: String, value: Any): MutableList<JSONObject> {
        return findAllWhere(ConditionSetBuilder(ConditionBuilder(field).`is`().equals(value).build()).build())
    }

    override fun findAllWhere(conditions: String): MutableList<JSONObject> {
        return findAllWhere(LegacyConditionSetBuilder(conditions).build())
    }

    override fun findAllWhere(conditionSet: ConditionSet): MutableList<JSONObject> {
        return (connection.query(
            SelectBuilder("*").from(name).where(conditionSet).build()
        ) as Result).entries.map { entry ->
            formatEntity(
                entry
            )
        }.toMutableList()
    }

    override fun findOneByPrimary(primary: Any): JSONObject? {
        return (connection.query(
            SelectBuilder("*").from(name).primary(primary.toString()).build()
        ) as Result).entries.map { entry ->
            formatEntity(
                entry
            )
        }.firstOrNull()
    }

    override fun findOneBy(field: String, value: Any): JSONObject? {
        return findOneWhere(ConditionSetBuilder(ConditionBuilder(field).`is`().equals(value).build()).build())
    }

    override fun findOneWhere(conditions: String): JSONObject? {
        return findOneWhere(LegacyConditionSetBuilder(conditions).build())
    }

    override fun findOneWhere(conditionSet: ConditionSet): JSONObject? {
        return (connection.query(
            SelectBuilder("*").from(name).where(conditionSet).build()
        ) as Result).entries.map { entry ->
            formatEntity(
                entry
            )
        }.firstOrNull()
    }

}
