package com.kabu.admin.role.dto;

import java.util.List;

public record RoleListResponse(
    List<RoleResponse> items,
    long total,
    int page,
    int size
) {
}
