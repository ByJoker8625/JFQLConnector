package de.jokergames.connector.connection;

import de.jokergames.connector.util.Result;
import de.jokergames.connector.util.User;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Janick
 */

public class Connection {

    private final URL url;
    private final User user;

    public Connection(String url, String user, String password) throws MalformedURLException {
        this.url = new URL(url);
        this.user = new User(user, password);
    }

    public Connection(String url, User user) throws MalformedURLException {
        this.url = new URL(url);
        this.user = user;
    }

    private JSONObject exec(String exec) {
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
            throw new RuntimeException(ex);
        }
    }

    public Result query(String query) {
        return new Result(exec(query));
    }

    public User getUser() {
        return user;
    }

    public URL getUrl() {
        return url;
    }
}
