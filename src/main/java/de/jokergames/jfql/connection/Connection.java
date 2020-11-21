package de.jokergames.jfql.connection;

import de.jokergames.jfql.exception.ConnectorException;
import de.jokergames.jfql.util.Result;
import de.jokergames.jfql.util.User;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author Janick
 */

public class Connection {

    private URL url;
    private String path;
    private User user;

    public Connection(String path, User user) {
        this.path = path;
        this.user = user;
    }

    public Connection(String path) {
        this(path, null);
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
        connect(path, user);
    }

    public void connect(String path) {
        connect(path, user);
    }

    public void connect(User user) {
        connect(path, user);
    }

    public void connect(String path, User user) {
        try {
            this.url = new URL(path);
            this.user = user;
        } catch (Exception ex) {
            throw new ConnectorException("Connection failed!");
        }

        final JSONObject object = exec("#connect", false);

        if (object == null)
            throw new ConnectorException("Connection deny!");
    }

    public void disconnect() {
        this.url = null;
        this.path = null;
        this.user = null;
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

            return new JSONObject(builder.toString());
        } catch (Exception ex) {
            if (exception)
                throw new ConnectorException(ex);
            else
                return null;
        }
    }

    public Result query(String query, String... replacers) {
        for (String replace : replacers) {
            query = query.replaceFirst("%", replace);
        }

        return new Result(exec(query, true));
    }

    public Result query(String query, boolean exception, String... replacers) {
        for (String replace : replacers) {
            query = query.replaceFirst("%", replace);
        }

        return new Result(exec(query, exception), exception);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isConnected() {
        return url != null && user != null;
    }

    public URL getUrl() {
        return url;
    }
}
