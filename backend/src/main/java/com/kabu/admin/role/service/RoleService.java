package com.kabu.admin.role.service;

import com.kabu.admin.role.dto.RoleCreateRequest;
import com.kabu.admin.role.dto.RoleListResponse;
import com.kabu.admin.role.dto.RoleQueryRequest;
import com.kabu.admin.role.dto.RoleResponse;
import com.kabu.admin.role.dto.RoleUpdateRequest;
import java.util.List;

public interface RoleService {

    RoleListResponse listRoles(RoleQueryRequest request);

    RoleResponse getRoleById(Long id);

    RoleResponse createRole(RoleCreateRequest request);

    RoleResponse updateRole(Long id, RoleUpdateRequest request);

    RoleResponse updateRoleStatus(Long id, Integer status);

    void deleteRole(Long id);

    List<Long> findExistingIds(List<Long> ids);
}
