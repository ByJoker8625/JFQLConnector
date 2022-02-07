package de.byjoker.jfql.statement

class ConditionSetBuilder(condition: Condition) {

    private val builder: StringBuilder = StringBuilder()

    init {
        builder.append(condition.condition())
    }

    fun and(condition: Condition): ConditionSetBuilder {
        builder.append(" and ").append(condition.condition())
        return this
    }

    fun or(condition: Condition): ConditionSetBuilder {
        builder.append(" or ").append(condition.condition())
        return this
    }

    fun build(): ConditionSet {
        return ConditionSet { builder.toString() }
    }

}
