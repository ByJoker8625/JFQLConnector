package de.byjoker.jfql.driver

import de.byjoker.jfql.util.Response

interface DatabaseDriver {
    val name: String
    fun createTable(name: String, structure: List<String>, primary: String = structure[0]): TableDriver?
    fun getTable(name: String): TableDriver?
    fun deleteTable(name: String): Response
    val tables: List<String>
    fun delete(): Response
}
