package com.kabu.admin.rbac.repository.impl;

import com.kabu.admin.rbac.mapper.RolePermissionMapper;
import com.kabu.admin.rbac.repository.RolePermissionRepository;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class RolePermissionRepositoryImpl implements RolePermissionRepository {

    private final RolePermissionMapper rolePermissionMapper;

    public RolePermissionRepositoryImpl(RolePermissionMapper rolePermissionMapper) {
        this.rolePermissionMapper = rolePermissionMapper;
    }

    @Override
    public List<Long> findPermissionIdsByRoleId(Long roleId) {
        return rolePermissionMapper.findPermissionIdsByRoleId(roleId);
    }

    @Override
    public int insertIgnoreBatch(Long roleId, List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        return rolePermissionMapper.insertIgnoreBatch(roleId, ids, LocalDateTime.now(ZoneOffset.UTC));
    }

    @Override
    public int deleteByRoleIdAndPermissionIds(Long roleId, List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        return rolePermissionMapper.deleteByRoleIdAndPermissionIds(roleId, ids);
    }

    @Override
    public int deleteByRoleId(Long roleId) {
        return rolePermissionMapper.deleteByRoleId(roleId);
    }
}
