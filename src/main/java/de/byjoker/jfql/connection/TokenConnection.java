package de.byjoker.jfql.connection;

import com.google.gson.Gson;
import de.byjoker.jfql.exception.ConnectorException;
import de.byjoker.jfql.util.JsonResult;
import de.byjoker.jfql.util.Result;
import de.byjoker.jfql.util.User;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class TokenConnection implements Connection {

    private final Gson gson = new Gson();

    private String host;
    private User user;
    private String token;

    public TokenConnection(String host, User user) {
        this.host = host;
        this.user = user;
        this.token = null;
    }

    public TokenConnection() {
        this(null, null);
    }

    private JSONObject send(String url, JSONObject request) {
        try {
            final HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();

            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            final OutputStream outputStream = connection.getOutputStream();
            outputStream.write(request.toString().getBytes(StandardCharsets.UTF_8));
            outputStream.close();

            final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            final StringBuilder builder = new StringBuilder();

            String read;

            while ((read = reader.readLine()) != null) {
                builder.append(read);
            }

            reader.close();
            connection.disconnect();

            return new JSONObject(builder.toString());
        } catch (Exception ex) {
            throw new ConnectorException(ex);
        }
    }

    @Override
    public void connect() {
        if (host.startsWith("myjfql:"))
            host = host.replace("myjfql:", "http://");


        if (!host.startsWith("http://") && !host.startsWith("https://"))
            host = "http://" + host;

        try {
            token = send(host + "/api/v1/session/open", new JSONObject().put("user", user.getName()).put("password", user.getPassword())).getJSONArray("result").getString(0);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ConnectorException("Connection failed: " + ex.getMessage());
        }

        if (user.useDatabaseQuery())
            query("use database '%'", user.getDatabase());
    }

    @Override
    public void connect(String host, User user) {
        this.host = host;
        this.user = user;

        connect();
    }

    @Override
    public boolean isConnected() {
        return token != null;
    }

    @Override
    public void disconnect() {
        if (!isConnected()) {
            throw new ConnectorException("Client isn't connected!");
        }

        send(host + "/api/v1/session/close", new JSONObject().put("token", token));
        token = null;
    }

    @Override
    public Result query(String query) {
        if (!isConnected()) {
            throw new ConnectorException("Client isn't connected!");
        }

        return new JsonResult(send(host + "/api/v1/query", new JSONObject().put("token", token).put("query", query)), true);
    }

    @Override
    public Result query(String query, boolean exceptions) {
        if (!isConnected()) {
            throw new ConnectorException("Client isn't connected!");
        }

        return new JsonResult(send(host + "/api/v1/query", new JSONObject().put("token", token).put("query", query)), exceptions);
    }

    @Override
    public Result query(String query, Object... replacers) {
        if (!isConnected()) {
            throw new ConnectorException("Client isn't connected!");
        }

        return new JsonResult(send(host + "/api/v1/query", new JSONObject().put("token", token).put("query", formatQuery(query, replacers))), true);
    }

    @Override
    public Result query(String query, boolean exception, Object... replacers) {
        if (!isConnected()) {
            throw new ConnectorException("Client isn't connected!");
        }

        return new JsonResult(send(host + "/api/v1/query", new JSONObject().put("token", token).put("query", formatQuery(query, replacers))), exception);
    }

    private String formatQuery(String query, Object... replacers) {
        for (Object replace : replacers) {
            if (replace == null)
                query = query.replaceFirst("%", "null");
            else {
                query = query.replaceFirst("%", replace.toString());
            }
        }

        return query;
    }

    @Override
    public String stringify(Object object) {
        return gson.toJson(object);
    }

    @Override
    public <T> T parse(String json, Class<T> clazz) {
        return gson.fromJson(token, clazz);
    }

    public String getToken() {
        return token;
    }

    public String getHost() {
        return host;
    }

    public User getUser() {
        return user;
    }
}
