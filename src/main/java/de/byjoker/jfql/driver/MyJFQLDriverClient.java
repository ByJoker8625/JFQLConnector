package de.byjoker.jfql.driver;

import de.byjoker.jfql.connection.Connection;
import de.byjoker.jfql.connection.TokenConnection;
import de.byjoker.jfql.exception.ConnectorException;
import de.byjoker.jfql.util.Response;
import de.byjoker.jfql.util.Result;
import de.byjoker.jfql.util.Token;
import de.byjoker.jfql.util.User;

import java.util.List;
import java.util.stream.Collectors;

public class MyJFQLDriverClient implements ClientDriver {

    private final Connection connection;

    public MyJFQLDriverClient(Connection connection) {
        this.connection = connection;
    }

    public MyJFQLDriverClient(String url) throws ConnectorException {
        final String[] arguments = url.split("@");
        final String host = arguments[1];

        User user;

        if (arguments[0].contains(":")) {
            user = new User(arguments[0].split(":")[0], arguments[0].split(":")[1]);
        } else {
            user = new Token(arguments[0]);
        }

        connection = new TokenConnection(host, user);
        connection.connect();
    }

    @Override
    public DatabaseDriver getDatabase(String name) {
        final Response response = connection.query("list databases", true);

        if (!response.isOk()) {
            return null;
        }

        final Result result = (Result) response;

        if (result.getEntries().stream().noneMatch(tableEntry -> tableEntry.getString("name").equals(name))) {
            return null;
        }

        return new DynamicDatabaseDriver(connection, name);
    }

    @Override
    public Response deleteDatabase(String name) {
        return connection.query("delete database " + name, true);
    }

    @Override
    public List<String> getDatabases() {
        return ((Result) connection.query("list databases", true)).getEntries().stream().map(tableEntry -> tableEntry.getString("name")).collect(Collectors.toList());
    }

    @Override
    public Connection getConnection() {
        return connection;
    }
}
