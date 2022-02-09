package de.byjoker.jfql.connection

import de.byjoker.jfql.statement.Statement
import de.byjoker.jfql.util.Response

interface Connection {
    fun connect()
    fun disconnect()
    fun closeSession(ignoreStaticSessions: Boolean = true)
    fun query(query: String, exception: Boolean = true): Response
    fun query(statement: Statement, exception: Boolean = true): Response
    fun isConnected(): Boolean
}
