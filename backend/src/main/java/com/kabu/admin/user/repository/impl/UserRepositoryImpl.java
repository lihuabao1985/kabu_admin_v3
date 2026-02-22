package com.kabu.admin.user.repository.impl;

import com.kabu.admin.user.mapper.UserMapper;
import com.kabu.admin.user.model.User;
import com.kabu.admin.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final UserMapper userMapper;

    public UserRepositoryImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public List<User> findByCriteria(
        String username,
        String email,
        Integer status,
        Integer locked,
        String tenantId,
        int limit,
        int offset
    ) {
        return userMapper.findByCriteria(username, email, status, locked, tenantId, limit, offset);
    }

    @Override
    public long countByCriteria(String username, String email, Integer status, Integer locked, String tenantId) {
        return userMapper.countByCriteria(username, email, status, locked, tenantId);
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(userMapper.findById(id));
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return Optional.ofNullable(userMapper.findByUsername(username));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.ofNullable(userMapper.findByEmail(email));
    }

    @Override
    public Optional<User> findByPhone(String phone) {
        return Optional.ofNullable(userMapper.findByPhone(phone));
    }

    @Override
    public int insert(User user) {
        return userMapper.insert(user);
    }

    @Override
    public int update(User user) {
        return userMapper.update(user);
    }

    @Override
    public int updateStatus(Long id, Integer status, LocalDateTime updatedAt) {
        return userMapper.updateStatus(id, status, updatedAt);
    }

    @Override
    public int updateLock(Long id, Integer locked, LocalDateTime lockedAt, LocalDateTime updatedAt) {
        return userMapper.updateLock(id, locked, lockedAt, updatedAt);
    }

    @Override
    public int softDelete(Long id, LocalDateTime deletedAt, LocalDateTime updatedAt) {
        return userMapper.softDelete(id, deletedAt, updatedAt);
    }
}
