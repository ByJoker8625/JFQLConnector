package de.byjoker.jfql.statement

import com.google.gson.Gson
import de.byjoker.jfql.exception.ConnectorException
import org.json.JSONObject

class InsertBuilder(private val into: String) {

    private var content: String? = null
    private var keys: String? = null
    private var values: String? = null
    private var primary: String? = null
    private var conditionSet: ConditionSet? = null
    private var fully: Boolean = false

    fun content(json: JSONObject): InsertBuilder {
        this.content = json.toString()
        return this
    }

    fun content(`object`: Any): InsertBuilder {
        this.content = Gson().toJson(`object`)
        return this
    }

    fun keys(keys: String): InsertBuilder {
        this.keys = keys
        return this
    }

    fun values(values: String): InsertBuilder {
        this.values = values
        return this
    }

    fun where(conditionSet: ConditionSet): InsertBuilder {
        this.conditionSet = conditionSet
        return this
    }

    fun primary(primary: String): InsertBuilder {
        this.primary = primary
        return this
    }

    fun fully(): InsertBuilder {
        this.fully = true
        return this
    }

    fun build(): Statement {
        val builder = StringBuilder("insert into ").append(into)

        if (content != null) {
            builder.append(" content ").append(content)
        } else if (keys != null && values != null) {
            builder.append(" key ").append(keys).append(" value ").append(values)
        } else {
            throw ConnectorException("Required filed isn't present!")
        }

        if (primary != null) {
            builder.append(" primary-key ").append(primary)
        } else if (conditionSet != null) {
            builder.append(" where ").append(conditionSet!!.conditions())
        }

        if (fully) {
            builder.append(" fully")
        }

        return Statement { builder.toString() }
    }


}
