package com.kabu.admin.permission.dto;

import java.time.LocalDateTime;

public record PermissionResponse(
    Long id,
    String permissionCode,
    String permissionName,
    String description,
    Integer status,
    String resourceType,
    String resource,
    String httpMethod,
    String action,
    String permissionGroup,
    Integer sortOrder,
    String uiMenuKey,
    String uiRoute,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
