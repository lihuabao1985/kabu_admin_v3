package com.kabu.admin.user.repository.impl;

import com.kabu.admin.user.mapper.UserMapper;
import com.kabu.admin.user.model.User;
import com.kabu.admin.user.repository.UserRepository;
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
    public List<User> findByCriteria(String username, String email, Integer status, int limit, int offset) {
        return userMapper.findByCriteria(username, email, status, limit, offset);
    }

    @Override
    public long countByCriteria(String username, String email, Integer status) {
        return userMapper.countByCriteria(username, email, status);
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
    public int insert(User user) {
        return userMapper.insert(user);
    }

    @Override
    public int update(User user) {
        return userMapper.update(user);
    }

    @Override
    public int deleteById(Long id) {
        return userMapper.deleteById(id);
    }
}
