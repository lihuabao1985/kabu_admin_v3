package com.kabu.admin.role.repository.impl;

import com.kabu.admin.role.mapper.RoleMapper;
import com.kabu.admin.role.model.Role;
import com.kabu.admin.role.repository.RoleRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class RoleRepositoryImpl implements RoleRepository {

    private final RoleMapper roleMapper;

    public RoleRepositoryImpl(RoleMapper roleMapper) {
        this.roleMapper = roleMapper;
    }

    @Override
    public List<Role> findByCriteria(String roleCode, String roleName, Integer status, int limit, int offset) {
        return roleMapper.findByCriteria(roleCode, roleName, status, limit, offset);
    }

    @Override
    public long countByCriteria(String roleCode, String roleName, Integer status) {
        return roleMapper.countByCriteria(roleCode, roleName, status);
    }

    @Override
    public Optional<Role> findById(Long id) {
        return Optional.ofNullable(roleMapper.findById(id));
    }

    @Override
    public Optional<Role> findByRoleCode(String roleCode) {
        return Optional.ofNullable(roleMapper.findByRoleCode(roleCode));
    }

    @Override
    public int insert(Role role) {
        return roleMapper.insert(role);
    }

    @Override
    public int update(Role role) {
        return roleMapper.update(role);
    }

    @Override
    public int updateStatus(Long id, Integer status, LocalDateTime updatedAt) {
        return roleMapper.updateStatus(id, status, updatedAt);
    }

    @Override
    public int softDelete(Long id, LocalDateTime deletedAt, LocalDateTime updatedAt) {
        return roleMapper.softDelete(id, deletedAt, updatedAt);
    }

    @Override
    public List<Long> findExistingIds(List<Long> ids) {
        return roleMapper.findExistingIds(ids);
    }
}
