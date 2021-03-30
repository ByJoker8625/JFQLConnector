package org.jokergames.jfql.connection;

import com.neovisionaries.ws.client.*;
import org.jokergames.jfql.encryption.Encryption;
import org.jokergames.jfql.encryption.NoneEncryption;
import org.jokergames.jfql.exception.ConnectorException;
import org.jokergames.jfql.util.Result;
import org.jokergames.jfql.util.User;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SocketConnection {

    private final String host;
    private final Random random;
    private final List<JSONObject> responses;
    private final Encryption encryption;

    private WebSocket webSocket;
    private User user;
    private boolean connectionAccepted;

    public SocketConnection(final String host, final User user) {
        this.random = new Random();
        this.host = host;
        this.user = user;
        this.responses = new ArrayList<>();
        this.encryption = new NoneEncryption();
        this.connectionAccepted = false;
    }

    public SocketConnection(final String host) {
        this(host, null);
    }

    public SocketConnection(final User user) {
        this(null, user);
    }

    public SocketConnection() {
        this(null, null);
    }

    public static String createURL(final String address) {
        return createURL(address, 2291);
    }

    public static String createURL(final String address, final int port) {
        return "ws://" + address + ":" + port + "/query";
    }

    public void connect() {
        connect(host, user);
    }

    public void connect(final String path) {
        connect(path, user);
    }

    public void connect(final User user) {
        connect(host, user);
    }

    private void connect(final String host, final User user, final int delay) {
        if (isConnected()) {
            throw new ConnectorException("Client is already connected!");
        }

        try {
            this.webSocket = new WebSocketFactory()
                    .setConnectionTimeout(delay)
                    .createSocket(formatHost(host))
                    .addListener(new WebSocketAdapter() {
                        @Override
                        public void onTextMessage(WebSocket websocket, String message) {
                            final JSONObject response = new JSONObject(message);

                            if (!connectionAccepted) {
                                if (!response.getString("type").equalsIgnoreCase("SUCCESS")) {
                                    throw new ConnectorException("Wrong user data!");
                                }

                                connectionAccepted = true;
                                return;
                            }

                            if (response.getString("id").equals("-1")) {
                                throw new ConnectorException("Unassigned response!");
                            }

                            responses.add(response);
                        }

                        @Override
                        public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
                            throw new ConnectorException("Server closed connection!");
                        }
                    })
                    .addExtension(WebSocketExtension.PERMESSAGE_DEFLATE)
                    .connect();

            {
                final JSONObject jsonObject = new JSONObject();
                jsonObject.put("user", user.getName());
                jsonObject.put("password", user.getPassword());
                jsonObject.put("id", "-1");
                webSocket.sendText(jsonObject.toString());
            }

            this.user = user;
        } catch (Exception ex) {
            throw new ConnectorException("Connection failed!");
        }

    }

    public void connect(final String host, final User user) {
        connect(host, user, 5000);
    }

    public void disconnect() {
        if (!isConnected()) {
            throw new ConnectorException("Client isn't connected!");
        }

        connectionAccepted = false;
        System.out.println(0);

        if (webSocket == null) {
            System.out.println(1);
            return;
        }

        if (!webSocket.isOpen()) {
            webSocket = null;
            System.out.println(2);
            return;
        }

        System.out.println(3);

        webSocket.sendClose();
        webSocket.disconnect();
    }

    private JSONObject exec(final String exec, final boolean exception) {
        if (!isConnected()) {
            throw new ConnectorException("Client isn't connected!");
        }


        try {
            final String id = generate();

            final JSONObject request = new JSONObject();
            request.put("id", id);
            request.put("query", exec);

            webSocket.sendText(request.toString());

            while (webSocket.isOpen()) {
                System.out.println("is open");
                if (responses.stream().filter(response -> response.getString("id").equals(id)).findFirst().orElse(null) != null)
                    break;
            }

            if (!webSocket.isOpen()) {
                this.reconnect();

                while (!connectionAccepted) {
                    System.out.println("[JFQLConnector] Reconnecting to '" + host + "'...");
                }

                return exec(exec, exception);
            }

            final JSONObject response = responses.stream().filter(resp -> resp.getString("id").equals(id)).findFirst().orElse(null);
            responses.removeIf(resp -> resp.getString("id").equals(id));

            return response;
        } catch (Exception ex) {
            if (exception)
                throw new ConnectorException(ex);
            else
                return null;
        }
    }

    private void reconnect() {
        disconnect();
        connect(host, user, 60 * 2 * 1000);
    }

    private String formatHost(final String host) {
        String formatted = host;

        if (host.startsWith("myjfql:")) {
            formatted = "ws://" + host.replace("myjfql:", "") + ":2291/query";
        }

        if (!host.startsWith("ws://") && !host.startsWith("wss://")) {
            formatted = "ws://" + host;
        }

        return formatted;
    }

    public Result query(final String query) {
        return new Result(encryption, exec(query, true));
    }

    public Result query(final String query, final boolean exception) {
        return new Result(encryption, exec(query, exception), exception);
    }

    public Result query(String query, final Object... replacers) {
        for (Object replace : replacers) {
            if (replace == null)
                query = query.replaceFirst("%", "null");
            else
                query = query.replaceFirst("%", replace.toString());
        }

        return new Result(encryption, exec(query, true));
    }

    public Result query(String query, final boolean exception, final Object... replacers) {
        for (Object replace : replacers) {
            if (replace == null)
                query = query.replaceFirst("%", "null");
            else
                query = query.replaceFirst("%", replace.toString());
        }

        return new Result(encryption, exec(query, exception), exception);
    }

    public boolean isConnected() {
        return connectionAccepted;
    }

    public Encryption getEncryption() {
        return encryption;
    }

    public User getUser() {
        return user;
    }

    public String getHost() {
        return host;
    }

    private String generate() {
        final StringBuilder generate = new StringBuilder();

        for (int i = 0; i < 14; i++) {
            generate.append(random.nextInt(9));
        }

        return generate.toString();
    }

}
