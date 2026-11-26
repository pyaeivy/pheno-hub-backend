package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

public interface GenericCriteriaRepository {

    default <T> boolean exists(Class<T> entityClass, List<SearchCriteria> criteria) {
        return findOne(entityClass, criteria).isPresent();
    }

    <T> List<T> find(Class<T> entityClass, List<SearchCriteria> criteria);

    <T> List<T> find(Class<T> entityClass, List<SearchCriteria> criteria, LogicalOperator operator);

    <T> Optional<T> findOne(Class<T> entityClass, List<SearchCriteria> criteria);

    default <T> Optional<T> findOne(Class<T> entityClass, List<SearchCriteria> criteria, LogicalOperator operator) {
        List<T> results = find(entityClass, criteria, operator);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }
}
