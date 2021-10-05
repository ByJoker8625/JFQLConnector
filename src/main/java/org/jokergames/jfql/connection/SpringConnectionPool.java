package org.jokergames.jfql.connection;

import org.jokergames.jfql.util.User;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.Properties;

public class SpringConnectionPool {

    private static SpringConnectionPool instance = null;
    private final Connection connection;

    public SpringConnectionPool() throws IOException {
        final ClassPathResource resource = new ClassPathResource("/application.properties");
        final Properties props = PropertiesLoaderUtils.loadProperties(resource);

        connection = new Connection(props.getProperty("org.jokergames.jfql.connection.host"), new User(props.getProperty("org.jokergames.jfql.connection.user"), props.getProperty("org.jokergames.jfql.connection.password")));
        connection.connect();

        String database = props.getProperty("org.jokergames.jfql.connection.database");

        if (!database.equals("%STATIC_DATABASE%"))
            connection.query("use database '%'", false, new Object[]{database});

        instance = this;
    }

    public static SpringConnectionPool getInstance() {
        if (instance == null) {
            try {
                return new SpringConnectionPool();
            } catch (IOException e) {
                return null;
            }
        }

        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}
