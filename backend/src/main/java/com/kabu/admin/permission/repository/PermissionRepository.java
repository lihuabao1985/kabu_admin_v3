package com.kabu.admin.permission.repository;

import com.kabu.admin.permission.model.Permission;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PermissionRepository {

    List<Permission> findByCriteria(
        String permissionCode,
        String permissionName,
        String resourceType,
        Integer status,
        int limit,
        int offset
    );

    long countByCriteria(String permissionCode, String permissionName, String resourceType, Integer status);

    Optional<Permission> findById(Long id);

    Optional<Permission> findByPermissionCode(String permissionCode);

    int insert(Permission permission);

    int update(Permission permission);

    int updateStatus(Long id, Integer status, LocalDateTime updatedAt);

    int softDelete(Long id, LocalDateTime deletedAt, LocalDateTime updatedAt);

    List<Long> findExistingEnabledIds(List<Long> ids);
}
