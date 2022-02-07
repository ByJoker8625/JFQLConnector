package de.byjoker.jfql.statement

import de.byjoker.jfql.exception.ConnectorException

class ConditionBuilder(private val field: String) {

    private var state: State? = null
    private var method: Method? = null
    private var needed: Any? = null

    fun `is`(): ConditionBuilder {
        this.state = State.IS
        return this
    }

    fun not(): ConditionBuilder {
        this.state = State.NOT
        return this
    }

    fun equals(needed: Any): ConditionBuilder {
        this.method = Method.EQUALS
        this.needed = needed
        return this
    }

    fun equalsIgnoreCase(needed: Any): ConditionBuilder {
        this.method = Method.EQUALS_IGNORE_CASE
        this.needed = needed
        return this
    }

    fun contains(needed: Any): ConditionBuilder {
        this.method = Method.CONTAINS
        this.needed = needed
        return this
    }

    fun containsIgnoreCase(needed: Any): ConditionBuilder {
        this.method = Method.CONTAINS_IGNORE_CASE
        this.needed = needed
        return this
    }

    fun needed(needed: Any): ConditionBuilder {
        this.needed = needed
        return this
    }

    fun build(): Condition {
        if (state == null || method == null || needed == null) {
            throw ConnectorException("Required filed isn't present!")
        }

        val builder = StringBuilder(field).append(" ")

        when (method) {
            Method.EQUALS -> if (state == State.NOT) builder.append("!== ") else builder.append("=== ")
            else -> {
                if (state == State.NOT) {
                    builder.append("!=")
                } else {
                    builder.append("==")
                }

                builder.append(" ").append(method!!.method)
            }
        }

        builder.append("'").append(needed).append("'")

        return Condition { builder.toString() }
    }

    enum class State {
        IS, NOT
    }

    enum class Method(val method: String) {
        EQUALS("equals:"), EQUALS_IGNORE_CASE("equals_ignore_case:"), CONTAINS("contains:"), CONTAINS_IGNORE_CASE("contains_ignore_case:")
    }

}
