package org.jokergames.jfql.repository;

import org.jokergames.jfql.connection.Connection;
import org.jokergames.jfql.exception.RepositoryException;
import org.jokergames.jfql.util.Column;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class JFQLRepositoryBuilder<T> implements JFQLRepository<T> {

    private final Connection connection;
    private final Class<? extends T> clazz;
    private TableData tableData;

    protected JFQLRepositoryBuilder(Connection connection, Class<? extends T> clazz) {
        this.connection = connection;
        this.tableData = null;
        this.clazz = clazz;
    }

    public void build() {
        if (!clazz.isAnnotationPresent(DatabaseTable.class))
            throw new RepositoryException(clazz.getName() + " isn't annotated with @DatabaseTable!");


        final DatabaseTable table = clazz.getAnnotation(DatabaseTable.class);
        final List<ColumnData> columns = getDatabaseColumns();

        final ColumnData primaryColumn = columns.stream().filter(ColumnData::isPrimary).findFirst().orElse(null);

        if (table.primary().equals("%") && primaryColumn == null)
            throw new RepositoryException("No primary-key found in " + clazz.getName() + "!");

        final String name = (!table.name().equals("%")) ? table.name() : clazz.getName();
        final String primary = (!table.primary().equals("%")) ? table.primary() : primaryColumn.getName();
        final String structure = (!table.structure().equals("%")) ? table.structure() : columns.stream().map(ColumnData::getName)
                .collect(Collectors.toList()).toString().replace("[", "").replace("]", "").replace(", ", " ");

        connection.query("create table " + name + " structure " + structure + " primary-key " + primary, false);
        tableData = new TableData(name, primary, structure);
    }

    private List<ColumnData> getDatabaseColumns(T entity) {
        final List<ColumnData> columns = new ArrayList<>();

        Arrays.stream(entity.getClass().getDeclaredFields()).filter(field -> field.isAnnotationPresent(DatabaseColumn.class)).collect(Collectors.toList()).forEach(field -> {
            final DatabaseColumn column = field.getAnnotation(DatabaseColumn.class);
            field.setAccessible(true);

            String name = column.name().equals("%") ? field.getName() : column.name();
            boolean primary = column.primary();
            boolean json = column.json();
            String content;

            try {
                if (json) {
                    content = connection.formatJSON(field.get(entity));
                } else {
                    Object o = field.get(entity);

                    if (o == null)
                        content = null;
                    else
                        content = o.toString();
                }
            } catch (Exception ex) {
                content = null;
            }

            columns.add(new ColumnData(name, content, json, primary));
        });

        return columns;
    }

    private List<ColumnData> getDatabaseColumns() {
        final List<ColumnData> columns = new ArrayList<>();

        Arrays.stream(clazz.getDeclaredFields()).filter(field1 -> field1.isAnnotationPresent(DatabaseColumn.class)).forEach(field -> {
            final DatabaseColumn column = field.getAnnotation(DatabaseColumn.class);
            columns.add(new ColumnData((column.name().equals("%")) ? field.getName() : column.name(), null, column.primary(), column.json()));
        });

        return columns;
    }

    @Override
    public List<T> formatEntities(List<Column> columns) {
        return columns.stream().map(this::formatEntity).collect(Collectors.toList());
    }

    @Override
    public void save(T entity) {
        final List<ColumnData> columns = getDatabaseColumns(entity);
        final StringBuilder builder = new StringBuilder().append("insert into ").append(tableData.getName()).append(" key");
        final StringBuilder values = new StringBuilder();

        for (ColumnData column : columns) {
            builder.append(" ").append(column.getName());
            values.append(" '").append(column.getContent()).append("'");
        }

        builder.append(" value").append(values);

        connection.query(builder.toString());
    }

    @Override
    public void saveAll(List<T> entities) {
        entities.forEach(this::save);
    }

    @Override
    public void delete(T entity) {
        connection.query("remove column '" + Objects.requireNonNull(getDatabaseColumns(entity).stream().filter(column -> column.getName().equals(tableData.getPrimary())).findFirst().orElse(null)).getContent() + "' from " + tableData.getName());
    }

    @Override
    public void deleteAllBy(String field, Object value) {
        connection.query("remove column * from " + tableData.getName() + " where " + field + " = " + value.toString());
    }

    @Override
    public void deleteAllWhere(String conditions) {
        connection.query("remove column * from " + tableData.getName() + " where " + conditions);
    }

    @Override
    public void deleteAll() {
        connection.query("remove column * from " + tableData.getName());
    }

    @Override
    public List<T> findAll() {
        return formatEntities(connection.query("select value * from " + tableData.getName()).getColumns());
    }

    @Override
    public List<T> findAllBy(String field, Object value) {
        return formatEntities(connection.query("select value * from " + tableData.getName() + " where " + field + " = " + value.toString()).getColumns());
    }

    @Override
    public List<T> findAllWhere(String conditions) {
        return formatEntities(connection.query("select value * from " + tableData.getName() + " where " + conditions).getColumns());
    }

    @Override
    public T findOneByPrimary(Object primary) {
        try {
            return formatEntity(connection.query("select value * from " + tableData.getName() + " limit 1 primary-key " + primary).getColumns().get(0));
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public T findOneBy(String field, Object value) {
        try {
            return formatEntity(connection.query("select value * from " + tableData.getName() + " limit 1 where " + field + " = " + value.toString()).getColumns().get(0));
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public T findOneWhere(String conditions) {
        try {
            return formatEntity(connection.query("select value * from " + tableData.getName() + " limit 1 where" + conditions).getColumns().get(0));
        } catch (Exception ex) {
            return null;
        }
    }

    public Class<? extends T> getClazz() {
        return clazz;
    }

    public TableData getTableData() {
        return tableData;
    }

    public Connection getConnection() {
        return connection;
    }
}
