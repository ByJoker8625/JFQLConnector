package de.byjoker.jfql.statement

class QueryStatement(private val query: String) : Statement {
    override fun toQuery(): String {
        return query
    }
}
