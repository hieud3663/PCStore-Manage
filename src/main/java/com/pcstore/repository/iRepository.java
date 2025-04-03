package com.pcstore.repository;

import com.pcstore.model.base.Entity;
import java.util.List;
import java.util.Optional;

/**
 * Interface chung cho tất cả repository
 * @param <T> Entity type
 * @param <ID> ID type
 */
public interface iRepository<T extends Entity, ID> {
    T save(T entity);
    Optional<T> findById(ID id);
    List<T> findAll();
    boolean existsById(ID id);
    void deleteById(ID id);
}