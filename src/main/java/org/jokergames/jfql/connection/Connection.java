package org.jokergames.jfql.connection;

import org.jokergames.jfql.exception.ConnectorException;
import org.jokergames.jfql.util.Result;
import org.jokergames.jfql.util.User;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author Janick
 */

public class Connection {

    private final String host;
    private URL url;
    private User user;

    public Connection(String host, User user) {
        this.host = host;
        this.user = user;
    }

    public Connection(String host) {
        this(host, null);
    }

    public Connection(User user) {
        this(null, user);
    }

    public Connection() {
        this(null, null);
    }

    public static String createURL(String address) {
        return createURL(address, 2291);
    }

    public static String createURL(String address, int port) {
        return "http://" + address + ":" + port + "/query";
    }

    public void connect() {
        connect(host, user);
    }

    public void connect(String path) {
        connect(path, user);
    }

    public void connect(User user) {
        connect(host, user);
    }

    public void connect(String host, User user) {
        try {
            this.url = new URL(formatHost(host));
            this.user = user;
        } catch (Exception ex) {
            throw new ConnectorException("Connection failed!");
        }

        exec("#connect", true);
    }

    private JSONObject exec(String exec, boolean exception) {
        if (!isConnected()) {
            throw new ConnectorException("Client isn't connected!");
        }

        JSONObject jsonObject = new JSONObject();

        {
            JSONObject auth = new JSONObject();
            auth.put("user", user.getName());
            auth.put("password", user.getPassword());
            jsonObject.put("auth", auth);
        }

        jsonObject.put("query", exec);

        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            final OutputStream outputStream = connection.getOutputStream();
            outputStream.write(jsonObject.toString().getBytes());
            outputStream.close();

            final InputStream inputStream = connection.getInputStream();
            final StringBuilder builder = new StringBuilder();

            int read;

            while ((read = inputStream.read()) != -1) {
                builder.append((char) read);
            }

            inputStream.close();
            connection.disconnect();

            final var build = builder.toString();

            if (build.isEmpty() || !build.startsWith("{"))
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

    private String formatHost(String host) {
        if (host.startsWith("myjfql:")) {
            host = "http://" + host.replace("myjfql:", "") + ":2291/query";
        }

        if (!host.startsWith("http://") && !host.startsWith("https://")) {
            host = "http://" + host;
        }

        return host;
    }

    public Result query(String query) {
        return new Result(exec(query, true));
    }

    public Result query(String query, boolean exception) {
        return new Result(exec(query, exception), exception);
    }

    public Result query(String query, Object... replacers) {
        for (Object replace : replacers) {
            if (replace == null)
                query = query.replaceFirst("%", "null");
            else
                query = query.replaceFirst("%", replace.toString());
        }

        return new Result(exec(query, true));
    }

    public Result query(String query, boolean exception, Object... replacers) {
        for (Object replace : replacers) {
            if (replace == null)
                query = query.replaceFirst("%", "null");
            else
                query = query.replaceFirst("%", replace.toString());
        }

        return new Result(exec(query, exception), exception);
    }

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
