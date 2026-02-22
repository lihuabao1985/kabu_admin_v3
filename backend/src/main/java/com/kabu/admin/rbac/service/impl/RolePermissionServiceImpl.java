package com.kabu.admin.rbac.service.impl;

import com.kabu.admin.permission.service.PermissionService;
import com.kabu.admin.rbac.dto.RolePermissionListResponse;
import com.kabu.admin.rbac.repository.RolePermissionRepository;
import com.kabu.admin.rbac.service.RolePermissionService;
import com.kabu.admin.role.service.RoleService;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RolePermissionServiceImpl implements RolePermissionService {

    private final RoleService roleService;
    private final PermissionService permissionService;
    private final RolePermissionRepository rolePermissionRepository;

    public RolePermissionServiceImpl(
        RoleService roleService,
        PermissionService permissionService,
        RolePermissionRepository rolePermissionRepository
    ) {
        this.roleService = roleService;
        this.permissionService = permissionService;
        this.rolePermissionRepository = rolePermissionRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public RolePermissionListResponse listRolePermissions(Long roleId) {
        validateRoleExists(roleId);
        return new RolePermissionListResponse(rolePermissionRepository.findPermissionIdsByRoleId(roleId));
    }

    @Override
    @Transactional
    public RolePermissionListResponse replaceRolePermissions(Long roleId, List<Long> permissionIds) {
        validateRoleExists(roleId);
        List<Long> normalizedPermissionIds = normalizeIdList(permissionIds);
        validatePermissionIds(normalizedPermissionIds);
        rolePermissionRepository.deleteByRoleId(roleId);
        rolePermissionRepository.insertIgnoreBatch(roleId, normalizedPermissionIds);
        return listRolePermissions(roleId);
    }

    @Override
    @Transactional
    public RolePermissionListResponse addRolePermissions(Long roleId, List<Long> permissionIds) {
        validateRoleExists(roleId);
        List<Long> normalizedPermissionIds = normalizeIdList(permissionIds);
        validatePermissionIds(normalizedPermissionIds);
        rolePermissionRepository.insertIgnoreBatch(roleId, normalizedPermissionIds);
        return listRolePermissions(roleId);
    }

    @Override
    @Transactional
    public RolePermissionListResponse removeRolePermissions(Long roleId, List<Long> permissionIds) {
        validateRoleExists(roleId);
        List<Long> normalizedPermissionIds = normalizeIdList(permissionIds);
        rolePermissionRepository.deleteByRoleIdAndPermissionIds(roleId, normalizedPermissionIds);
        return listRolePermissions(roleId);
    }

    private void validateRoleExists(Long roleId) {
        if (roleId == null || roleId < 1) {
            throw new IllegalArgumentException("角色ID必须为正整数");
        }
        roleService.getRoleById(roleId);
    }

    private void validatePermissionIds(List<Long> permissionIds) {
        if (permissionIds.isEmpty()) {
            return;
        }
        List<Long> existingIds = permissionService.findExistingEnabledIds(permissionIds);
        if (existingIds.size() != permissionIds.size()) {
            throw new IllegalArgumentException("部分权限ID不存在或已停用");
        }
    }

    private List<Long> normalizeIdList(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        Set<Long> uniqueIds = new TreeSet<>();
        for (Long id : ids) {
            if (id == null || id < 1) {
                throw new IllegalArgumentException("所有ID必须为正整数");
            }
            uniqueIds.add(id);
        }
        return new ArrayList<>(uniqueIds);
    }
}
