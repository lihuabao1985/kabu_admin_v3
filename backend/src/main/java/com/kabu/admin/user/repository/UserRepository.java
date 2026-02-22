package com.kabu.admin.user.repository;

import com.kabu.admin.user.model.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository {

    List<User> findByCriteria(
        String username,
        String email,
        Integer status,
        Integer locked,
        String tenantId,
        int limit,
        int offset
    );

    long countByCriteria(String username, String email, Integer status, Integer locked, String tenantId);

    Optional<User> findById(Long id);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByPhone(String phone);

    int insert(User user);

    int update(User user);

    int updateStatus(Long id, Integer status, LocalDateTime updatedAt);

    int updateLock(Long id, Integer locked, LocalDateTime lockedAt, LocalDateTime updatedAt);

    int softDelete(Long id, LocalDateTime deletedAt, LocalDateTime updatedAt);
}
