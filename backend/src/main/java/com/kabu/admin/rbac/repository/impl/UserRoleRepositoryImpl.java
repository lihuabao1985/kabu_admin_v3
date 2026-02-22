package com.kabu.admin.rbac.repository.impl;

import com.kabu.admin.rbac.mapper.UserRoleMapper;
import com.kabu.admin.rbac.repository.UserRoleRepository;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class UserRoleRepositoryImpl implements UserRoleRepository {

    private final UserRoleMapper userRoleMapper;

    public UserRoleRepositoryImpl(UserRoleMapper userRoleMapper) {
        this.userRoleMapper = userRoleMapper;
    }

    @Override
    public List<Long> findRoleIdsByUserId(Long userId) {
        return userRoleMapper.findRoleIdsByUserId(userId);
    }

    @Override
    public List<Long> findUserIdsByRoleId(Long roleId) {
        return userRoleMapper.findUserIdsByRoleId(roleId);
    }

    @Override
    public int insertIgnoreBatch(Long userId, List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        return userRoleMapper.insertIgnoreBatch(userId, ids, LocalDateTime.now(ZoneOffset.UTC));
    }

    @Override
    public int deleteByUserIdAndRoleIds(Long userId, List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        return userRoleMapper.deleteByUserIdAndRoleIds(userId, ids);
    }

    @Override
    public int deleteByUserId(Long userId) {
        return userRoleMapper.deleteByUserId(userId);
    }
}
