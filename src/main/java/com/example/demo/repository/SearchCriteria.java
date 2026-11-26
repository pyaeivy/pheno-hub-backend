package com.example.demo.repository;

import java.util.Collection;
import java.util.List;

public record SearchCriteria(String field, Operator operator, Object value) {

    public static SearchCriteria eqIgnoreCase(String field, Object value){
        return new SearchCriteria(field, Operator.EQUAL_IGNORE_CASE, value);
    }

    public static SearchCriteria eq(String field, Object value) {
        return new SearchCriteria(field, Operator.EQUAL, value);
    }

    public static SearchCriteria notEq(String field, Object value) {
        return new SearchCriteria(field, Operator.NOT_EQUAL, value);
    }

    public static SearchCriteria like(String field, String value) {
        return new SearchCriteria(field, Operator.LIKE, value);
    }

    public static SearchCriteria gt(String field, Comparable<?> value) {
        return new SearchCriteria(field, Operator.GREATER_THAN, value);
    }

    public static SearchCriteria lt(String field, Comparable<?> value) {
        return new SearchCriteria(field, Operator.LESS_THAN, value);
    }

    public static SearchCriteria gte(String field, Comparable<?> value) {
        return new SearchCriteria(field, Operator.GREATER_THAN_OR_EQUAL, value);
    }

    public static SearchCriteria lte(String field, Comparable<?> value) {
        return new SearchCriteria(field, Operator.LESS_THAN_OR_EQUAL, value);
    }

    public static SearchCriteria in(String field, Collection<?> values) {
        return new SearchCriteria(field, Operator.IN, values);
    }

    public static SearchCriteria between(String field, Comparable<?> start, Comparable<?> end) {
        return new SearchCriteria(field, Operator.BETWEEN, List.of(start, end));
    }

    public static SearchCriteria isNull(String field) {
        return new SearchCriteria(field, Operator.IS_NULL, null);
    }

    public static SearchCriteria isNotNull(String field) {
        return new SearchCriteria(field, Operator.IS_NOT_NULL, null);
    }
}
