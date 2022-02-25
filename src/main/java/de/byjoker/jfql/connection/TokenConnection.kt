package de.byjoker.jfql.connection

import de.byjoker.jfql.exception.ConnectorException
import de.byjoker.jfql.statement.QueryStatement
import de.byjoker.jfql.statement.Statement
import de.byjoker.jfql.util.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets


class TokenConnection(private var host: String, private val user: User) : Connection {

    private var token: String? = null

    private fun send(url: String, request: JSONObject): JSONObject {
        return try {
            val connection: HttpURLConnection = URL(url).openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.connectTimeout = 5000
            connection.readTimeout = 5000
            connection.doOutput = true

            val outputStream: OutputStream = connection.outputStream
            outputStream.write(request.toString().toByteArray(StandardCharsets.UTF_8))
            outputStream.close()

            val reader = BufferedReader(InputStreamReader(connection.inputStream, StandardCharsets.UTF_8))
            val builder = StringBuilder()

            var read: String?
            while (reader.readLine().also { read = it } != null) {
                builder.append(read)
            }

            reader.close()
            connection.disconnect()

            JSONObject(builder.toString())
        } catch (ex: Exception) {
            throw ConnectorException(ex)
        }
    }

    override fun connect() {
        if (!host.startsWith("http://") && !host.startsWith("https://"))
            host = "http://$host";

        try {
            token = send(
                "$host/api/v1/session/open",
                JSONObject().put("user", user.name).put("password", user.password)
            ).getJSONArray("result")?.getString(0)
        } catch (ex: Exception) {
            throw ConnectorException(ex)
        }
    }

    override fun closeSession(ignoreStaticSessions: Boolean) {
        if (user.name == "%TOKEN%" && ignoreStaticSessions) {
            disconnect()
            return
        }

        send("$host/api/v1/session/close", JSONObject().put("token", token))
        disconnect()
    }

    override fun disconnect() {
        token = null
    }

    override fun query(query: String, exception: Boolean): Response {
        return query(QueryStatement(query), exception)
    }

    override fun query(statement: Statement, exception: Boolean): Response {
        val response = SimpleResponse(
            send(
                "$host/api/v1/query",
                JSONObject().put("token", token).put("query", statement.toQuery())
            ), exception
        )

        return when (response.type) {
            ResponseType.ERROR -> ErrorResult(response.response, exception)
            ResponseType.RESULT -> Result(response.response, exception)
            else -> response
        }
    }

    override fun isConnected(): Boolean {
        return token == null
    }

}
