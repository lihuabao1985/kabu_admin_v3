package com.kabu.admin.permission.repository.impl;

import com.kabu.admin.permission.mapper.PermissionMapper;
import com.kabu.admin.permission.model.Permission;
import com.kabu.admin.permission.repository.PermissionRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class PermissionRepositoryImpl implements PermissionRepository {

    private final PermissionMapper permissionMapper;

    public PermissionRepositoryImpl(PermissionMapper permissionMapper) {
        this.permissionMapper = permissionMapper;
    }

    @Override
    public List<Permission> findByCriteria(
        String permissionCode,
        String permissionName,
        String resourceType,
        Integer status,
        int limit,
        int offset
    ) {
        return permissionMapper.findByCriteria(permissionCode, permissionName, resourceType, status, limit, offset);
    }

    @Override
    public long countByCriteria(String permissionCode, String permissionName, String resourceType, Integer status) {
        return permissionMapper.countByCriteria(permissionCode, permissionName, resourceType, status);
    }

    @Override
    public Optional<Permission> findById(Long id) {
        return Optional.ofNullable(permissionMapper.findById(id));
    }

    @Override
    public Optional<Permission> findByPermissionCode(String permissionCode) {
        return Optional.ofNullable(permissionMapper.findByPermissionCode(permissionCode));
    }

    @Override
    public int insert(Permission permission) {
        return permissionMapper.insert(permission);
    }

    @Override
    public int update(Permission permission) {
        return permissionMapper.update(permission);
    }

    @Override
    public int updateStatus(Long id, Integer status, LocalDateTime updatedAt) {
        return permissionMapper.updateStatus(id, status, updatedAt);
    }

    @Override
    public int softDelete(Long id, LocalDateTime deletedAt, LocalDateTime updatedAt) {
        return permissionMapper.softDelete(id, deletedAt, updatedAt);
    }

    @Override
    public List<Long> findExistingEnabledIds(List<Long> ids) {
        return permissionMapper.findExistingEnabledIds(ids);
    }
}
