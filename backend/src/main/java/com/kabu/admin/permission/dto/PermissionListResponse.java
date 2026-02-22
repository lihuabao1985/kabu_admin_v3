package com.kabu.admin.permission.dto;

import java.util.List;

public record PermissionListResponse(
    List<PermissionResponse> items,
    long total,
    int page,
    int size
) {
}
