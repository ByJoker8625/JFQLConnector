package de.byjoker.jfql.statement

import de.byjoker.jfql.exception.ConnectorException
import de.byjoker.jfql.util.Order

class SelectBuilder(private val fields: String) {

    private var from: String? = null
    private var conditionSet: ConditionSet? = null
    private var limit: Int? = null
    private var order: Order? = null
    private var sort: String? = null

    fun from(from: String): SelectBuilder {
        this.from = from
        return this
    }

    fun where(conditionSet: ConditionSet): SelectBuilder {
        this.conditionSet = conditionSet
        return this
    }

    fun limit(limit: Int): SelectBuilder {
        this.limit = limit
        return this
    }

    fun sort(sort: String): SelectBuilder {
        this.sort = sort
        return this
    }

    fun order(order: Order): SelectBuilder {
        this.order = order
        return this
    }

    fun build(): Statement {
        if (from == null) {
            throw ConnectorException("Required filed isn't present!")
        }

        val builder = StringBuilder("select value ").append(fields).append(" from ").append(from)

        if (conditionSet != null) {
            builder.append(" where ").append(conditionSet!!.conditions())
        }

        if (limit != null) {
            builder.append(" limit ").append(limit)
        }

        if (order != null) {
            builder.append(" order ").append(order)
        }

        if (sort != null) {
            builder.append(" sort ").append(sort)
        }

        return Statement { builder.toString() }
    }

}
