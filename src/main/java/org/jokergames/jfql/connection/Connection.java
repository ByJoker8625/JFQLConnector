package org.jokergames.jfql.connection;

import org.jokergames.jfql.encryption.Encryption;
import org.jokergames.jfql.encryption.NoneEncryption;
import org.jokergames.jfql.exception.ConnectorException;
import org.jokergames.jfql.util.Result;
import org.jokergames.jfql.util.User;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Janick
 */

public class Connection {

    private final String host;
    private final Map<String, String> cache;
    private URL url;
    private User user;
    private Encryption encryption;

    public Connection(String host, User user, Encryption encryption) {
        this.host = host;
        this.user = user;
        this.encryption = encryption;
        this.cache = new HashMap<>();
    }

    public Connection(String host, User user) {
        this(host, user, new NoneEncryption());
    }

    public Connection(String host) {
        this(host, null, new NoneEncryption());
    }

    public Connection(User user) {
        this(null, user, new NoneEncryption());
    }

    public Connection(Encryption encryption) {
        this(null, null, encryption);
    }

    public Connection() {
        this(null, null, new NoneEncryption());
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

            if (cache.containsKey("user")) {
                auth.put("user", cache.get("user"));
            } else {
                String encrypted = encryption.getProtocol().encrypt(user.getName(), encryption.getKey());
                cache.put("user", encrypted);
                auth.put("user", encrypted);
            }

            if (cache.containsKey("password")) {
                auth.put("password", cache.get("password"));
            } else {
                String encrypted = encryption.getProtocol().encrypt(user.getPassword(), encryption.getKey());
                cache.put("password", encrypted);
                auth.put("password", encrypted);
            }

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

            System.out.println(build + " |0");

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
        if (!host.contains("?")) {
            if (host.startsWith("myjfql:")) {
                host = "http://" + host.replace("myjfql:", "") + ":2291/query";
            }

            if (!host.startsWith("http://") && !host.startsWith("https://")) {
                host = "http://" + host;
            }

            return host;
        }

        String[] strings = host.replace("?", "%").split("%");

        if (strings.length != 2) {
            return host;
        }

        return formatHost(strings[0]) + "?" + strings[1];
    }

    public Result query(String query) {
        return new Result(encryption, exec(query, true));
    }

    public Result query(String query, boolean exception) {
        return new Result(encryption, exec(query, exception), exception);
    }

    public Result query(String query, Object... replacers) {
        for (Object replace : replacers) {
            if (replace == null)
                query = query.replaceFirst("%", "null");
            else
                query = query.replaceFirst("%", replace.toString());
        }

        return new Result(encryption, exec(query, true));
    }

    public Result query(String query, boolean exception, Object... replacers) {
        for (Object replace : replacers) {
            if (replace == null)
                query = query.replaceFirst("%", "null");
            else
                query = query.replaceFirst("%", replace.toString());
        }

        return new Result(encryption, exec(query, exception), exception);
    }

    public boolean isConnected() {
        return url != null;
    }

    public Encryption getEncryption() {
        return encryption;
    }

    public void setEncryption(Encryption encryption) {
        this.encryption = encryption;
    }

    public User getUser() {
        return user;
    }

    public String getHost() {
        return host;
    }


}
