package com.example.demo.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Repository
public class GenericCriteriaImpl implements GenericCriteriaRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public <T> List<T> find(Class<T> entityClass, List<SearchCriteria> criteria) {
        return find(entityClass, criteria, LogicalOperator.AND);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public <T> List<T> find(Class<T> entityClass, List<SearchCriteria> criteria, LogicalOperator operator) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(entityClass);
        Root<T> root = cq.from(entityClass);
        List<Predicate> predicates = new ArrayList<>();

        if (criteria == null || criteria.isEmpty()) {
            return em.createQuery(cq).getResultList();
        }

        for (SearchCriteria search : criteria) {
            Predicate predicate = buildPredicate(cb, root, search);
            if (predicate != null) {
                predicates.add(predicate);
            }
        }

        if (predicates.isEmpty()) {
            return em.createQuery(cq).getResultList();
        }

        if (operator == LogicalOperator.OR) {
            cq.where(cb.or(predicates.toArray(new Predicate[0])));
        } else {
            cq.where(cb.and(predicates.toArray(new Predicate[0])));
        }

        return em.createQuery(cq).getResultList();
    }

    @Override
    public <T> Optional<T> findOne(Class<T> entityClass, List<SearchCriteria> criteria) {
        return findOne(entityClass, criteria, LogicalOperator.AND);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private <T> Predicate buildPredicate(CriteriaBuilder cb, Root<T> root, SearchCriteria search) {


        if (search == null || search.field() == null || search.field().isBlank()) {
            return null;
        }

        switch (search.operator()) {
            case EQUAL_IGNORE_CASE -> {
                return cb.equal(cb.lower(root.get(search.field())), search.value());
            }
            case EQUAL -> {
                return cb.equal(root.get(search.field()), search.value());
            }
            case NOT_EQUAL -> {
                return cb.notEqual(root.get(search.field()), search.value());
            }
            case LIKE -> {
                Object value = search.value();
                return cb.like(root.get(search.field()), "%" + value + "%");
            }
            case GREATER_THAN -> {
                return cb.greaterThan(root.get(search.field()), (Comparable) search.value());
            }
            case LESS_THAN -> {
                return cb.lessThan(root.get(search.field()), (Comparable) search.value());
            }
            case GREATER_THAN_OR_EQUAL -> {
                return cb.greaterThanOrEqualTo(root.get(search.field()), (Comparable) search.value());
            }
            case LESS_THAN_OR_EQUAL -> {
                return cb.lessThanOrEqualTo(root.get(search.field()), (Comparable) search.value());
            }
            case IN -> {
                Object value = search.value();
                if (value instanceof Iterable<?> iterable) {
                    return root.get(search.field()).in(iterable);
                }
                if (value instanceof Object[] array) {
                    return root.get(search.field()).in(array);
                }
                return cb.conjunction();
            }
            case BETWEEN -> {
                if (search.value() instanceof List<?> values && values.size() >= 2) {
                    return cb.between(
                            root.get(search.field()),
                            (Comparable) values.get(0),
                            (Comparable) values.get(1)
                    );
                }
                return cb.conjunction();
            }
            case IS_NULL -> {
                return cb.isNull(root.get(search.field()));
            }
            case IS_NOT_NULL -> {
                return cb.isNotNull(root.get(search.field()));
            }
            default -> throw new IllegalArgumentException("Unexpected operator: " + search.operator());
        }
    }
}
