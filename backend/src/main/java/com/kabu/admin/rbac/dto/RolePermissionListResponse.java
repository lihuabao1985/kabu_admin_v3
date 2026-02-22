package com.kabu.admin.rbac.dto;

import java.util.List;

public record RolePermissionListResponse(List<Long> permissionIds) {
}
