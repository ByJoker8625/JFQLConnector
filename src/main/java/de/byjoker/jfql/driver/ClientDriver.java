package de.byjoker.jfql.driver;

import de.byjoker.jfql.connection.Connection;
import de.byjoker.jfql.util.Response;

import java.util.List;

public interface ClientDriver {

    DatabaseDriver getDatabase(String name);

    Response deleteDatabase(String name);

    List<String> getDatabases();

    Connection getConnection();

}
