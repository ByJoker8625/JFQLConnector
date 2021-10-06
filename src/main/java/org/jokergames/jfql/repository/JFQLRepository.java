package org.jokergames.jfql.repository;

import org.jokergames.jfql.util.Column;

import java.util.List;

public interface JFQLRepository<T> {

    T formatEntity(Column column);

    List<T> formatEntities(List<Column> columns);

    void save(T entity);

    void saveAll(List<T> entities);

    void delete(T entity);

    void deleteAllBy(String field, Object value);

    void deleteAllWhere(String conditions);

    void deleteAll();

    List<T> findAll();

    List<T> findAllBy(String field, Object value);

    List<T> findAllWhere(String conditions);

    T findOneByPrimary(Object primary);

    T findOneBy(String field, Object value);

    T findOneWhere(String conditions);

}
