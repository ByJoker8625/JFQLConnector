package de.byjoker.jfql.statement

import de.byjoker.jfql.exception.ConnectorException

class RemoveBuilder(private val field: String) {

    private var from: String? = null
    private var conditionSet: ConditionSet? = null

    fun from(from: String): RemoveBuilder {
        this.from = from
        return this
    }

    fun where(conditionSet: ConditionSet): RemoveBuilder {
        this.conditionSet = conditionSet
        return this
    }

    fun build(): Statement {
        val builder = StringBuilder("remove column ").append(field)

        if (from == null) {
            throw ConnectorException("Required filed isn't present!")
        }

        builder.append(" from ").append(from)

        if (conditionSet != null) {
            builder.append(" where ").append(conditionSet!!.conditions())
        }

        return Statement { builder.toString() }
    }

}
