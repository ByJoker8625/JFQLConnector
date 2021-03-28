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
    private WebSocket webSocket;
    private User user;
    private Encryption encryption;
    private boolean connectionAccepted;

    public SocketConnection(String host, User user) {
        this.random = new Random();
        this.host = host;
        this.user = user;
        this.responses = new ArrayList<>();
        this.encryption = new NoneEncryption();
        this.connectionAccepted = false;
    }

    public SocketConnection(String host) {
        this(host, null);
    }

    public SocketConnection(User user) {
        this(null, user);
    }

    public SocketConnection() {
        this(null, null);
    }

    public static String createURL(String address) {
        return createURL(address, 2291);
    }

    public static String createURL(String address, int port) {
        return "ws://" + address + ":" + port + "/query";
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
            this.webSocket = new WebSocketFactory()
                    .setConnectionTimeout(5000)
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

                            if (response.getInt("id") == -1) {
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
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("user", user.getName());
                jsonObject.put("password", user.getPassword());
                jsonObject.put("id", -1);
                webSocket.sendText(jsonObject.toString());
            }

            this.user = user;
        } catch (Exception ex) {
            throw new ConnectorException("Connection failed!");
        }

    }

    public void disconnect() {
        if (!isConnectionAccepted()) {
            throw new ConnectorException("Client isn't connected!");
        }

        webSocket.sendClose();
    }

    private JSONObject exec(String exec, boolean exception) {
        if (!isConnectionAccepted()) {
            throw new ConnectorException("Client isn't connected!");
        }

        try {
            final int id = generate();

            JSONObject request = new JSONObject();
            request.put("id", id);
            request.put("query", exec);

            webSocket.sendText(request.toString());

            while (responses.stream().filter(response -> response.getInt("id") == id).findFirst().orElse(null) == null)
                ;

            final JSONObject response = responses.stream().filter(resp -> resp.getInt("id") == id).findFirst().orElse(null);
            responses.removeIf(resp -> resp.getInt("id") == id);

            return response;
        } catch (Exception ex) {
            ex.printStackTrace();

            if (exception)
                throw new ConnectorException(ex);
            else
                return null;
        }
    }

    private String formatHost(String host) {
        if (!host.contains("?")) {
            if (host.startsWith("myjfql:")) {
                host = "ws://" + host.replace("myjfql:", "") + ":2291/wsq";
            }

            if (!host.startsWith("ws://") && !host.startsWith("wss://")) {
                host = "ws://" + host;
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

    public boolean isConnectionAccepted() {
        return webSocket != null;
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

    private int generate() {
        StringBuilder generate = new StringBuilder();

        for (int i = 0; i < 4; i++) {
            generate.append(random.nextInt(9));
        }

        return Integer.parseInt(generate.toString());
    }

}
