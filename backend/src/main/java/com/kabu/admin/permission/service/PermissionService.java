package com.kabu.admin.permission.service;

import com.kabu.admin.permission.dto.PermissionCreateRequest;
import com.kabu.admin.permission.dto.PermissionListResponse;
import com.kabu.admin.permission.dto.PermissionQueryRequest;
import com.kabu.admin.permission.dto.PermissionResponse;
import com.kabu.admin.permission.dto.PermissionUpdateRequest;
import java.util.List;

public interface PermissionService {

    PermissionListResponse listPermissions(PermissionQueryRequest request);

    PermissionResponse getPermissionById(Long id);

    PermissionResponse createPermission(PermissionCreateRequest request);

    PermissionResponse updatePermission(Long id, PermissionUpdateRequest request);

    PermissionResponse updatePermissionStatus(Long id, Integer status);

    void deletePermission(Long id);

    List<Long> findExistingEnabledIds(List<Long> ids);
}
