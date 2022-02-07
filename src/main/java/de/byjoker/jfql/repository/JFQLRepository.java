package de.byjoker.jfql.repository;

import de.byjoker.jfql.statement.ConditionSet;
import de.byjoker.jfql.statement.SelectBuilder;
import de.byjoker.jfql.statement.Statement;
import de.byjoker.jfql.util.TableEntry;
import de.byjoker.jfql.util.Response;

import java.util.List;

public interface JFQLRepository<T> {

    T formatEntity(TableEntry entry);

    List<T> formatEntities(List<TableEntry> entries);

    Response query(Statement statement);

    void save(T entity);

    void saveAll(List<T> entities);

    void update(T entity);

    void delete(T entity);

    void deleteAll();

    void deleteAllBy(String field, Object value);

    void deleteAllWhere(String conditions);

    void deleteAllWhere(ConditionSet conditionSet);

    List<T> findAll();

    List<T> findAll(SelectBuilder statement);

    List<T> findAllBy(String field, Object value);

    List<T> findAllWhere(String conditions);

    List<T> findAllWhere(ConditionSet conditionSet);

    T findOneByPrimary(Object primary);

    T findOneBy(String field, Object value);

    T findOneWhere(String conditions);


}
