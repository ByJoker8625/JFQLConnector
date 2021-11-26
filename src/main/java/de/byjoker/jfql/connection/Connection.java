package de.byjoker.jfql.connection;

import de.byjoker.jfql.util.Result;
import de.byjoker.jfql.util.User;

public interface Connection {

    void connect();

    void connect(String host, User user);

    boolean isConnected();

    void disconnect();

    Result query(String query);

    Result query(String query, boolean exceptions);

    Result query(String query, Object... replacers);

    Result query(String query, boolean exception, Object... replacers);

    String stringify(Object object);

    <T> T parse(String json, Class<T> clazz);

}
