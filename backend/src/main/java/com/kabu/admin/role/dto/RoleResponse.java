package com.kabu.admin.role.dto;

import java.time.LocalDateTime;

public record RoleResponse(
    Long id,
    String roleCode,
    String roleName,
    String description,
    Integer status,
    Integer isSystem,
    Integer sortOrder,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
