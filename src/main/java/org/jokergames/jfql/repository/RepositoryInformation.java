package org.jokergames.jfql.repository;

import org.jokergames.jfql.connection.Connection;

public class RepositoryInformation {

    private final Connection connection;
    private final String table;

    public RepositoryInformation(Connection connection, String table) {
        this.connection = connection;
        this.table = table;
    }

    public Connection getConnection() {
        return connection;
    }

    public String getTable() {
        return table;
    }
}
