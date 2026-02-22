package com.kabu.admin.rbac.service;

import com.kabu.admin.rbac.dto.RolePermissionListResponse;
import java.util.List;

public interface RolePermissionService {

    RolePermissionListResponse listRolePermissions(Long roleId);

    RolePermissionListResponse replaceRolePermissions(Long roleId, List<Long> permissionIds);

    RolePermissionListResponse addRolePermissions(Long roleId, List<Long> permissionIds);

    RolePermissionListResponse removeRolePermissions(Long roleId, List<Long> permissionIds);
}
