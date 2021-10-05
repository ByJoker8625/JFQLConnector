package org.jokergames.jfql.repository;

import org.jokergames.jfql.connection.Connection;
import org.jokergames.jfql.util.Column;

import java.util.*;
import java.util.stream.Collectors;

public abstract class JFQLRepositoryBuilder<T> implements JFQLRepository<T> {

    private final Connection connection;
    private final String table;

    private final RepositoryInformation information;

    public JFQLRepositoryBuilder(RepositoryInformation information) {
        this.connection = information.getConnection();
        this.table = information.getTable();
        this.information = information;
    }

    public void prepare(Class<? extends T> clazz) {
        StringBuilder query = new StringBuilder();
        query.append("create table ").append(table).append(" structure ");

        for (String field : getDatabaseFieldNames(clazz)) {
            query.append(field).append(" ");
        }

        query.append("primary-key ").append(getPrimaryKeyFieldName(clazz)).append("");

        connection.query(query.toString(), false);
    }

    @Override
    public List<T> formatEntities(List<Column> columns) {
        return columns.stream().map(this::formatEntity).collect(Collectors.toList());
    }

    @Override
    public void save(T entity) {
        final Map<String, String> fields = getDatabaseFieldValueMap(entity);
        final StringBuilder query = new StringBuilder();

        query.append("insert into ").append(table).append(" key ");

        for (String key : fields.keySet()) {
            query.append("'").append(key).append("'").append(" ");
        }

        query.append("value");

        for (String value : fields.values())
            query.append(" ").append(value);

        connection.query(query.toString());
    }

    @Override
    public void saveAll(List<T> entities) {
        entities.forEach(this::save);
    }


    @Override
    public void delete(T entity) {
        connection.query("remove column '%' from '%'", getPrimaryKeyFieldValue(entity), table);
    }

    @Override
    public void deleteAllBy(String field, Object value) {
        connection.query("remove column * from '%' where % = %", table, field, value);
    }

    @Override
    public void deleteAllWhere(String conditions) {
        connection.query("remove column * from '%' where %", table, conditions);
    }

    @Override
    public void deleteAll() {
        connection.query("remove column * from '%'", table);
    }

    @Override
    public List<T> findAll() {
        List<Column> columns = connection.query("select value * from %", table).getColumns();

        if (columns.size() == 0)
            return new ArrayList<>();

        return formatEntities(columns);
    }

    @Override
    public List<T> findAllBy(String field, Object value) {
        List<Column> columns = connection.query("select value * from % where % = %", table, field, value).getColumns();

        if (columns.size() == 0)
            return new ArrayList<>();

        return formatEntities(columns);
    }

    @Override
    public List<T> findAllWhere(String conditions) {
        List<Column> columns = connection.query("select value * from % where %", table, conditions).getColumns();

        if (columns.size() == 0)
            return new ArrayList<>();

        return formatEntities(columns);
    }

    @Override
    public T findOneByPrimary(String primary) {
        try {
            List<Column> columns = connection.query("select value * from % primary-key % limit 1", table, primary).getColumns();

            if (columns.size() == 0)
                return null;

            return formatEntity(columns.get(0));
        } catch (Exception ignore) {
        }

        return null;
    }

    @Override
    public T findOneBy(String field, Object value) {
        List<Column> columns = connection.query("select value * from % where % = %", table, field, value).getColumns();

        if (columns.size() == 0)
            return null;

        return formatEntity(columns.get(0));
    }

    @Override
    public T findOneWhere(String conditions) {
        List<Column> columns = connection.query("select value * from % where %", table, conditions).getColumns();

        if (columns.size() == 0)
            return null;

        return formatEntity(columns.get(0));
    }

    private List<DatabaseField> getDatabaseFields(Class<? extends T> clazz) {
        List<DatabaseField> databaseFields = Arrays.stream(clazz.getMethods()).filter(method -> method.isAnnotationPresent(DatabaseField.class)).map(method -> method.getAnnotation(DatabaseField.class)).collect(Collectors.toList());
        databaseFields.sort(Comparator.comparingInt(DatabaseField::position));

        return databaseFields;
    }

    private List<String> getDatabaseFieldNames(Class<? extends T> clazz) {
        return getDatabaseFields(clazz).stream().map(DatabaseField::field).collect(Collectors.toList());
    }

    private String getPrimaryKeyFieldName(Class<? extends T> clazz) {
        DatabaseField databaseField = getDatabaseFields(clazz).stream().filter(DatabaseField::primary).findFirst().orElse(null);

        if (databaseField == null)
            return getDatabaseFieldNames(clazz).get(0);

        return databaseField.field();
    }

    public String getPrimaryKeyFieldValue(T entity) {
        return getDatabaseFieldValueMap(entity).get(getPrimaryKeyFieldName((Class<? extends T>) entity.getClass()));
    }

    private Map<String, String> getDatabaseFieldValueMap(T entity) {
        Map<String, String> fields = new HashMap<>();

        Arrays.stream(entity.getClass().getMethods()).filter(method -> method.isAnnotationPresent(DatabaseField.class)).forEach(method -> {
            final DatabaseField databaseField = method.getAnnotation(DatabaseField.class);

            Object object = null;

            try {
                object = method.invoke(entity);

                if (databaseField.formatJSON())
                    object = connection.formatJSON(object);
            } catch (Exception ignore) {
            }

            String field = databaseField.field();

            if (databaseField.quotationMarks())
                fields.put(field, "'" + object + "'");
            else if (object != null)
                fields.put(field, object.toString());
            else
                fields.put(field, null);
        });

        return fields;
    }

    public RepositoryInformation getInformation() {
        return information;
    }
}
