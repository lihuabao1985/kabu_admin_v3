package com.kabu.admin.permission.mapper;

import com.kabu.admin.permission.model.Permission;
import java.time.LocalDateTime;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PermissionMapper {

    List<Permission> findByCriteria(
        @Param("permissionCode") String permissionCode,
        @Param("permissionName") String permissionName,
        @Param("resourceType") String resourceType,
        @Param("status") Integer status,
        @Param("limit") int limit,
        @Param("offset") int offset
    );

    long countByCriteria(
        @Param("permissionCode") String permissionCode,
        @Param("permissionName") String permissionName,
        @Param("resourceType") String resourceType,
        @Param("status") Integer status
    );

    Permission findById(@Param("id") Long id);

    Permission findByPermissionCode(@Param("permissionCode") String permissionCode);

    int insert(Permission permission);

    int update(Permission permission);

    int updateStatus(
        @Param("id") Long id,
        @Param("status") Integer status,
        @Param("updatedAt") LocalDateTime updatedAt
    );

    int softDelete(
        @Param("id") Long id,
        @Param("deletedAt") LocalDateTime deletedAt,
        @Param("updatedAt") LocalDateTime updatedAt
    );

    List<Long> findExistingEnabledIds(@Param("ids") List<Long> ids);
}
