package com.example.demo.repository;

import com.example.demo.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AuthRepository {

    private final GenericCriteriaRepository repo;

    public Optional<User> findByUsername(String username){
        return repo.findOne(User.class, List.of(new SearchCriteria("username", Operator.EQUAL, username)));
    }

    public Optional<User> findByEmail(String email){
        return repo.findOne(User.class, List.of(new SearchCriteria("email", Operator.EQUAL, email)));
    }

    public Optional<User> findByUsernameOrEmail(String usernameOrEmail){
        return repo.findOne(User.class, List.of(SearchCriteria.eq("username", usernameOrEmail), SearchCriteria.eq("email", usernameOrEmail)), LogicalOperator.OR);
    }

    public Optional<User> findByPhone(String phone){
        return repo.findOne(User.class, List.of(new SearchCriteria("phone", Operator.EQUAL, phone)));
    }


}
