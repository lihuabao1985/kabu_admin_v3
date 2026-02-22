package com.kabu.admin.role.dto;

public record RoleCreateRequest(
    String roleCode,
    String roleName,
    String description,
    Integer status,
    Integer isSystem,
    Integer sortOrder
) {
}
