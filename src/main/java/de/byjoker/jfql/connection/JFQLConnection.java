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

public class JFQLConnection implements Connection {

    private final Gson gson;
    private String host;
    private URL url;
    private User user;

    public JFQLConnection(String host, User user) {
        this.host = host;
        this.user = user;
        this.url = null;
        this.gson = new Gson();
    }

    public JFQLConnection(String host) {
        this(host, null);
    }

    public JFQLConnection(User user) {
        this(null, user);
    }

    public JFQLConnection() {
        this(null, null);
    }

    @Override
    public void connect() {
        connect(host, user);
    }

    public void connect(String path) {
        connect(path, user);
    }

    public void connect(User user) {
        connect(host, user);
    }

    @Override
    public void connect(String host, User user) {
        try {
            this.host = formatHost(host);
            this.url = new URL(this.host);
            this.user = user;
        } catch (Exception ex) {
            throw new ConnectorException("Connection failed!");
        }

        exec("#connect", true);

        if (user.useDatabaseQuery()) try {
            query("use database '%'", user.getDatabase());
        } catch (Exception ex) {
            throw new ConnectorException(ex);
        }
    }

    private JSONObject exec(String exec, boolean exception) {
        if (!isConnected()) {
            throw new ConnectorException("Client isn't connected!");
        }

        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", user.getName());
        jsonObject.put("password", user.getPassword());
        jsonObject.put("query", exec);

        try {
            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            final OutputStream outputStream = connection.getOutputStream();
            outputStream.write(jsonObject.toString().getBytes(StandardCharsets.UTF_8));
            outputStream.close();

            final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            final StringBuilder builder = new StringBuilder();

            String read;

            while ((read = reader.readLine()) != null) {
                builder.append(read);
            }

            reader.close();
            connection.disconnect();

            final String build = builder.toString();

            if (!build.startsWith("{"))
                return null;
            else
                return new JSONObject(builder.toString());
        } catch (Exception ex) {
            if (exception)
                throw new ConnectorException(ex);
            else
                return null;
        }
    }

    @Override
    public Result query(String query) {
        return new JsonResult(exec(query, true), true);
    }

    @Override
    public Result query(String query, boolean exception) {
        return new JsonResult(exec(query, exception), exception);
    }

    @Override
    public Result query(String query, Object... replacers) {
        return new JsonResult(exec(formatQuery(query, replacers), true), true);
    }

    @Override
    public Result query(String query, boolean exception, Object... replacers) {
        return new JsonResult(exec(formatQuery(query, replacers), exception), exception);
    }

    @Override
    public String stringify(Object object) {
        return gson.toJson(object);
    }

    @Override
    public <T> T parse(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }

    private String formatHost(String host) {
        if (!host.startsWith("http://") && !host.startsWith("https://"))
            host = "myjfql:" + host;

        if (host.startsWith("myjfql:")) {
            host = "http://" + host.replace("myjfql:", "") + ":2291/query";
        }

        if (!host.startsWith("http://") && !host.startsWith("https://")) {
            host = "http://" + host;
        }

        return host;
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
    public void disconnect() {
        url = null;
    }

    @Override
    public boolean isConnected() {
        return url != null;
    }

    public User getUser() {
        return user;
    }

    public String getHost() {
        return host;
    }

}
