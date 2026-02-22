package com.kabu.admin.role.dto;

public record RoleQueryRequest(
    String roleCode,
    String roleName,
    Integer status,
    Integer page,
    Integer size
) {
}
