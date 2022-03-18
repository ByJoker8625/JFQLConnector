package de.byjoker.jfql.driver

import de.byjoker.jfql.statement.ConditionSet
import de.byjoker.jfql.util.Response
import de.byjoker.jfql.util.ResultType

interface TableDriver {
    val primary: String
    val name: String
    val type: ResultType
    val entries: List<TableEntryDriver>
    val structure: List<String>
    val database: DatabaseDriver
    fun delete(): Response
    fun removeEntry(identifier: String): Response
    fun removeEntriesWhere(conditionSet: ConditionSet): Response
    fun getEntry(identifier: String): TableEntryDriver?
    fun getEntriesWhere(conditionSet: ConditionSet): List<TableEntryDriver>
    fun createEntry(content: Map<String, Any>): TableEntryDriver?
}
