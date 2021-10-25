package de.byjoker.jfql.util;

public class User {

    private final String name;
    private final String password;
    private final String database;

    public User(String name, String password, String database) {
        this.name = name;
        this.password = password;
        this.database = database;
    }

    public User(String name, String password) {
        this(name, password, "%STATIC_DATABASE%");
    }

    public boolean useDatabaseQuery() {
        return !database.equals("%STATIC_DATABASE%");
    }

    public String getDatabase() {
        return database;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }
}
