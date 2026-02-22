package com.kabu.admin.user.repository;

import com.kabu.admin.user.model.User;
import java.util.List;
import java.util.Optional;

public interface UserRepository {

    List<User> findByCriteria(String username, String email, Integer status, int limit, int offset);

    long countByCriteria(String username, String email, Integer status);

    Optional<User> findById(Long id);

    Optional<User> findByUsername(String username);

    int insert(User user);

    int update(User user);

    int deleteById(Long id);
}
