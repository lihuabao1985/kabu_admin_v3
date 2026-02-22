package com.kabu.admin.permission.dto;

public record PermissionQueryRequest(
    String permissionCode,
    String permissionName,
    String resourceType,
    Integer status,
    Integer page,
    Integer size
) {
}
