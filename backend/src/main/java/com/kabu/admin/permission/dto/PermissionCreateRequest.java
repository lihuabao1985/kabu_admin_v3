package com.kabu.admin.permission.dto;

public record PermissionCreateRequest(
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
    String uiRoute
) {
}
