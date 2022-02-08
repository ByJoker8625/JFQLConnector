package de.byjoker.jfql.repository

import de.byjoker.jfql.statement.ConditionSet
import de.byjoker.jfql.statement.SelectBuilder
import de.byjoker.jfql.statement.Statement
import de.byjoker.jfql.util.Response
import de.byjoker.jfql.util.TableEntry

interface Repository<T> {

    fun formatEntity(entry: TableEntry): T
    fun formatEntities(entries: MutableList<TableEntry>): MutableList<T>
    fun query(statement: Statement): Response
    fun save(entity: T)
    fun saveAll(entities: MutableList<T>)
    fun update(entity: T)
    fun updateBy(entity: T, field: String, value: Any)
    fun updateWhere(entity: T, conditions: String)
    fun updateWhere(entity: T, conditionSet: ConditionSet)
    fun delete(entity: T)
    fun deleteAll()
    fun deleteAllBy(field: String, value: Any)
    fun deleteAllWhere(conditions: String)
    fun deleteAllWhere(conditionSet: ConditionSet)
    fun findAll(): MutableList<T>
    fun findAll(statement: SelectBuilder): MutableList<T>
    fun findAllBy(field: String, value: Any): MutableList<T>
    fun findAllWhere(conditions: String): MutableList<T>
    fun findAllWhere(conditionSet: ConditionSet): MutableList<T>
    fun findOneByPrimary(primary: Any): T?
    fun findOneBy(field: String, value: Any): T?
    fun findOneWhere(conditions: String): T?
    fun findOneWhere(conditionSet: ConditionSet): T?
}
